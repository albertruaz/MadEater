package com.example.tab_layout;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "example.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE contact (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT," +
                "phone_num TEXT)";
        db.execSQL(CREATE_TABLE);

    }
    public void onCreatePhoto(SQLiteDatabase db) {
        String CREATE_TABLE_PHOTO = "CREATE TABLE photo (" +
                "id INTEGER PRIMARY KEY," +
                "file_name TEXT," +
                "name TEXT," +
                "introduction TEXT)";

        String CREATE_TABLE_HASHTAG = "CREATE TABLE hashtag (" +
                "id INTEGER PRIMARY KEY," +
                "hashtag TEXT)";

        db.execSQL(CREATE_TABLE_PHOTO);
        db.execSQL(CREATE_TABLE_HASHTAG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contact");
        onCreate(db);
    }

    public void onUpgradeContact(SQLiteDatabase db, ContentValues values) {
        db.insert("contact", null, values);
    }

    public void onUpgradeHashtag(SQLiteDatabase db, ContentValues values,String updater){
        String[] ableDb = {"photo","hashtag"};

        if(Arrays.asList(ableDb).contains(updater)){
            db.insert(updater, null, values);
        }

    }
}
