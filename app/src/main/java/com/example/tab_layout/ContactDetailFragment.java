package com.example.tab_layout;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import com.example.tab_layout.DBHelper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
        TextView nameTextView = view.findViewById(R.id.detailNameTextView);
        TextView phoneNumTextView = view.findViewById(R.id.detailPhoneNumTextView);
        TextView hashTagTextView = view.findViewById(R.id.detailHashTagTextView);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name", "");
            String phoneNum = args.getString("phoneNum", "");
            String contactId = args.getString("contactId", "");
            String contactHashTag = dbHelper.onSearchContactHashTag(db,contactId);
            nameTextView.setText(name);
            phoneNumTextView.setText(phoneNum);
            hashTagTextView.setText(contactHashTag);
        }

        //삭제 버튼 띄우기
        Button deleteButton = view.findViewById(R.id.deleteContactButton);
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

        return view;
    }
}

