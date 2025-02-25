package com.example.biblioteis;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.models.BookLending;
import com.example.biblioteis.API.models.User;
import com.example.biblioteis.API.repository.BookLendingRepository;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.API.repository.ImageRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import okhttp3.ResponseBody;

public class DetallesActivity extends AppCompatActivity {

    TextView txtTitulo, txtAutor, txtIsbn, txtDisponible, txtFecha;
    ImageView imgLibroDetalle;
    Button btnReservar, btnVolver;
    Book currentBook;
    BookLending lastLending;
    LinearLayout linearDevolucion;

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

        setViews();

        Intent intent = getIntent();
        Integer bookId = intent.getIntExtra(AdapterBooks.BOOK_ID,0);
        BookRepository br = new BookRepository();
        BookLendingRepository blr = new BookLendingRepository();

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

        btnReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnReservar.getText().equals("Reservar")){
                    reservarLibro(blr, bookId);
                }else{
                    devolverLibro(blr);
                }
            }
        });
    }
    private void setViews() {
        txtTitulo = findViewById(R.id.txtTitulo);
        txtAutor = findViewById(R.id.txtAuthor);
        txtIsbn = findViewById(R.id.txtIsbn);
        txtDisponible = findViewById(R.id.txtDisponible);
        txtFecha = findViewById(R.id.txtFecha);
        imgLibroDetalle = findViewById(R.id.imgLibroDetalle);
        btnReservar = findViewById(R.id.btnReservar);
        btnVolver = findViewById(R.id.btnVolverDetalle);
        linearDevolucion = findViewById(R.id.linearDevolucion);
    }
    private void devolverLibro(BookLendingRepository blr) {
        blr.returnBook(lastLending.getId(), new BookRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(DetallesActivity.this, "Libro devuelto", Toast.LENGTH_SHORT).show();
                btnReservar.setText("Reservar");
                txtDisponible.setText("Sí");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void reservarLibro(BookLendingRepository blr, Integer bookId) {
        blr.lendBook(bookId, UserProvider.getInstance().getId(), new BookRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(DetallesActivity.this, "Se ha reservado el libro", Toast.LENGTH_SHORT).show();
                btnReservar.setText("Devolver");
                txtDisponible.setText("No");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void setBook(Book book){
        currentBook = book;
        txtTitulo.setText(book.getTitle());
        txtAutor.setText(book.getAuthor());

        LocalDateTime publishedDate = LocalDateTime.parse(book.getPublishedDate());
        txtFecha.setText(formatearFecha(publishedDate));

        if(!book.isAvailable()){
            linearDevolucion.setVisibility(VISIBLE);
            txtDisponible.setText("No");
        }

        txtIsbn.setText(book.getIsbn());

        setBookImage(book);

        if(!book.isAvailable()){
            List<BookLending> lendings = book.getBookLendings();
            Optional<BookLending> optLastLending = lendings.stream().sorted(Comparator.comparing(BookLending::getId).reversed()).findFirst();
            if(optLastLending.isPresent()){
                lastLending = optLastLending.get();

                LocalDateTime lendingDate = LocalDateTime.parse(lastLending.getLendDate());
                LocalDateTime returningDate = lendingDate.plusWeeks(2);

                txtDisponible.setText(formatearFecha(returningDate));

                User currentUser = UserProvider.getInstance();
                if(lastLending.getUserId() == currentUser.getId()){
                    btnReservar.setText("Devolver");
                }else{
                    btnReservar.setEnabled(false);
                }
            }
        }
    }

    private void setBookImage(Book book) {
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

    private String formatearFecha(LocalDateTime fecha){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return fecha.format(dateFormat);
    }
}