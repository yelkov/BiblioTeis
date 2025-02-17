package com.example.biblioteis;

import static androidx.core.widget.TextViewKt.addTextChangedListener;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.repository.BookRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogoActivity extends AppCompatActivity {

    RecyclerView rvCatalogo;
    Button btnVolver;
    EditText etBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_catalogo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVolver = findViewById(R.id.btnVolverCatalogo);
        etBuscar = findViewById(R.id.etBuscar);
        rvCatalogo = findViewById(R.id.rvCatalogo);
        rvCatalogo.setLayoutManager(new LinearLayoutManager(this));

        CatalogoVM vm = new ViewModelProvider(this).get(CatalogoVM.class);
        vm.books.observe(this, books -> {
            rvCatalogo.setAdapter(new AdapterBooks(books));
        });

        BookRepository br = new BookRepository();

        br.getBooks(new BookRepository.ApiCallback<List<Book>>(){

            @Override
            public void onSuccess(List<Book> result) {
                vm.books.setValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(CatalogoActivity.this, "Se ha producido un error en el servidor", Toast.LENGTH_SHORT).show();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String busqueda = etBuscar.getText().toString().toLowerCase();
                List<Book> filtrados = vm.books.getValue()
                        .stream()
                        .filter(book -> book.getTitle().toLowerCase().contains(busqueda) || book.getAuthor().toLowerCase().contains(busqueda))
                        .collect(Collectors.toList());
                rvCatalogo.setAdapter(new AdapterBooks(filtrados));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }
}