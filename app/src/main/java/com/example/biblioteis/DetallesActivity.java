package com.example.biblioteis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.repository.BookRepository;

public class DetallesActivity extends AppCompatActivity {

    TextView txtTitulo, txtAutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtTitulo = findViewById(R.id.txtTitulo);
        txtAutor = findViewById(R.id.txtAuthor);

        Intent intent = getIntent();
        Integer bookId = intent.getIntExtra(AdapterBooks.BOOK_ID,0);
        BookRepository br = new BookRepository();

        br.getBookById(bookId, new BookRepository.ApiCallback<Book>() {
            @Override
            public void onSuccess(Book result) {
                setBook(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(DetallesActivity.this, "Se ha producido un error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void setBook(Book book){
        txtTitulo.setText(book.getTitle());
        txtAutor.setText(book.getAuthor());
    }
}