package com.example.tab_layout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;



public class FullScreenImageFragment extends Fragment {
    private String imagePath;

    public FullScreenImageFragment(String imagePath) {
        this.imagePath = imagePath;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);
        ImageView fullScreenImageView = view.findViewById(R.id.fullScreenImageView);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fullScreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });

        // 이미지 경로를 받아와서 이미지 표시
        Glide.with(requireContext())
                .load("file:///android_asset/images/" + imagePath)
                .into(fullScreenImageView);

        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(imagePath);

        return view;
    }
}

