package com.example.tab_layout;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Contact extends Fragment implements DataUpdateListener {

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ContactAdapter adapter;
    Button addContactButton;
    public Contact() {
        // Required empty public constructor
    }
    @Override
    public void onDataUpdated() {
        if (adapter != null) {
//            adapter.notifyDataSetChanged();
            List<Map<String, String>> contactList = dbHelper.onSearchContact(db);
            addContactButton.setVisibility(View.VISIBLE);
            adapter.updateData(contactList);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // db 설정
        dbHelper = ((MainActivity) getActivity()).getDbHelper();
        db = dbHelper.getWritableDatabase();
        // db에서 가져오기
        List<Map<String, String>> contactList = dbHelper.onSearchContact(db);

        // view 및 adapter 연락
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        adapter = new ContactAdapter(
                getActivity(),
                contactList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "phoneNum"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        // 버튼에 대한 설정(클릭시 새로운 창 생성, 제출 기능)
        addContactButton = view.findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Contact");
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
                builder.setView(dialogView);
                final EditText dialogNameEditText = dialogView.findViewById(R.id.dialogNameEditText);
                final EditText dialogPhoneNumEditText = dialogView.findViewById(R.id.dialogPhoneNumEditText);
                // 새로운 창 내에서 제출 기능
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 새로운 연락처를 생성
                        Map<String, String> newContact = new HashMap<>();
                        newContact.put("name", dialogNameEditText.getText().toString());
                        newContact.put("phoneNum", dialogPhoneNumEditText.getText().toString());
                        contactList.add(newContact);
                        updateDb(newContact);
                        adapter.notifyDataSetChanged();
                    }
                });
                // 새로운 창 내에서 취소
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // 리스트 뷰에 대한 설정
        ListView listContact = view.findViewById(R.id.listContact);
        listContact.setAdapter(adapter);
        // 클릭시 상세정보 띄우기
        listContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addContactButton.setVisibility(View.GONE);
                Map<String, String> clickedContact = contactList.get(position);
                String name = clickedContact.get("name");
                String phoneNum = clickedContact.get("phoneNum");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, ContactDetailFragment.newInstance(name, phoneNum),"data_display_fragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void updateDb(Map<String, String> newContact) {
        ContentValues values = new ContentValues();
        values.put("name", newContact.get("name"));
        values.put("phone_num", newContact.get("phoneNum"));
        dbHelper.onUpgradeContact(db, values);
    }

}