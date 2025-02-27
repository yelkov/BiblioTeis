package com.example.biblioteis;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.example.biblioteis.API.models.BookLending;
import com.example.biblioteis.API.models.User;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.API.repository.ImageRepository;
import com.example.biblioteis.API.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class PerfilUsuarioActivity extends AppCompatActivity {

    ImageView imgPerfil;
    TextView txtPerfilNombre, txtPerfilEmail;
    RecyclerView rvHistoricoLendings, rvActiveLendings;
    List<BookLending> historicoLendings, activeLendings;
    PerfilUsuarioVM vm;
    User usuario;
    UserRepository ur;
    Toolbar tbPerfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        addMenuProvider(new MenuConfig(this,tbPerfil));

        usuario = UserProvider.getInstance();
        txtPerfilNombre.setText(usuario.getName());
        txtPerfilEmail.setText(usuario.getEmail());
        if(usuario.getProfilePicture() == null || usuario.getProfilePicture().isEmpty()){
            imgPerfil.setImageResource(R.drawable.user);
        }else{
            ImageRepository ir = new ImageRepository();
            ir.getImage(usuario.getProfilePicture(), new BookRepository.ApiCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody result) {
                    if(result != null){
                        Bitmap bitmap = BitmapFactory.decodeStream(result.byteStream());
                        imgPerfil.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }

        vm = new ViewModelProvider(this).get(PerfilUsuarioVM.class);

        vm.historicoLendings.observe(this, lendings ->{
            rvHistoricoLendings.setAdapter(new AdapterLendings(lendings));
        });

        vm.activeLendings.observe(this, bookLendings -> {
            rvActiveLendings.setAdapter(new AdapterLendings(bookLendings));
        });

        ur = new UserRepository();
        askForUser(vm);
    }

    private void askForUser(PerfilUsuarioVM vm) {
        ur.getUserById(usuario.getId(), new BookRepository.ApiCallback<User>() {
            @Override
            public void onSuccess(User result) {
                historicoLendings.clear();
                activeLendings.clear();
                List<BookLending> allLendings = result.getBookLendings();
                for (BookLending lending : allLendings){
                    if(lending.getReturnDate() != null){
                        historicoLendings.add(lending);
                    }else{
                        activeLendings.add(lending);
                    }
                }
                vm.historicoLendings.setValue(historicoLendings);
                vm.activeLendings.setValue(activeLendings);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(PerfilUsuarioActivity.this, "Se ha producido un error de conexión.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        usuario = UserProvider.getInstance();
        if(usuario.getName() == null){
            finish(); //en caso de que el usuario haga Log Out partiendo de PerfilUsuarioActivity, cerramos la activity cuando vuelva a esta
        }else{
            askForUser(vm); //en caso de que el usuario vuelva desde el detalle de un libro, recargamos los lendings por si se producen cambios
        }
    }

    private void initializeViews() {
        imgPerfil = findViewById(R.id.imgPerfil);
        txtPerfilNombre = findViewById(R.id.txtPerfilNombre);
        txtPerfilEmail = findViewById(R.id.txtPerfilEmail);
        tbPerfil = findViewById(R.id.tbPerfil);

        historicoLendings = new ArrayList<>();
        activeLendings = new ArrayList<>();

        rvHistoricoLendings = findViewById(R.id.rvHistoricoLendings);
        rvHistoricoLendings.setLayoutManager(new LinearLayoutManager(this));
        rvActiveLendings = findViewById(R.id.rvActiveLendings);
        rvActiveLendings.setLayoutManager(new LinearLayoutManager(this));
    }
}