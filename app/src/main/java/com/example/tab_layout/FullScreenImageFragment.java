package com.example.tab_layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import com.example.tab_layout.R;

public class FullScreenImageFragment extends Fragment {
    private String imagePath;

    public FullScreenImageFragment(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);
        ImageView fullScreenImageView = view.findViewById(R.id.fullScreenImageView);

        // 이미지 경로를 받아와서 이미지 표시
        Glide.with(requireContext())
                .load("file:///android_asset/images/" + imagePath)
                .into(fullScreenImageView);

        return view;
    }
}

