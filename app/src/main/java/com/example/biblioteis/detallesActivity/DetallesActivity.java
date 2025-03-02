package com.example.biblioteis.detallesActivity;

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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.models.BookLending;
import com.example.biblioteis.API.models.User;
import com.example.biblioteis.API.repository.BookLendingRepository;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.API.repository.ImageRepository;
import com.example.biblioteis.adapter.AdapterBooks;
import com.example.biblioteis.config.MenuConfig;
import com.example.biblioteis.R;
import com.example.biblioteis.provider.UserProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import okhttp3.ResponseBody;

public class DetallesActivity extends AppCompatActivity {

    TextView txtTitulo, txtAutor, txtIsbn, txtFechaDevolucion, txtFechaPublicacion, txtDetallesUsuario, txtDetallesNombre;
    ImageView imgLibroDetalle;
    Button btnReservar, btnVolver;
    BookLending lastLending;
    LinearLayout linearDevolucion;
    Integer bookId;
    BookRepository br;
    BookLendingRepository blr;
    Toolbar tbDetalles;
    MenuConfig menuConfig;

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

        initializeViews();
        checkUserLogin();
        menuConfig = new MenuConfig(this, tbDetalles);
        addMenuProvider(menuConfig);

        Intent intent = getIntent();
        bookId = intent.getIntExtra(AdapterBooks.BOOK_ID,0);
        br = new BookRepository();
        blr = new BookLendingRepository();

        askForBook();

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
    }//fin del OnCreate

    private void checkUserLogin() {
        User usuario = UserProvider.getInstance();
        if(usuario.getName() != null){
            btnReservar.setEnabled(true);
            txtDetallesUsuario.setText("Usuario: ");
            txtDetallesNombre.setText(usuario.getName());
        }else{
            btnReservar.setEnabled(false);
            txtDetallesUsuario.setText("");
            txtDetallesNombre.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        askForBook();
        checkUserLogin();
        menuConfig.updateToolbarProfileTint();
    }

    private void askForBook() {
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
    }

    private void initializeViews() {
        txtTitulo = findViewById(R.id.txtTitulo);
        txtAutor = findViewById(R.id.txtAuthor);
        txtIsbn = findViewById(R.id.txtIsbn);
        txtFechaDevolucion = findViewById(R.id.txtFechaDevolucion);
        txtFechaPublicacion = findViewById(R.id.txtFechaPublicacion);
        txtDetallesUsuario = findViewById(R.id.txtDetallesUsuario);
        txtDetallesNombre = findViewById(R.id.txtDetallesNombre);
        imgLibroDetalle = findViewById(R.id.imgLibroDetalle);
        btnReservar = findViewById(R.id.btnReservar);
        btnVolver = findViewById(R.id.btnVolverDetalle);
        linearDevolucion = findViewById(R.id.linearDevolucion);
        tbDetalles = findViewById(R.id.tbDetalles);
    }

    private void devolverLibro(BookLendingRepository blr) {
        blr.returnBook(lastLending.getId(), new BookRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(DetallesActivity.this, "Libro devuelto", Toast.LENGTH_SHORT).show();
                btnReservar.setText("Reservar");
                linearDevolucion.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(DetallesActivity.this, "Se ha producido un error al devolver el libro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reservarLibro(BookLendingRepository blr, Integer bookId) {
        blr.lendBook(bookId, UserProvider.getInstance().getId(), new BookRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(DetallesActivity.this, "Se ha reservado el libro", Toast.LENGTH_SHORT).show();
                btnReservar.setText("Devolver");
                linearDevolucion.setVisibility(View.VISIBLE);
                LocalDateTime fechaDevolucion = LocalDateTime.now().plusWeeks(2);
                setFechaDevolucion(fechaDevolucion);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(DetallesActivity.this, "Se ha producido un error al reservar el libro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setBook(Book book){
        if(book != null){
            txtTitulo.setText(book.getTitle());
            txtAutor.setText(book.getAuthor());

            LocalDateTime publishedDate = LocalDateTime.parse(book.getPublishedDate());
            txtFechaPublicacion.setText(formatearFecha(publishedDate));

            txtIsbn.setText(book.getIsbn());

            setBookImage(book);
            setLendingViews(book);
        }else{
            Toast.makeText(this, "El libro solicitado no existe", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setLendingViews(Book book) {
        if(!book.isAvailable()){
            List<BookLending> lendings = book.getBookLendings();
            Optional<BookLending> optLastLending = lendings.stream().sorted(Comparator.comparing(BookLending::getId).reversed()).findFirst();
            if(optLastLending.isPresent()){
                lastLending = optLastLending.get();

                LocalDateTime lendingDate = LocalDateTime.parse(lastLending.getLendDate());
                LocalDateTime returningDate = lendingDate.plusWeeks(2);

                linearDevolucion.setVisibility(View.VISIBLE);
                setFechaDevolucion(returningDate);

                User currentUser = UserProvider.getInstance();
                if(lastLending.getUserId() == currentUser.getId()){
                    btnReservar.setText("Devolver");
                }else{
                    btnReservar.setEnabled(false);
                }
            }
        }else{
            linearDevolucion.setVisibility(View.GONE);
            btnReservar.setText("Reservar");
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
                }else{
                    imgLibroDetalle.setImageResource(R.drawable.cover);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(DetallesActivity.this, "Se ha producido un error al cargar la imagen del libro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatearFecha(LocalDateTime fecha){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return fecha.format(dateFormat);
    }

    private void setFechaDevolucion(LocalDateTime fecha){
        if(fecha.isBefore(LocalDateTime.now())){
            txtFechaDevolucion.setTextColor(ContextCompat.getColor(this,R.color.red));
        }else{
            txtFechaDevolucion.setTextColor(ContextCompat.getColor(this,R.color.black));
        }
        txtFechaDevolucion.setText(formatearFecha(fecha));
    }
}