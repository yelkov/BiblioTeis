package com.example.biblioteis;

import com.example.biblioteis.API.models.User;

public class UserProvider {
    private static User instance;

    private UserProvider(){
    }

    public static User getInstance(User usuario){
        if(instance == null){
            instance = usuario;
        }
        return instance;
    }

}
