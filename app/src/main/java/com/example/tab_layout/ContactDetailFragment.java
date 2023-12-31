package com.example.tab_layout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

        return view;
    }
}

