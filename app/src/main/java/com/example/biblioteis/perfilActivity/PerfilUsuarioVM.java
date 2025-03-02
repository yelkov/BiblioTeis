package com.example.biblioteis.perfilActivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblioteis.API.models.BookLending;

import java.util.List;

public class PerfilUsuarioVM extends ViewModel {
    MutableLiveData<List<BookLending>> historicoLendings;
    MutableLiveData<List<BookLending>> activeLendings;

    public PerfilUsuarioVM(){
        historicoLendings = new MutableLiveData<>();
        activeLendings = new MutableLiveData<>();
    }
}
