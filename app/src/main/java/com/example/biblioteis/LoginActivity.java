package com.example.biblioteis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.biblioteis.API.models.User;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.API.repository.UserRepository;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin, btnCancelar;
    EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();

        User usuario = UserProvider.getInstance();
        if(usuario.getName() != null){
            etEmail.setText(usuario.getEmail());
            etPassword.setText(usuario.getPasswordHash());
            btnLogin.setText("LogOut");
        }

        UserRepository ur = new UserRepository();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usuario.getName() == null){
                    ur.login(etEmail.getText().toString(), etPassword.getText().toString(), new BookRepository.ApiCallback<User>() {
                        @Override
                        public void onSuccess(User result) {
                            if(result == null){
                                Toast.makeText(LoginActivity.this, "Usuario o contraseña no son correctos", Toast.LENGTH_LONG).show();
                                etEmail.setText("");
                                etPassword.setText("");
                            }else{
                                Toast.makeText(LoginActivity.this, "Login existoso, bienvenido", Toast.LENGTH_LONG).show();
                                User usuario = UserProvider.getInstance();
                                usuario.setEmail(result.getEmail());
                                usuario.setName(result.getName());
                                usuario.setId(result.getId());
                                usuario.setBookLendings(result.getBookLendings());
                                usuario.setDateJoined(result.getDateJoined());
                                usuario.setPasswordHash(result.getPasswordHash());
                                usuario.setProfilePicture(result.getProfilePicture());
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            etEmail.setText("");
                            etPassword.setText("");
                            Toast.makeText(LoginActivity.this, "Se ha producido un error en la solicitud", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "Cerrando sesión", Toast.LENGTH_LONG).show();
                    UserProvider.logout();
                    finish();
                }

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initializeViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnCancelar = findViewById(R.id.btnCancelar);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
    }
}