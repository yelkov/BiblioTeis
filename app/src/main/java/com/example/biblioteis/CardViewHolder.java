package com.example.biblioteis;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardViewHolder extends RecyclerView.ViewHolder {
    TextView txtNombre, txtAutor, txtDisponibles;
    ImageView imgLibro;


    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
        txtNombre = itemView.findViewById(R.id.txtNombre);
        txtAutor = itemView.findViewById(R.id.txtAutor);
        imgLibro = itemView.findViewById(R.id.imgLibro);
        txtDisponibles = itemView.findViewById(R.id.txtDisponibles);
    }
}
