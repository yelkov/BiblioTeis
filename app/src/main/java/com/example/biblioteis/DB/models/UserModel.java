package com.example.biblioteis.DB.models;

public class UserModel {

    private int user_id;
    private int book_fav;

    public UserModel() {
    }

    public UserModel(int user_id, int book_fav) {
        this.user_id = user_id;
        this.book_fav = book_fav;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getBook_fav() {
        return book_fav;
    }

    public void setBook_fav(int book_fav) {
        this.book_fav = book_fav;
    }
}
