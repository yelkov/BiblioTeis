package com.example.biblioteis.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.API.repository.ImageRepository;
import com.example.biblioteis.R;
import com.example.biblioteis.ui.detalles.DetallesActivity;

import java.util.List;

import okhttp3.ResponseBody;

public class AdapterBooks extends RecyclerView.Adapter{
    public static final String BOOK_ID = "bookId";
    List<Book> books;
    ImageRepository imageRepository;

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView txtNombre, txtAutor;
        ImageView imgLibro, imgDisponible;
        Book selectedBook;


        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtAutor = itemView.findViewById(R.id.txtAutor);
            imgLibro = itemView.findViewById(R.id.imgLibro);
            imgDisponible = itemView.findViewById(R.id.imgDisponible);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(view->{
                selectedBook = books.get(getAdapterPosition());
                return false;
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(v.getContext() instanceof Activity){
                ((Activity) v.getContext()).getMenuInflater().inflate(R.menu.menucontextual, menu);
                MenuItem itemCodigo = menu.findItem(R.id.itemCodigo);
                itemCodigo.setVisible(false);
                MenuItem itemFavorito = menu.findItem(R.id.itemFavorito);
                itemFavorito.setVisible(true);

                //No sé si lo siguiente es una mala praxis, pero no se me ocurre ningún modo de pasar el id del libro seleccionado para que funcione el metodo desde el onContextItemSelected de Main
                itemFavorito.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        Toast.makeText(v.getContext(), "Id de libro es: " + selectedBook.getId(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
            }
        }
    }

    public AdapterBooks(List<Book> books){
        this.books = books;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        imageRepository = new ImageRepository();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_book_card,parent,false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CardViewHolder viewHolder = (CardViewHolder) holder;
        Book book = books.get(position);
        viewHolder.txtNombre.setText(book.getTitle());
        viewHolder.txtAutor.setText(book.getAuthor());

        String imageName = book.getBookPicture();
        askForBookImage(imageName, viewHolder);

        if(book.isAvailable()){
            viewHolder.imgDisponible.setImageResource(R.drawable.ok);
        }else{
            viewHolder.imgDisponible.setImageResource(R.drawable.x);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetallesActivity.class);
                    intent.putExtra(BOOK_ID,book.getId());
                    v.getContext().startActivity(intent);
            }
        });

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_dark));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.secondary_light));
        }
    }

    private void askForBookImage(String imageName, CardViewHolder viewHolder) {
        imageRepository.getImage(imageName, new BookRepository.ApiCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                if(result != null){
                    Bitmap bitmap = BitmapFactory.decodeStream(result.byteStream());
                    viewHolder.imgLibro.setImageBitmap(bitmap);
                }else{
                    viewHolder.imgLibro.setImageResource(R.drawable.cover);
                }

            }
            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(viewHolder.itemView.getContext(), "Error al solicitar imagen de libro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
