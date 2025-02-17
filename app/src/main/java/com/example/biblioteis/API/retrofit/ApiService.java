package com.example.biblioteis.API.retrofit;

import com.example.biblioteis.API.models.Book;
import com.example.biblioteis.API.models.BookLending;
import com.example.biblioteis.API.models.BookLendingForm;
import com.example.biblioteis.API.models.User;
import com.example.biblioteis.API.models.UserForm;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // Books Endpoints
    @GET("books")
    Call<List<Book>> getBooks();

    @GET("books/{id}")
    Call<Book> getBook(@Path("id") int id);

    @POST("books")
    Call<Book> createBook(@Body Book book);

    @PUT("books/{id}")
    Call<Void> updateBook(@Path("id") int id, @Body Book book);

    @DELETE("books/{id}")
    Call<Void> deleteBook(@Path("id") int id);

    // Users Endpoints
    @GET("users")
    Call<List<User>> getUsers();

    @GET("users/{id}")
    Call<User> getUser(@Path("id") int id);

    @POST("users")
    Call<User> createUser(@Body User user);

    @PUT("users/{id}")
    Call<Void> updateUser(@Path("id") int id, @Body User user);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int id);

    @POST("users/login")
    Call<User> login(@Body UserForm loginDTO);

    // Book Lending Endpoints
    @GET("booklending")
    Call<List<BookLending>> getLendings();

    @GET("booklending/{id}")
    Call<BookLending> getLending(@Path("id") int id);

    @POST("booklending")
    Call<BookLending> lendBook(@Query("bookId") int bookId, @Query("userId") int userId);

    @PUT("booklending/{id}/return")
    Call<Void> returnBook(@Path("id") int id);
    @GET("image/{filename}")
    Call<ResponseBody> getImage(@Path("filename") String fileName);
}
