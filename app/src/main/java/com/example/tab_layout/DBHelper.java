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
                "phone_num TEXT," +
                "path TEXT," +
                "hash_tag TEXT)";

        db.execSQL(CREATE_CONTACT_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contact");
        onCreate(db);
    }

    // addContact (name, phone number)에서 결과 update할 때 쓰는 함수
    public void onUpgradeContact(SQLiteDatabase db, ContentValues values) {
        db.insert("contact", null, values);
    }

    public void onUpgradeContactHashtag(SQLiteDatabase db, String id, ContentValues value){
        String selection = "id = ?";
        String[] selectionArgs = { id };
        db.update("contact", value, selection, selectionArgs);
    }

    // addContact에서 데이터 (name, phone number) 가져올 때 쓰는 함수
    public List<Map<String, String>>onSearchContact(SQLiteDatabase db){
//        checkTableList(db);
        List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();
        Cursor cursor = db.query("contact", new String[]{"id", "name", "phone_num", "path", "hash_tag"}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phoneNum = cursor.getString(cursor.getColumnIndex("phone_num"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                String hashTag = cursor.getString(cursor.getColumnIndex("hash_tag"));

                Map<String, String> contact = new HashMap<String, String>(5);
                contact.put("id", id);
                contact.put("name", name);
                contact.put("phoneNum", phoneNum);
                contact.put("path", path);
                contact.put("hashTag", hashTag);
                contactList.add(contact);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return contactList;
    }

//    // hashtag 읽어올 때 쓰는 함수
    public Map<String, String> onSearchContactById(String id){
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            cursor = db.query(
                    "contact",   // 테이블 이름
                    new String[]{"id", "name", "phone_num", "path", "hash_tag"},
                    "id = ?",            // 선택 조건
                    new String[] { id }, // 선택 조건에 대한 값
                    null,                // group by
                    null,                // having
                    null                 // order by
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> out = new HashMap<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                out.put("id", cursor.getString(cursor.getColumnIndex("id")));
                out.put("name", cursor.getString(cursor.getColumnIndex("name")));
                out.put("phone_num", cursor.getString(cursor.getColumnIndex("phone_num")));
                out.put("path", cursor.getString(cursor.getColumnIndex("path")));
                out.put("hash_tag", cursor.getString(cursor.getColumnIndex("hash_tag")));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return out;
    }



    // contact 삭제시 쓰는 함수
    public void onContactDelete(String contactId){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("contact", "id = ?", new String[]{contactId});
//        db.delete("contact_hashtag", "id = ?", new String[]{contactId});
    }

    // contact 수정시 쓰는 함수
    public void onEditContact(String contact_id, ContentValues values) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = {contact_id};

        // 테이블 업데이트
        db.update("contact", values, selection, selectionArgs);

//        // 새로운 hashtag 생성 및 저장
//        String existingHashtag = onSearchContactHashTag(contact_id);
//        if (existingHashtag == null || existingHashtag.isEmpty()) {
//            ContentValues newHashtagValues = new ContentValues();
//            newHashtagValues.put("id", contact_id);
//            newHashtagValues.put("hashtag", hashtag);
//            db.insert("contact_hashtag", null, newHashtagValues);
//        }
//        db.update("contact_hashtag", values2, selection, selectionArgs);
    }
    public void onEditContactPath(String contact_id, ContentValues values) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = {contact_id};

        // 테이블 업데이트
        db.update("contact", values, selection, selectionArgs);

    }

//    public void onEditPhotoHashtag(String path, String hashtag){
//        SQLiteDatabase db = this.getReadableDatabase();
//        String existingHashtag = onSearchPhotoHashTag(db, path);
//        if (existingHashtag == null || existingHashtag.isEmpty()) {
//            ContentValues changeHashtag = new ContentValues();
//            changeHashtag.put("path", path);
//            changeHashtag.put("hashtag", hashtag); // 여기에 새로운 hashtag 값을 넣어줘
//            db.insert("photo_hashtag", null, changeHashtag);
//        } else {
//            String selection = "path = ?";
//            String[] selectionArgs = { path };
//            ContentValues newHashtagValues = new ContentValues();
//            newHashtagValues.put("hashtag", hashtag);
//            db.update("photo_hashtag", newHashtagValues, selection, selectionArgs);
//        }
//        existingHashtag = onSearchPhotoHashTag(db, path);
//
//    }

    // search fragment에서 결과 불러올 때 쓰는 함수
    public List<Map<String, String>> search(String query) {
        List<Map<String, String>> searchResults = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // 검색 쿼리
        String selection = "name LIKE ? OR phone_num LIKE ? OR hash_tag LIKE ?";
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%", "%" + query + "%"};
        Cursor cursor = db.query(
                "contact",
                new String[]{"id", "name", "phone_num"},
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // 결과 추가
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phoneNum = cursor.getString(cursor.getColumnIndex("phone_num"));

                Map<String, String> contact1 = new HashMap<>();
                contact1.put("id", id);
                contact1.put("name", name);
                contact1.put("phoneNum", phoneNum);
                searchResults.add(contact1);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return searchResults;
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
