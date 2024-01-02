package com.example.tab_layout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.PublicKey;
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

        String CREATE_TABLE_HASHTAG = "CREATE TABLE photo_hashtag (" +
                "id INTEGER PRIMARY KEY," +
                "path TEXT," +
                "hashtag TEXT)";

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

    public void onUpgradeContactHashtag(SQLiteDatabase db, String id, ContentValues value){
        String selection = "id = ?";
        String[] selectionArgs = { id };
        db.update("contact", value, selection, selectionArgs);
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
    public String onSearchContactHashTag(SQLiteDatabase db, String path){
        Cursor cursor = null;
        db = this.getReadableDatabase();
        try {
            cursor = db.query(
                    "contact_hashtag",   // 테이블 이름
                    new String[]{"hashtag"},              // 반환할 컬럼들
                    "path = ?",            // 선택 조건
                    new String[] { path }, // 선택 조건에 대한 값
                    null,                // group by
                    null,                // having
                    null                 // order by
            );
            // 쿼리 결과 처리...
        } catch (Exception e) {
            e.printStackTrace();
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
    public String onSearchPhotoHashTag(SQLiteDatabase db, String path){
        Cursor cursor = null;
        db = this.getReadableDatabase();
        try {
            cursor = db.query(
                    "photo_hashtag",   // 테이블 이름
                    new String[]{"path, hashtag"},              // 반환할 컬럼들
                    "path = ?",            // 선택 조건
                    new String[] { path }, // 선택 조건에 대한 값
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

    public void onContactDelete(SQLiteDatabase db, String name, String phone_num, String contactId, String contactHashTag){
        db = this.getReadableDatabase();
        db.delete("contact", "id = ?", new String[]{contactId});
        db.delete("contact_hashtag", "id = ?", new String[]{contactId});
    }
    public void onEditContact(SQLiteDatabase db, String contact_id, String hashtag, ContentValues values, ContentValues values2){
        db = this.getReadableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = { contact_id };

        // 테이블 업데이트
        db.update("contact", values, selection, selectionArgs);

        // 새로운 hashtag 생성 및 저장
        String existingHashtag = onSearchContactHashTag(db, contact_id);
        if (existingHashtag == null || existingHashtag.isEmpty()) {
            ContentValues newHashtagValues = new ContentValues();
            newHashtagValues.put("id", contact_id);
            newHashtagValues.put("hashtag", hashtag);
            db.insert("contact_hashtag", null, newHashtagValues);
        }
        else {
            db.update("contact_hashtag", values2, selection, selectionArgs);
        }
    }
    public void onEditPhotoHashtag(SQLiteDatabase db, String path, String hashtag){
        db = this.getReadableDatabase();
        String selection = "path = ?";
        String[] selectionArgs = { path };

        ContentValues newHashtagValues = new ContentValues();
        newHashtagValues.put("hashtag", hashtag);
        // 테이블 업데이트
        db.update("photo_hashtag", newHashtagValues, selection, selectionArgs);

        // 새로운 hashtag 생성 및 저장
        String existingHashtag = onSearchPhotoHashTag(db, path);
        if (existingHashtag == null || existingHashtag.isEmpty()) {
            ContentValues changeHashtag = new ContentValues();
            changeHashtag.put("path", path);
            changeHashtag.put("hashtag", hashtag); // 여기에 새로운 hashtag 값을 넣어줘
            db.insert("contact_hashtag", null, changeHashtag);
        }
    }

    //에러 확인용도
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
