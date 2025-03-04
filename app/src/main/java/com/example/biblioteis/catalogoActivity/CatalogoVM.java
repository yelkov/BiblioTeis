package com.example.biblioteis.catalogoActivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblioteis.API.models.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogoVM extends ViewModel {
    MutableLiveData<List<Book>> books;
    Map<String, Integer[]> allBooksStats;

    public CatalogoVM(){
        books = new MutableLiveData<>();
        allBooksStats = new HashMap<>();
    }
}
