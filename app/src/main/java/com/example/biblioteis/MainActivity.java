package com.example.biblioteis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.models.User;
import com.example.biblioteis.API.repository.BookRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    RecyclerView rvUltimosPublicados, rvRecomendaciones;
    Button btnLoguear;
    TextView txtNombreUsuario;

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

        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtNombreUsuario.setText(" ");


        btnLoguear = findViewById(R.id.btnLoguear);
        rvUltimosPublicados = findViewById(R.id.rvUltimosPublicados);
        rvUltimosPublicados.setLayoutManager(new LinearLayoutManager(this));
        rvRecomendaciones = findViewById(R.id.rvRecomendaciones);
        rvRecomendaciones.setLayoutManager(new LinearLayoutManager(this));

        btnLoguear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        MainActivityVM vm = new ViewModelProvider(this).get(MainActivityVM.class);


        vm.ultimosPublicados.observe(this, books -> {
            rvUltimosPublicados.setAdapter(new AdapterBooks(books));
        });

        vm.recomendaciones.observe(this,books ->{
            rvRecomendaciones.setAdapter(new AdapterBooks(books));
        });

        BookRepository br = new BookRepository();


        br.getBooks(new BookRepository.ApiCallback<List<Book>>(){

            @Override
            public void onSuccess(List<Book> result) {
                List<Book> ultimosPublicados = result.stream().sorted(Comparator.comparing(Book::getPublishedDate).reversed())
                        .limit(5)
                        .collect(Collectors.toList());
                vm.ultimosPublicados.setValue(ultimosPublicados);

                Collections.shuffle(result);
                List<Book> recomendaciones = result.stream()
                        .limit(2)
                        .collect(Collectors.toList());
                vm.recomendaciones.setValue(recomendaciones);

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(MainActivity.this, "Se ha producido un error en el servidor", Toast.LENGTH_SHORT).show();
            }
        });

    }//fin onCreate

    @Override
    protected void onResume() {
        super.onResume();
        User usuario = UserProvider.getInstance();
        if(usuario.getName() != null){
            txtNombreUsuario.setText(usuario.getName());
            btnLoguear.setText("LogOut");
        }else{
            txtNombreUsuario.setText(" ");
            btnLoguear.setText("LogIn");
        }
    }
}

