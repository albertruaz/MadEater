package com.example.tab_layout;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;


public class FullScreenImageFragment extends Fragment {
    private String imagePath;

    private OnBackPressedCallback onBackPressedCallback;


    public FullScreenImageFragment(String imagePath) {
        this.imagePath = imagePath;
    }

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentManager.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // db 설정
        dbHelper = ((MainActivity) getActivity()).getDbHelper();
        db = dbHelper.getWritableDatabase();

        //view 가져오기 및 화면 클릭시 뒤로가기
        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);
        ImageView fullScreenImageView = view.findViewById(R.id.fullScreenImageView);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fullScreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });

        // 이미지 가져오기
        Glide.with(requireContext())
                .load(imagePath)
                .into(fullScreenImageView);

        // 해시태그? 일단 가져오기
        String contactHashTag = dbHelper.onSearchPhotoHashTag(db, imagePath);
        TextView titleView = view.findViewById(R.id.hashtagView);
        titleView.setText(contactHashTag);

        TextView hashtagView = view.findViewById(R.id.hashtagView);
        EditText hashtagEdit = view.findViewById(R.id.hashtagEdit);

        // "EDIT" 버튼
        Button editButton = view.findViewById(R.id.editButton);
        Button saveButton = view.findViewById(R.id.saveButton);
        ListView contactlistView = view.findViewById(R.id.contactlistView);
        List<Map<String, String>> contactList = dbHelper.onSearchContact(db);

        ContactAdapter adapter = new ContactAdapter(
                getActivity(),
                contactList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "phoneNum"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        contactlistView.setAdapter(adapter);
        contactlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> clickedContact = contactList.get(position);
                String idd = clickedContact.get("id");
                ContentValues newHashtagValues = new ContentValues();
                newHashtagValues.put("path", imagePath);
                dbHelper.onEditContactPath(idd,newHashtagValues);
                fullScreenImageView.setVisibility(View.VISIBLE);
                contactlistView.setVisibility(View.GONE);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                editButton.setVisibility(View.GONE);
//                saveButton.setVisibility(View.VISIBLE);
//
//                hashtagView.setVisibility(View.GONE);
//                hashtagEdit.setVisibility(View.VISIBLE);
//                hashtagEdit.setText(hashtagView.getText());
                fullScreenImageView.setVisibility(View.GONE);
                contactlistView.setVisibility(View.VISIBLE);
            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                String hashtag = hashtagEdit.getText().toString();
                dbHelper.onEditPhotoHashtag(imagePath, hashtag);
                hashtagEdit.setVisibility(View.GONE);
                hashtagView.setVisibility(View.VISIBLE);
                hashtagView.setText(hashtagEdit.getText());
            }
        });

        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onBackPressedCallback.remove();
    }
}

