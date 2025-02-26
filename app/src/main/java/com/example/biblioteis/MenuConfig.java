package com.example.biblioteis;

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
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id == R.id.btnEscanear){
            Intent intent = new Intent(context,QRScannerActivity.class);
            context.startActivity(intent);
        }
        if(id == R.id.btnLoguear){
            Intent intent = new Intent(context,LoginActivity.class);
            context.startActivity(intent);
        }
        if(id == R.id.btnPerfil){
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

    public void updateToolbarProfileTint(int color) {
        MenuItem btnPerfil = toolbar.getMenu().findItem(R.id.btnPerfil);
        if (btnPerfil != null) {
            btnPerfil.getIcon().setTint(context.getColor(color));
        }
    }
}
