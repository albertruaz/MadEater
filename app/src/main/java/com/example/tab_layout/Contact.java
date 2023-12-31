package com.example.tab_layout;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Contact extends Fragment {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public Contact() {
        // Required empty public constructor
    }


    public List<Map<String, String>> extract() {
        try {
            //jsonString 추출
            InputStream inputStream = getResources().openRawResource(R.raw.contact_data);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder StringBuilder = new StringBuilder();
            for (int data = reader.read(); data != -1; data = reader.read()) {
                StringBuilder.append((char) data);
            }
            String jsonString = StringBuilder.toString();

            //jsonString 파싱
            List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            for (JsonNode addressNode : rootNode.path("contact")) {
                String name = addressNode.path("name").asText();
                String phoneNum = addressNode.path("phone_num").asText();

                Map<String, String> contact = new HashMap<String, String>(2);
                contact.put("name", name);
                contact.put("phoneNum", phoneNum);
                contactList.add(contact);
            }
            return contactList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        List<Map<String, String>> contactList = extract();

        List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();

        // DB에서 가져오기
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("contact", new String[]{"id", "name", "phone_num"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phoneNum = cursor.getString(cursor.getColumnIndex("phone_num"));
                Map<String, String> contact = new HashMap<String, String>(2);
                contact.put("name", name);
                contact.put("phoneNum", phoneNum);
                System.out.println(name);
                System.out.println(phoneNum);

                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();

        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        SimpleAdapter adapter = new SimpleAdapter(
                getActivity(),
                contactList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "phoneNum"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        // 리스트 뷰 찾기 및 어댑터 설정
        ListView listContact = view.findViewById(R.id.listContact);
        listContact.setAdapter(adapter);

        Button addContactButton = view.findViewById(R.id.addContactButton);


        // 버튼 클릭 시의 동작 설정
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Contact");

                // 다이얼로그에 사용될 레이아웃 inflate
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
                builder.setView(dialogView);

                final EditText dialogNameEditText = dialogView.findViewById(R.id.dialogNameEditText);
                final EditText dialogPhoneNumEditText = dialogView.findViewById(R.id.dialogPhoneNumEditText);

                // 다이얼로그의 확인 버튼 동작 설정
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 새로운 연락처를 생성
                        Map<String, String> newContact = new HashMap<>();
                        newContact.put("name", dialogNameEditText.getText().toString());
                        newContact.put("phoneNum", dialogPhoneNumEditText.getText().toString());

                        // 연락처 목록에 추가
                        contactList.add(newContact);

                        // JSON 파일 업데이트
                        updateDb(newContact);

                        // 어댑터 갱신
                        adapter.notifyDataSetChanged();
                    }
                });


                // 다이얼로그의 취소 버튼 동작 설정
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // 다이얼로그 표시
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return view;
    }

    private void updateDb(Map<String, String> newContact) {
        ContentValues values = new ContentValues();
        values.put("name", newContact.get("name"));
        values.put("phone_num", newContact.get("phoneNum"));
        dbHelper.onUpgrade(db, values);
    }

}