package com.example.biblioteis.config;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;

import com.example.biblioteis.API.models.User;
import com.example.biblioteis.R;
import com.example.biblioteis.provider.UserProvider;
import com.example.biblioteis.loginActivity.LoginActivity;
import com.example.biblioteis.perfilActivity.PerfilUsuarioActivity;
import com.example.biblioteis.qrscannerActivity.QRScannerActivity;

public class MenuConfig implements MenuProvider {
    Context context;
    Toolbar toolbar;

    public MenuConfig(Context context, Toolbar toolbar){
        this.context = context;
        this.toolbar = toolbar;
        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        ((AppCompatActivity) context).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu, menu);
        updateToolbarProfileTint();
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id == R.id.btnEscanear){
            Intent intent = new Intent(context, QRScannerActivity.class);
            context.startActivity(intent);
        }
        if(id == R.id.btnLoguear && !(context instanceof LoginActivity)){
            Intent intent = new Intent(context,LoginActivity.class);
            context.startActivity(intent);
        }
        if(id == R.id.btnPerfil && !(context instanceof PerfilUsuarioActivity)){
            User usuario = UserProvider.getInstance();
            if(usuario.getName() == null){
                Toast.makeText(context, "Usuario no logueado", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(context,PerfilUsuarioActivity.class);
                context.startActivity(intent);
            }
        }
        return false;
    }

    public void updateToolbarProfileTint() {
        MenuItem btnPerfil = toolbar.getMenu().findItem(R.id.btnPerfil);
        if (btnPerfil != null) {
            User usuario = UserProvider.getInstance();
            int color = (usuario.getName() != null) ? R.color.secondary_dark : R.color.grey;
            btnPerfil.getIcon().setTint(context.getColor(color));
        }
    }
}
