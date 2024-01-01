package com.example.tab_layout;

import android.content.Context;
import android.os.Bundle;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ContactDetailFragment extends Fragment {

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    DataUpdateListener updateListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        updateListener = (DataUpdateListener) context;
    }

    public static ContactDetailFragment newInstance(String name, String phoneNum, int id) {
        ContactDetailFragment fragment = new ContactDetailFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("phoneNum", phoneNum);
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
        TextView nameTextView = view.findViewById(R.id.detailNameTextView);
        TextView phoneNumTextView = view.findViewById(R.id.detailPhoneNumTextView);
        Button deleteButton = view.findViewById(R.id.deleteContactButton);
        Button callButton = view.findViewById(R.id.callButton);
        Button messageButton = view.findViewById(R.id.textButton);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name", "");
            String phoneNum = args.getString("phoneNum", "");

            nameTextView.setText(name);
            phoneNumTextView.setText(phoneNum);
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
                    db.delete("contact", "name = ? AND phone_num = ?", new String[]{name, phoneNum});
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
}

