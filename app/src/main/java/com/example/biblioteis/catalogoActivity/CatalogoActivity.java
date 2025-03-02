package com.example.biblioteis.catalogoActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.models.User;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.adapter.AdapterBooks;
import com.example.biblioteis.config.MenuConfig;
import com.example.biblioteis.R;
import com.example.biblioteis.provider.UserProvider;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogoActivity extends AppCompatActivity {

    RecyclerView rvCatalogo;
    Button btnVolver;
    EditText etBuscar;
    CatalogoVM vm;
    BookRepository br;
    Toolbar tbCatalogo;
    MenuConfig menuConfig;
    TextView txtCatalogoUsuario, txtCatalogoNombre;

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

        initializeViews();

        comprobarLogin();

        menuConfig = new MenuConfig(this,tbCatalogo);
        addMenuProvider(menuConfig);

        vm = new ViewModelProvider(this).get(CatalogoVM.class);
        vm.books.observe(this, books -> {
            rvCatalogo.setAdapter(new AdapterBooks(books));
        });

        br = new BookRepository();
        cargarLibros();

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
                filtrarLibros();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initializeViews() {
        btnVolver = findViewById(R.id.btnVolverCatalogo);
        etBuscar = findViewById(R.id.etBuscar);
        tbCatalogo = findViewById(R.id.tbCatalogo);
        txtCatalogoUsuario = findViewById(R.id.txtCatalogoUsuario);
        txtCatalogoNombre = findViewById(R.id.txtCatalogoNombre);

        rvCatalogo = findViewById(R.id.rvCatalogo);
        rvCatalogo.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarLibros();
        menuConfig.updateToolbarProfileTint();
        comprobarLogin();
    }

    private void comprobarLogin() {
        User usuario = UserProvider.getInstance();
        if (usuario.getName() != null){
            txtCatalogoUsuario.setText("Usuario: ");
            txtCatalogoNombre.setText(usuario.getName());
        }else{
            txtCatalogoUsuario.setText("");
            txtCatalogoNombre.setText("");
        }
    }

    private void filtrarLibros() {
        String busqueda = etBuscar.getText().toString().toLowerCase();
        List<Book> filtrados = vm.books.getValue()
                .stream()
                .filter(book -> book.getTitle().toLowerCase().contains(busqueda) || book.getAuthor().toLowerCase().contains(busqueda))
                .collect(Collectors.toList());
        rvCatalogo.setAdapter(new AdapterBooks(filtrados));
    }

    private void cargarLibros() {
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
    }
}