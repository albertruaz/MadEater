package com.example.tab_layout;

import android.content.ContentValues;
import android.content.Context;
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

    DataUpdateListener updateListener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        updateListener = (DataUpdateListener) context;
    }
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
    public void add_data(){
        dbHelper.onUpgrade(db,1,1);
        String[] name = {
                "청춘",
                "궁손칼국수",
                "세번째우물",
                "엉클부대찌개",
                "슬림비빔밥",
                "매력에 취한 밤"
        };
        String[] phoneNum = {
                "042-862-3628",
                "042-861-1551",
                "0507-1427-5200",
                "042-867-0102",
                "0507-1478-0429",
                "0507-1473-0774"
        };
        String[] path = {
                "file:///android_asset/images/1.png",
                "file:///android_asset/images/2.png",
                "file:///android_asset/images/3.png",
                "file:///android_asset/images/4.png",
                "file:///android_asset/images/5.png",
                "file:///android_asset/images/6.png"
        };
        String[] hashtag = {
                "#한식 #점심 #저렴 #가성비",
                "#한식 #면 #칼국수",
                "#한식 #닭 #든든",
                "#한식 #부대찌개 #부대전골",
                "#한식 #비빔밥 #죽",
                "#한식 #분위기 #술"
        };


        ContentValues newHashtagValues = new ContentValues();
        for(int i=0; i < 6; i++){
            newHashtagValues.put("name", name[i]);
            newHashtagValues.put("phone_num", phoneNum[i]);
            newHashtagValues.put("path", path[i]);
            newHashtagValues.put("hash_tag", hashtag[i]);
            dbHelper.onUpgradeContact(db, newHashtagValues);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // db 설정
        dbHelper = ((MainActivity) getActivity()).getDbHelper();
        db = dbHelper.getWritableDatabase();
        // db에서 가져오기
//        add_data();

        List<Map<String, String>> contactList = dbHelper.onSearchContact(db);
        List<Map<String, String>> contactList1 = dbHelper.onSearchContact(db);

        // view 및 adapter 연락
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        adapter = new ContactAdapter(
                getActivity(),
                contactList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "phoneNum"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        // +버튼에 대한 설정(클릭시 새로운 창 생성, 제출 기능)
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
                        ContentValues values = new ContentValues();
                        values.put("name", dialogNameEditText.getText().toString());
                        values.put("phone_num", dialogPhoneNumEditText.getText().toString());
                        dbHelper.onUpgradeContact(db, values);
                        updateListener.onDataUpdated();
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
                String idd = clickedContact.get("id");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, ContactDetailFragment.newInstance(idd),"data_display_fragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

}