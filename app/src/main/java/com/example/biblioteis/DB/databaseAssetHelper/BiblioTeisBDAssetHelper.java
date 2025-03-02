package com.example.biblioteis.DB.databaseAssetHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class BiblioTeisBDAssetHelper extends SQLiteAssetHelper {

    private static final int VERSION = 1;
    private static final String DBNAME = "biblioteis.db";
    public static final String GETUSER = "SELECT * FROM USERS WHERE USER_ID = ?";
    public BiblioTeisBDAssetHelper(@Nullable Context context) {
        super(context, DBNAME, null, VERSION);
    }
}
