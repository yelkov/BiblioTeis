package com.example.biblioteis;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteis.API.models.Book;

import java.util.List;

public class AdapterBooks extends RecyclerView.Adapter {

    public static final String BOOK_ID = "bookId";
    List<Book> books;

    public AdapterBooks(List<Book> books){
        this.books = books;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_book_card,parent,false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CardViewHolder viewHolder = (CardViewHolder) holder;
        Book book = books.get(position);
        viewHolder.txtNombre.setText(book.getTitle());
        viewHolder.txtAutor.setText(book.getAuthor());
        viewHolder.imgLibro.setImageURI(Uri.parse(book.getBookPicture()));
        viewHolder.txtDisponibles.setText(String.valueOf(book.isAvailable()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetallesActivity.class);
                intent.putExtra(BOOK_ID,book.getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
