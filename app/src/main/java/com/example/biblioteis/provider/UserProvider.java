package com.example.biblioteis.provider;

import com.example.biblioteis.API.models.User;

public class UserProvider {
    private static User instance;

    private UserProvider(){
    }

    public static User getInstance(){
        if(instance == null){
            instance = new User();
        }
        return instance;
    }

    public static void logout() {
        instance = null;
    }

}
