package com.example.biblioteis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.API.repository.ImageRepository;

import java.util.List;

import okhttp3.ResponseBody;

public class AdapterBooks extends RecyclerView.Adapter {

    public static final String BOOK_ID = "bookId";
    List<Book> books;
    ImageRepository imageRepository;

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
        imageRepository.getImage(imageName, new BookRepository.ApiCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                if(result != null){
                    Bitmap bitmap = BitmapFactory.decodeStream(result.byteStream());
                    viewHolder.imgLibro.setImageBitmap(bitmap);
                }

            }
            @Override
            public void onFailure(Throwable t) {

            }
        });
        if(book.isAvailable()){
            viewHolder.imgDisponible.setImageResource(R.drawable.color_verde);
        }else{
            viewHolder.imgDisponible.setImageResource(R.drawable.color_rosa);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserProvider.getInstance().getName() != null){
                    Intent intent = new Intent(v.getContext(), DetallesActivity.class);
                    intent.putExtra(BOOK_ID,book.getId());
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
