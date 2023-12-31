package com.example.tab_layout;

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

public class ContactDetailFragment extends Fragment {

    public static ContactDetailFragment newInstance(String name, String phoneNum) {
        ContactDetailFragment fragment = new ContactDetailFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("phoneNum", phoneNum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_detail, container, false);

        TextView nameTextView = view.findViewById(R.id.detailNameTextView);
        TextView phoneNumTextView = view.findViewById(R.id.detailPhoneNumTextView);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name", "");
            String phoneNum = args.getString("phoneNum", "");

            nameTextView.setText(name);
            phoneNumTextView.setText(phoneNum);
        }

        Button deleteButton = view.findViewById(R.id.deleteContactButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 여기에서 연락처 삭제 로직을 추가하세요.
                deleteContactFromDatabase();

                // 삭제 후에는 프래그먼트를 닫아주는 코드를 추가할 수 있습니다.
                reloadContactFragment();

            }

            // 연락처 삭제 메소드
            private void deleteContactFromDatabase() {
                Bundle args = getArguments();
                if (args != null) {
                    String name = args.getString("name", "");
                    String phoneNum = args.getString("phoneNum", "");

                    // 여기에서 데이터베이스에서 연락처를 삭제하는 로직을 추가하세요.
                    // DBHelper 등을 사용하여 데이터베이스에서 삭제할 수 있습니다.
                    // 아래는 가상의 예시 코드일 뿐 실제 데이터베이스에 맞게 수정이 필요합니다.

                    DBHelper dbHelper = new DBHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    // 예시: 이름과 전화번호가 일치하는 데이터 삭제
                    db.delete("contact", "name = ? AND phone_num = ?", new String[]{name, phoneNum});

                    db.close();
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
                    fragmentTransaction.add(R.id.fragment_container, newContactFragment, "contact_fragment_tag");

                    // 트랜잭션을 커밋하여 변경사항을 적용
                    fragmentTransaction.commit();
                }
            }
        });



        return view;
    }
}

