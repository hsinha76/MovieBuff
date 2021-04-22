package com.hsdroid.moviebuff.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hsdroid.moviebuff.Model.MovieResponse;

import java.util.ArrayList;

public class DBSqlite extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static String db_name = "MyMovies";
    private static String col1 = "movie_id";
    private static String col2 = "movie_blob";
    private static String table_name = "movies";

    public DBSqlite(@Nullable Context context) {
        super(context, db_name, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + table_name + " ("
                + col1 + " INTEGER primary key AUTOINCREMENT, "
                + col2 + " BLOB not null)";
        Log.d("myQuery", query);
        db.execSQL(query);
    }

    public void insertDBData(ArrayList<MovieResponse> responses) {
        SQLiteDatabase db = this.getWritableDatabase();

        truncateTable();
        Log.d("myQuery", "datainserted");
        Gson gson = new Gson();
        ContentValues values = new ContentValues();
        values.put(col2, gson.toJson(responses).getBytes());
        db.insert(table_name, null, values);
        db.close();

    }

    public ArrayList<MovieResponse> getDBData() {
        String selectQuery = "SELECT  * FROM " + table_name;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        byte[] blob = cursor.getBlob(cursor.getColumnIndex(col2));
        String json = new String(blob);
        Gson gson = new Gson();
        ArrayList<MovieResponse> response = gson.fromJson(json, new TypeToken<ArrayList<MovieResponse>>() {
        }.getType());

        Log.d("myQuery", response.toString());

        return response;
    }


    public void truncateTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM '" + table_name + "'");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreate(db);
    }
}
