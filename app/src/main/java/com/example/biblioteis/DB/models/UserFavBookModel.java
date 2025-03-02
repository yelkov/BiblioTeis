package com.example.biblioteis.DB.models;

import java.util.ArrayList;
import java.util.List;

public class UserFavBookModel {

    private int user_id;
    private List<Integer> book_ids;

    public UserFavBookModel() {
        book_ids = new ArrayList<>();
    }

    public UserFavBookModel(int user_id, List<Integer> book_ids) {
        this.user_id = user_id;
        this.book_ids = book_ids;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public List<Integer> getBook_ids() {
        return book_ids;
    }

    public void setBook_ids(List<Integer> book_ids) {
        this.book_ids = book_ids;
    }

    public void addBook(Integer book_id){
        if(!book_ids.contains(book_id)){
            book_ids.add(book_id);
        }
    }

    public void removeBook(Integer book_id){
        if (book_ids.contains(book_id)) {
            book_ids.remove(book_id);
        }
    }

}
