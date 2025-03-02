package com.example.biblioteis.DB.repositoryDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import com.example.biblioteis.DB.databaseAssetHelper.BiblioTeisBDAssetHelper;
import com.example.biblioteis.DB.models.UserFavBookModel;

public class UserRepositoryAssetHelper implements IUsersRepository<UserFavBookModel>{

    Context context;
    BiblioTeisBDAssetHelper bdb;

    public UserRepositoryAssetHelper(Context context){
        this.context = context;
        bdb = new BiblioTeisBDAssetHelper(context);
    }
    @Override
    public UserFavBookModel getUserFavBooks(int i) {
        UserFavBookModel u = new UserFavBookModel();
        Cursor c = null;
        SQLiteDatabase rdb = null;
        try{
            rdb = bdb.getReadableDatabase();

            c = rdb.rawQuery(BiblioTeisBDAssetHelper.GETUSERBOOKS, new String[]{String.valueOf(i)});
            if(c.moveToFirst()){
                u.setUser_id(c.getInt(c.getColumnIndexOrThrow("user")));
                do{
                    u.addBook(c.getInt(c.getColumnIndexOrThrow("book")));
                }while (c.moveToNext());
            }

        } catch (Exception e) {
            Toast.makeText(context, "Error al obtener favoritos", Toast.LENGTH_SHORT).show();
        }finally {
            if(c != null && !c.isClosed()){
                c.close();
            }
            if(rdb != null){
                rdb.close();
            }
        }
        return u;
    }

    @Override
    public void insertFavBook(int userId, int bookId) {
        SQLiteDatabase wdb = null;
        SQLiteStatement stmt = null;
        try{
            wdb = bdb.getWritableDatabase();
            stmt = wdb.compileStatement(BiblioTeisBDAssetHelper.INSERT_NEW_BOOK);
            stmt.bindLong(1, userId);
            stmt.bindLong(2, bookId);
            stmt.executeInsert();

        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al agregar favorito: " + e.getMessage(), e);
            Toast.makeText(context, "Error al agregar favorito", Toast.LENGTH_SHORT).show();
        }finally {
            if (stmt != null) {
                stmt.close();
            }
            if (wdb != null) {
                wdb.close();
            }
        }
    }

    @Override
    public void deleteFavBook(int userId, int bookId) {
        SQLiteDatabase wdb = null;
        SQLiteStatement stmt = null;
        try{
            wdb = bdb.getWritableDatabase();
            stmt = wdb.compileStatement(BiblioTeisBDAssetHelper.DELETE_FAV_BOOK);
            stmt.bindLong(1,userId);
            stmt.bindLong(2,bookId);
            int filasEliminadas = stmt.executeUpdateDelete();

            if(filasEliminadas == 0){
                Toast.makeText(context, "No se ha eliminado ningún favorito", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al eliminar favorito: " + e.getMessage(), e);
            Toast.makeText(context, "Error al eliminar favorito", Toast.LENGTH_SHORT).show();
        }finally {
            if (stmt != null) {
                stmt.close();
            }
            if (wdb != null) {
                wdb.close();
            }
        }
    }
}
