package com.example.tab_layout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "example.db";
    private static final int DATABASE_VERSION = 1;
    private static DBHelper instance;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
//        return instance = new DBHelper(context.getApplicationContext());
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACT_TABLE = "CREATE TABLE contact (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT," +
                "phone_num TEXT)";
        String CREATE_HASHTAG_TABLE = "CREATE TABLE contact_hashtag (" +
                "id INTEGER PRIMARY KEY," +
                "hashtag TEXT)";

        db.execSQL(CREATE_CONTACT_TABLE);
        db.execSQL(CREATE_HASHTAG_TABLE);
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

    public void onUpgradeContactHashtag(SQLiteDatabase db, int id, ContentValues value){
        value.put("id", id);
        db.insert("contact_hashtag", null, value);
    }
    public List<Map<String, String>> onSearchContact(SQLiteDatabase db){
//        checkTableList(db);
        List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();
        Cursor cursor = db.query("contact", new String[]{"id", "name", "phone_num"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phoneNum = cursor.getString(cursor.getColumnIndex("phone_num"));
                Map<String, String> contact = new HashMap<String, String>(2);
                contact.put("id", id);
                contact.put("name", name);
                contact.put("phoneNum", phoneNum);
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactList;
    }
    public String onSearchContactHashTag(SQLiteDatabase db, String id){
        Cursor cursor = null;
        db = this.getReadableDatabase();
        try {
            cursor = db.query(
                    "contact_hashtag",   // 테이블 이름
                    new String[]{"hashtag"},              // 반환할 컬럼들
                    "id = ?",            // 선택 조건
                    new String[] { id }, // 선택 조건에 대한 값
                    null,                // group by
                    null,                // having
                    null                 // order by
            );
            // 쿼리 결과 처리...
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리 로직
        }
        String out = "";
        if (cursor != null && cursor.moveToFirst()) {
            do {
                out = cursor.getString(cursor.getColumnIndex("hashtag"));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return out;
    }

    public List<Map<String, String>> search(String query) {
        List<Map<String, String>> searchResults = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 검색 쿼리
        String selection = "name LIKE ? OR phone_num LIKE ?";
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%"};

        Cursor cursor = db.query(
                "contact",
                new String[]{"name", "phone_num"},
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // 결과 추가
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phoneNum = cursor.getString(cursor.getColumnIndex("phone_num"));
                Map<String, String> contact1 = new HashMap<>();
                contact1.put("name", name);
                contact1.put("phoneNum", phoneNum);
                searchResults.add(contact1);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return searchResults;
    }

    public void onContactDelete(SQLiteDatabase db, String name, String phone_num, String contactId){
        db = this.getReadableDatabase();
        db.delete("contact", "id = ?", new String[]{contactId});
    }

    public void checkTableList(SQLiteDatabase db){
        List<String> tableNames = new ArrayList<>();
        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor.moveToFirst()) {
            do {
                String tableName = cursor.getString(0);
                tableNames.add(tableName);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
