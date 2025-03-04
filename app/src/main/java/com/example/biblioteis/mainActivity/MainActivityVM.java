package com.example.biblioteis.mainActivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblioteis.API.models.Book;

import java.util.List;

public class MainActivityVM extends ViewModel {

    MutableLiveData<Book> book;
    MutableLiveData<List<Book>> books;
    public MainActivityVM(){
        book = new MutableLiveData<>();
        books = new MutableLiveData<>();
    }

}
