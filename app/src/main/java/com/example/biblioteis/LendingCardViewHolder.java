package com.example.biblioteis;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


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
