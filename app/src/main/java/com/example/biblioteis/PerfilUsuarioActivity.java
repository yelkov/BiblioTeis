package com.example.biblioteis;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    TextView txtPerfilNombre, txtPerfilEmail, txtMasElementos;
    RecyclerView rvHistoricoLendings, rvActiveLendings;
    List<BookLending> historicoLendings, activeLendings;
    LinearLayout linearActiveLendings;

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

        User usuario = UserProvider.getInstance();
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

        PerfilUsuarioVM vm = new ViewModelProvider(this).get(PerfilUsuarioVM.class);

        vm.historicoLendings.observe(this, lendings ->{
            rvHistoricoLendings.setAdapter(new AdapterLendings(lendings));
        });

        vm.activeLendings.observe(this, bookLendings -> {
            rvActiveLendings.setAdapter(new AdapterLendings(bookLendings));
        });

        UserRepository ur = new UserRepository();
        ur.getUserById(usuario.getId(), new BookRepository.ApiCallback<User>() {
            @Override
            public void onSuccess(User result) {
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
                if(activeLendings.size() == 0){
                    linearActiveLendings.setVisibility(GONE);
                }else{
                    linearActiveLendings.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        rvActiveLendings.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    int totalItems = layoutManager.getItemCount();

                    txtMasElementos.setVisibility(lastVisibleItem < totalItems - 1 ? View.VISIBLE : View.GONE);
                }
            }
        });


    }

    private void initializeViews() {
        imgPerfil = findViewById(R.id.imgPerfil);
        txtPerfilNombre = findViewById(R.id.txtPerfilNombre);
        txtPerfilEmail = findViewById(R.id.txtPerfilEmail);
        linearActiveLendings = findViewById(R.id.linearActiveLendings);
        txtMasElementos = findViewById(R.id.txtMoreItems);

        historicoLendings = new ArrayList<>();
        activeLendings = new ArrayList<>();

        rvHistoricoLendings = findViewById(R.id.rvHistoricoLendings);
        rvHistoricoLendings.setLayoutManager(new LinearLayoutManager(this));
        rvActiveLendings = findViewById(R.id.rvActiveLendings);
        rvActiveLendings.setLayoutManager(new LinearLayoutManager(this));
    }
}