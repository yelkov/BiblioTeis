package com.example.biblioteis.ui.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.biblioteis.adapter.AdapterBooks;
import com.example.biblioteis.R;
import com.example.biblioteis.ui.detalles.DetallesActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrscanner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Escanea un QR");
        intentIntegrator.initiateScan();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            if(intentResult.getContents() == null){
                Toast.makeText(this, "Se ha producido un error al escanear", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(this, "Leyendo: "+intentResult.getContents(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, DetallesActivity.class);
                intent.putExtra(AdapterBooks.BOOK_ID,1); //vamos a hardcodear el id del libro, pero aquí deberiamos obtener este id del qr que se vaya a leer
                startActivity(intent);
                finish();
            }
        }
    }
}