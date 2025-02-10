package com.example.biblioteis;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblioteis.API.models.Book;

public class MainActivityVM extends ViewModel {

    MutableLiveData<Book> book;

    public MainActivityVM(){
        book = new MutableLiveData<>();
    }



}
