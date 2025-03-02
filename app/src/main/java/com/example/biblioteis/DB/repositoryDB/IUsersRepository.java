package com.example.biblioteis.DB.repositoryDB;

import com.example.biblioteis.DB.models.UserFavBookModel;

public interface IUsersRepository<T> {
    UserFavBookModel getUserFavBooks(int userId);

    void insertFavBook(int userId, int bookId);

    void deleteFavBook(int userId, int bookId);
}
