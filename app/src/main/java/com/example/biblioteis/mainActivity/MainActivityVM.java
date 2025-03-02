package com.example.biblioteis.mainActivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblioteis.API.models.Book;

import java.util.List;

public class MainActivityVM extends ViewModel {

    MutableLiveData<Book> book;
    MutableLiveData<List<Book>> ultimosPublicados;
    MutableLiveData<List<Book>> recomendaciones;

    public MainActivityVM(){
        book = new MutableLiveData<>();
        ultimosPublicados = new MutableLiveData<>();
        recomendaciones = new MutableLiveData<>();
    }

}
