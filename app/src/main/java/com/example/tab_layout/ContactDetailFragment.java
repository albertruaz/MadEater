package com.example.tab_layout;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.database.sqlite.SQLiteDatabase;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactDetailFragment extends Fragment {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    DataUpdateListener updateListener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        updateListener = (DataUpdateListener) context;
    }

    public static ContactDetailFragment newInstance(String name, String phoneNum, String id) {
        ContactDetailFragment fragment = new ContactDetailFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("phoneNum", phoneNum);
        args.putString("contactId", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // db 설정
        dbHelper = ((MainActivity) getActivity()).getDbHelper();
        db = dbHelper.getWritableDatabase();

        // 화면에 대한 설정
        View view = inflater.inflate(R.layout.fragment_contact_detail, container, false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateListener.onDataUpdated();
                fragmentManager.popBackStack();
            }
        });

        // 이름 및 전화번호 띄우기
        TextView detailNameTextView = view.findViewById(R.id.detailNameTextView);
        EditText detailNameEditText = view.findViewById(R.id.detailNameEditText);

        TextView detailPhoneNumTextView = view.findViewById(R.id.detailPhoneNumTextView);
        EditText detailphoneNumEditText = view.findViewById(R.id.detailphoneNumEditText);

        TextView hashTagTextView = view.findViewById(R.id.detailHashTagTextView);

        // 각종 버튼들
        Button deleteButton = view.findViewById(R.id.deleteContactButton);
        Button editContactButton = view.findViewById(R.id.editContactButton);
        Button saveContactButton = view.findViewById(R.id.saveContactButton);
        Button callButton = view.findViewById(R.id.callButton);
        Button messageButton = view.findViewById(R.id.textButton);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name", "");
            String phoneNum = args.getString("phoneNum", "");
            String contactId = args.getString("contactId", "");
            String contactHashTag = dbHelper.onSearchContactHashTag(db,contactId);
            detailNameTextView.setText(name);
            detailPhoneNumTextView.setText(phoneNum);
            hashTagTextView.setText(contactHashTag);
        }

        //삭제 버튼 띄우기
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContactFromDatabase();
//                reloadContactFragment();
                fragmentManager.popBackStack();
            }

            // 연락처 삭제 메소드
            private void deleteContactFromDatabase() {
                Bundle args = getArguments();
                if (args != null) {
                    String name = args.getString("name", "");
                    String phoneNum = args.getString("phoneNum", "");
                    String contactId = args.getString("contactId", "");
                    dbHelper.onContactDelete(db, name, phoneNum,contactId);
                    updateListener.onDataUpdated();
                }
            }

            private void reloadContactFragment() {
                if (getActivity() != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    // 기존의 ContactFragment를 제거
                    Fragment existingFragment = fragmentManager.findFragmentByTag("contact_fragment_tag");
                    if (existingFragment != null) {
                        fragmentTransaction.remove(existingFragment);
                    }

                    // 새로운 ContactFragment를 추가
                    ContactDetailFragment newContactFragment = new ContactDetailFragment();
                    fragmentTransaction.replace(R.id.fragment_container, newContactFragment, "contact_fragment_tag");

                    // 트랜잭션을 커밋하여 변경사항을 적용
                    fragmentTransaction.commit();
                }
            }
        });

        // "EDIT" 버튼 클릭 시 동작 구현
        editContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailNameTextView.setVisibility(View.GONE);
                detailNameEditText.setVisibility(View.VISIBLE);
                detailNameEditText.setText(detailNameTextView.getText());

                detailPhoneNumTextView.setVisibility(View.GONE);
                detailphoneNumEditText.setVisibility(View.VISIBLE);
                detailphoneNumEditText.setText(detailPhoneNumTextView.getText());
            }
        });

        // "EDIT" 버튼 클릭 이후 저장버튼 구현
        saveContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 수정사항 저장될 수 있게
                String contactId = args.getString("contactId", "");

                ContentValues values = new ContentValues();
                values.put("name", detailNameEditText.getText().toString());
                values.put("phone_num", detailphoneNumEditText.getText().toString());

                dbHelper.onEditContact(db, contactId, values);
                updateListener.onDataUpdated();

                detailNameEditText.setVisibility(View.GONE);
                detailNameTextView.setVisibility(View.VISIBLE);
                detailNameTextView.setText(detailNameEditText.getText());

                detailphoneNumEditText.setVisibility(View.GONE);
                detailPhoneNumTextView.setVisibility(View.VISIBLE);
                detailPhoneNumTextView.setText(detailphoneNumEditText.getText());


            }
        });


        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 전화 버튼 클릭 시 전화 앱을 열도록 함
                String phoneNum = args.getString("phoneNum", "");
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum));
                startActivity(dialIntent);
            }
        });

        // 문자 보내기 버튼 클릭 시 동작
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 문자 보내기 동작 구현
                String phoneNum = args.getString("phoneNum", "");
                Intent smsIntent = new Intent(Intent.ACTION_SEND);
                smsIntent.setType("text/plain");
                smsIntent.putExtra(Intent.EXTRA_TEXT, "");
                smsIntent.putExtra("address", phoneNum);

                startActivity(smsIntent);
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

