package com.example.biblioteis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.API.repository.ImageRepository;

import okhttp3.ResponseBody;

public class DetallesActivity extends AppCompatActivity {

    TextView txtTitulo, txtAutor, txtIsbn, txtDisponible, txtFecha;
    ImageView imgLibroDetalle;
    Button btnReservar, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtTitulo = findViewById(R.id.txtTitulo);
        txtAutor = findViewById(R.id.txtAuthor);
        txtIsbn = findViewById(R.id.txtIsbn);
        txtDisponible = findViewById(R.id.txtDisponible);
        txtFecha = findViewById(R.id.txtFecha);
        imgLibroDetalle = findViewById(R.id.imgLibroDetalle);
        btnReservar = findViewById(R.id.btnReservar);
        btnVolver = findViewById(R.id.btnVolverDetalle);

        Intent intent = getIntent();
        Integer bookId = intent.getIntExtra(AdapterBooks.BOOK_ID,0);
        BookRepository br = new BookRepository();

        br.getBookById(bookId, new BookRepository.ApiCallback<Book>() {
            @Override
            public void onSuccess(Book result) {
                setBook(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(DetallesActivity.this, "Se ha producido un error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void setBook(Book book){
        txtTitulo.setText(book.getTitle());
        txtAutor.setText(book.getAuthor());
        txtFecha.setText(book.getPublishedDate());
        if(book.isAvailable()){
            txtDisponible.setText("Sí");
        }else{
            txtDisponible.setText("No");
        }
        txtIsbn.setText(book.getIsbn());
        ImageRepository ir = new ImageRepository();
        ir.getImage(book.getBookPicture(), new BookRepository.ApiCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                if(result != null){
                    Bitmap bitmap = BitmapFactory.decodeStream(result.byteStream());
                    imgLibroDetalle.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}