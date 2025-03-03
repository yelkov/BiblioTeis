package com.example.biblioteis.DB.repositoryDB;

import com.example.biblioteis.DB.models.UserFavBookModel;

public interface IUsersRepository<T> {
    UserFavBookModel getUserFavBooks(int userId);

    boolean userExists(int userId);
    boolean bookExists(int bookId);

    void insertUser(int userId);

    void insertBook(int bookId);

    void insertUserFavBook(int userId, int bookId);

    void deleteFavBook(int userId, int bookId);
}
