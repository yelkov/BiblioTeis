package com.example.biblioteis;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblioteis.API.models.Book;

import java.util.List;

public class CatalogoVM extends ViewModel {
    MutableLiveData<List<Book>> books;

    public CatalogoVM(){
        books = new MutableLiveData<>();
    }
}
