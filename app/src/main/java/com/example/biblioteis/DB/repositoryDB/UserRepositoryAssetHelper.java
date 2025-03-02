package com.example.biblioteis.DB.repositoryDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.biblioteis.DB.databaseAssetHelper.BiblioTeisBDAssetHelper;
import com.example.biblioteis.DB.models.UserModel;

public class UserRepositoryAssetHelper implements IUsersRepository<UserModel>{

    Context context;
    BiblioTeisBDAssetHelper bdb;

    public UserRepositoryAssetHelper(Context context){
        this.context = context;
        bdb = new BiblioTeisBDAssetHelper(context);
    }
    @Override
    public LiveData<UserModel> getUser(int i) {
        UserModel u = new UserModel();
        Cursor c = null;
        SQLiteDatabase rdb = null;
        try{
            rdb = bdb.getReadableDatabase();

            c = rdb.rawQuery(BiblioTeisBDAssetHelper.GETUSER, new String[]{String.valueOf(i)});
            if(c.moveToFirst()){
                u.setUser_id(c.getInt(c.getColumnIndexOrThrow("user_id")));
                u.setBook_fav(c.getInt(c.getColumnIndexOrThrow("book_fav")));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if(c != null && !c.isClosed()){
                c.close();
            }
            if(rdb != null){
                rdb.close();
            }
        }
        return new MutableLiveData<>(u);
    }
}
