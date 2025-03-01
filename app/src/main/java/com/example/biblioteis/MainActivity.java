package com.example.biblioteis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.models.User;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.API.repository.UserRepository;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    Toolbar tbMain;
    RecyclerView rvUltimosPublicados, rvRecomendaciones;
    Button  btnCatalogo;
    TextView txtUsuario, txtNombreUsuario;
    BookRepository br;
    MainActivityVM vm;
    MenuConfig menuConfig;
    MutableLiveData<User> userLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate: started");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();

        setSessionUsuario();

        menuConfig = new MenuConfig(this,tbMain);
        addMenuProvider(menuConfig);

        vm = new ViewModelProvider(this).get(MainActivityVM.class);
        vm.ultimosPublicados.observe(this, books -> {
            rvUltimosPublicados.setAdapter(new AdapterBooks(books));
        });
        vm.recomendaciones.observe(this,books ->{
            rvRecomendaciones.setAdapter(new AdapterBooks(books));
        });

        br = new BookRepository();
        cargarLibros();

        btnCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CatalogoActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        registerForContextMenu(btnCatalogo); //vamos a poner un menu contextual en el botón de catálogo para permitir introducir manualmente un código de libro

        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                checkUsuarioLogueado();
            }
        });

    }//fin onCreate



    private void initializeViews() {
        tbMain = findViewById(R.id.tbMain);

        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtNombreUsuario.setText(" ");
        txtUsuario = findViewById(R.id.txtUsuario);
        txtUsuario.setText(" ");

        btnCatalogo = findViewById(R.id.btnCatalogo);

        rvUltimosPublicados = findViewById(R.id.rvUltimosPublicados);
        rvUltimosPublicados.setLayoutManager(new LinearLayoutManager(this));
        rvRecomendaciones = findViewById(R.id.rvRecomendaciones);
        rvRecomendaciones.setLayoutManager(new LinearLayoutManager(this));
    }

    private void cargarLibros() {
        br.getBooks(new BookRepository.ApiCallback<List<Book>>(){

            @Override
            public void onSuccess(List<Book> result) {
                List<Book> ultimosPublicados = result.stream()
                        .sorted(Comparator.comparing(Book::getPublishedDate).reversed())
                        .limit(5)
                        .collect(Collectors.toList());
                vm.ultimosPublicados.setValue(ultimosPublicados);

                Collections.shuffle(result);
                List<Book> recomendaciones = result.stream()
                        .filter(Book::isAvailable)
                        .limit(2)
                        .collect(Collectors.toList());
                vm.recomendaciones.setValue(recomendaciones);

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(MainActivity.this, "Se ha producido un error en el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkUsuarioLogueado();
        cargarLibros();
        menuConfig.updateToolbarProfileTint();
    }

    private void checkUsuarioLogueado() {
        User usuario = UserProvider.getInstance();
        if(usuario.getName() != null){
            txtUsuario.setText("Usuario: ");
            txtNombreUsuario.setText(usuario.getName());

        }else{
            txtUsuario.setText(" ");
            txtNombreUsuario.setText(" ");

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menucontextual,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuItem itemCodigo = menu.findItem(R.id.itemCodigo);
        itemCodigo.setVisible(true);
        MenuItem itemFavorito = menu.findItem(R.id.itemFavorito);
        itemFavorito.setVisible(false);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.itemCodigo){
            mostrarDialogoCodigo();
        }
        return super.onContextItemSelected(item);
    }

    private void mostrarDialogoCodigo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresa código de libro manualmente");

        EditText etCodigo = new EditText(this);
        etCodigo.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(etCodigo);

        builder.setPositiveButton("Aceptar",((dialog, which) -> {
            try{
                Integer codigo = Integer.parseInt(etCodigo.getText().toString());
                Intent intent = new Intent(this, DetallesActivity.class);
                intent.putExtra(AdapterBooks.BOOK_ID,codigo);
                startActivity(intent);
            }catch (NumberFormatException e){
                dialog.cancel();
            }

        }));

        builder.setNegativeButton("Cancelar",((dialog, which) -> dialog.cancel()));

        builder.show();
    }

    private void setSessionUsuario() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isLogged = sp.getBoolean(LoginActivity.IS_LOGGED,false);

        if(isLogged){
            String email = sp.getString(LoginActivity.USER_EMAIL,null);
            String password;
            MasterKey mk = null;
            try{
                mk = new MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
                SharedPreferences spEcrypted = EncryptedSharedPreferences.create(this,LoginActivity.ENCRYPTEDSHARE,mk,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

                password = spEcrypted.getString(LoginActivity.PASSWORD,null);

            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            loguearUsuario(email,password);
            checkUsuarioLogueado();
        }
    }

    private void loguearUsuario(String email, String password) {
        UserRepository ur = new UserRepository();
        ur.login(email, password, new BookRepository.ApiCallback<User>() {
            @Override
            public void onSuccess(User result) {
                if(result != null){
                    Toast.makeText(MainActivity.this, "Login existoso, bienvenido", Toast.LENGTH_LONG).show();
                    User usuario = UserProvider.getInstance();
                    usuario.setEmail(result.getEmail());
                    usuario.setName(result.getName());
                    usuario.setId(result.getId());
                    usuario.setBookLendings(result.getBookLendings());
                    usuario.setDateJoined(result.getDateJoined());
                    usuario.setPasswordHash(result.getPasswordHash());
                    usuario.setProfilePicture(result.getProfilePicture());
                    userLiveData.setValue(usuario);

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(MainActivity.this, "Se ha producido un error en la solicitud de login", Toast.LENGTH_LONG).show();
            }
        });

    }
}

