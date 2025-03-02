package com.example.biblioteis.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.models.BookLending;
import com.example.biblioteis.API.repository.BookRepository;
import com.example.biblioteis.R;
import com.example.biblioteis.detallesActivity.DetallesActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdapterLendings extends RecyclerView.Adapter {

    List<BookLending> lendings;
    BookRepository bookRepository;

    public class LendingCardViewHolder extends RecyclerView.ViewHolder {
        TextView txtLendingBookName, txtLendingDate, txtReturningDateText, txtReturningDate;
        public LendingCardViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLendingBookName = itemView.findViewById(R.id.txtLendingBookName);
            txtLendingDate = itemView.findViewById(R.id.txtLendingDate);
            txtReturningDateText = itemView.findViewById(R.id.txtReturningDateText);
            txtReturningDate = itemView.findViewById(R.id.txtReturningDate);

        }
    }

    public AdapterLendings(List<BookLending> lendings){
        this.lendings = lendings;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        bookRepository = new BookRepository();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_lending_card,parent,false);
        return new LendingCardViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LendingCardViewHolder viewHolder = (LendingCardViewHolder) holder;
        BookLending lending = lendings.get(position);

        LocalDateTime lendDate = LocalDateTime.parse(lending.getLendDate());

        viewHolder.txtLendingDate.setText(formatearFecha(lendDate));
        if(lending.getReturnDate() != null){
            LocalDateTime returnDate = LocalDateTime.parse(lending.getReturnDate());
            viewHolder.txtReturningDate.setText(formatearFecha(returnDate));
        }else{
            LocalDateTime limitDate = lendDate.plusWeeks(2);
            if(limitDate.isBefore(LocalDateTime.now())){
                viewHolder.txtReturningDateText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.red));
                viewHolder.txtReturningDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.red));
            }else{
                viewHolder.txtReturningDateText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                viewHolder.txtReturningDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
            }
            viewHolder.txtReturningDateText.setText("Devolver antes de: ");
            viewHolder.txtReturningDate.setText(formatearFecha(limitDate));
        }

        bookRepository.getBookById(lending.getBookId(), new BookRepository.ApiCallback<Book>() {
            @Override
            public void onSuccess(Book result) {
                viewHolder.txtLendingBookName.setText(result.getTitle());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), DetallesActivity.class);
                        intent.putExtra(AdapterBooks.BOOK_ID,result.getId());
                        v.getContext().startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "Se ha producido un error al consultar el libro", Toast.LENGTH_SHORT).show();
            }
        });

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_dark));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.secondary_light));
        }
        
    }

    @Override
    public int getItemCount() {
        return lendings.size();
    }

    private String formatearFecha(LocalDateTime fecha){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return fecha.format(dateFormat);
    }
}
