package com.example.biblioteis.loginActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.biblioteis.API.models.User;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.API.repository.UserRepository;
import com.example.biblioteis.config.MenuConfig;
import com.example.biblioteis.R;
import com.example.biblioteis.provider.UserProvider;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class LoginActivity extends AppCompatActivity {

    public static final String IS_LOGGED = "IS_LOGGED";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String ENCRYPTEDSHARE = "ENCRYPTEDSHARE";
    private Button btnLogin, btnCancelar;
    EditText etEmail, etPassword;
    Toolbar tbLogin;

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
        MenuConfig menuConfig = new MenuConfig(this,tbLogin);
        addMenuProvider(menuConfig);

        User usuario = UserProvider.getInstance();
        if(usuario.getName() != null){
            etEmail.setText(usuario.getEmail());
            etPassword.setText(usuario.getPasswordHash());
            btnLogin.setText("Log Out");
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
                                saveUserPreferences(usuario.getEmail(),etPassword.getText().toString(),true);
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
                    saveUserPreferences(null,null,false);
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
        tbLogin = findViewById(R.id.tbLogin);
    }

    private void saveUserPreferences(String email, String password, Boolean isLogged){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(IS_LOGGED,isLogged);
        editor.putString(USER_EMAIL,email);
        editor.apply();

        MasterKey mk = null;
        try{
            mk = new MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            SharedPreferences spEncrypted = EncryptedSharedPreferences.create(this, ENCRYPTEDSHARE,
                    mk,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            SharedPreferences.Editor editorEncrypted = spEncrypted.edit();
            editorEncrypted.putString(PASSWORD,password);
            editorEncrypted.apply();

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}