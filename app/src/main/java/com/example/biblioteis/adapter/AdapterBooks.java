package com.example.biblioteis.adapter;

import static android.view.View.VISIBLE;

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
import com.example.biblioteis.API.models.User;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.API.repository.ImageRepository;
import com.example.biblioteis.DB.models.UserFavBookModel;
import com.example.biblioteis.DB.repositoryDB.UserRepositoryAssetHelper;
import com.example.biblioteis.R;
import com.example.biblioteis.detallesActivity.DetallesActivity;
import com.example.biblioteis.provider.UserProvider;

import java.util.List;

import okhttp3.ResponseBody;

public class AdapterBooks extends RecyclerView.Adapter{
    public static final String BOOK_ID = "bookId";
    List<Book> books;
    ImageRepository imageRepository;
    UserRepositoryAssetHelper userRepositoryAssetHelper;
    UserFavBookModel userFavBooks;

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView txtNombre, txtAutor;
        ImageView imgLibro, imgDisponible, imgHeart;
        Book selectedBook;



        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtAutor = itemView.findViewById(R.id.txtAutor);
            imgLibro = itemView.findViewById(R.id.imgLibro);
            imgDisponible = itemView.findViewById(R.id.imgDisponible);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(view->{
                selectedBook = books.get(getAdapterPosition());
                return false;
            });
            User usuario = UserProvider.getInstance();
            if (usuario.getName() != null){
                userRepositoryAssetHelper = new UserRepositoryAssetHelper(itemView.getContext());
                userFavBooks = userRepositoryAssetHelper.getUserFavBooks(usuario.getId());
            }

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(v.getContext() instanceof Activity){
                ((Activity) v.getContext()).getMenuInflater().inflate(R.menu.menucontextual, menu);

                MenuItem itemCodigo = menu.findItem(R.id.itemCodigo);
                itemCodigo.setVisible(false);

                MenuItem itemFavorito = menu.findItem(R.id.itemFavorito);
                itemFavorito.setVisible(true);

                boolean isFavorito = userFavBooks != null && userFavBooks.getBook_ids() != null && userFavBooks.getBook_ids().contains(selectedBook.getId());
                if(isFavorito){
                    itemFavorito.setTitle("Eliminar favorito");
                }else{
                    itemFavorito.setTitle("Marcar favorito");
                }

                itemFavorito.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        if(UserProvider.getInstance().getName() == null){
                            Toast.makeText(itemView.getContext(), "Es necesario estar logueado para agregar a favoritos", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if(isFavorito){
                            eliminarFavorito(selectedBook.getId());
                            userFavBooks.removeBook(selectedBook.getId());
                        }else{
                            agregarFavorito(selectedBook.getId());
                            userFavBooks.addBook(selectedBook.getId());
                        }

                        return false;
                    }
                });
            }
        }

        private void agregarFavorito(int bookId){
            userRepositoryAssetHelper.insertUserFavBook(UserProvider.getInstance().getId(), bookId);
            int position = getAdapterPosition();
            notifyItemChanged(position);
        }

        private void eliminarFavorito(int bookId){
            userRepositoryAssetHelper.deleteFavBook(UserProvider.getInstance().getId(), bookId);
            int position = getAdapterPosition();
            notifyItemChanged(position);
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

        if(userFavBooks != null){
            List<Integer> favoriteBooks = userFavBooks.getBook_ids();
            for (Integer favBookId : favoriteBooks){
                if(favBookId == book.getId()){
                    viewHolder.imgHeart.setImageResource(R.drawable.heart);
                    viewHolder.imgHeart.setVisibility(VISIBLE);
                }
            }
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
