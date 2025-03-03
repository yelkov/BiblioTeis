package com.example.biblioteis.DB.databaseAssetHelper;

import android.content.Context;

import androidx.annotation.Nullable;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class BiblioTeisBDAssetHelper extends SQLiteAssetHelper {

    private static final int VERSION = 2;
    private static final String DBNAME = "biblioteis.db";
    public static final String GETUSERBOOKS = "SELECT * FROM USERS_BOOKS WHERE USER = ?";
    public static final String USER_EXISTS = "SELECT 1 FROM USERS WHERE USER_ID = ?";
    public static final String BOOK_EXISTS = "SELECT 1 FROM BOOKS WHERE BOOK_ID = ?";
    public static final String INSERT_NEW_USER = "INSERT INTO USERS(USER_ID) VALUES(?)";
    public static final String INSERT_NEW_BOOK = "INSERT INTO BOOKS(BOOK_ID) VALUES(?)";
    public static final String INSERT_NEW_FAV_BOOK = "INSERT INTO USERS_BOOKS(USER,BOOK) VALUES(?,?)";
    public static final String DELETE_FAV_BOOK = "DELETE FROM USERS_BOOKS WHERE USER = ? AND BOOK = ?";
    public BiblioTeisBDAssetHelper(@Nullable Context context) {
        super(context, DBNAME, null, VERSION);
    }
}
