package com.example.biblioteis;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.repository.BookRepository;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        MainActivityVM vm = new ViewModelProvider(this).get(MainActivityVM.class);

        vm.book.observe(this, (Book book) -> {
            ((TextView)findViewById(R.id.name)).setText(book.getTitle());
            ((TextView)findViewById(R.id.Autor)).setText(book.getAuthor());
        });

        BookRepository br = new BookRepository();

        br.getBookById(1, new BookRepository.ApiCallback<Book>() {
            @Override
            public void onSuccess(Book result) {
                vm.book.setValue(result);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }
    }

