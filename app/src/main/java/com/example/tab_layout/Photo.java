package com.example.tab_layout;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;

public class Photo extends Fragment {

    public Photo() {
        // Required empty public constructor
    }
    public class MyGridAdapter extends BaseAdapter {
        Context context;
        public MyGridAdapter(Context c){
            context = c;
        }

        Integer[] picID = {
                R.drawable.image_1,R.drawable.image_2,R.drawable.image_3,R.drawable.image_4,
                R.drawable.image_1,R.drawable.image_2,R.drawable.image_3,R.drawable.image_4,
                R.drawable.image_1,R.drawable.image_2,R.drawable.image_3,R.drawable.image_4,
                R.drawable.image_1,R.drawable.image_2,R.drawable.image_3,R.drawable.image_4,
                R.drawable.image_1,R.drawable.image_2,R.drawable.image_3,R.drawable.image_4,
                R.drawable.image_1,R.drawable.image_2,R.drawable.image_3,R.drawable.image_4,
                R.drawable.image_1,R.drawable.image_2,R.drawable.image_3,R.drawable.image_4,
                R.drawable.image_1,R.drawable.image_2,R.drawable.image_3,R.drawable.image_4,
                R.drawable.image_1,R.drawable.image_2,R.drawable.image_3,R.drawable.image_4,
        };

        // BaseAdapter를 상속받은 클래스가 구현해야 할 함수들은
        // { getCount(), getItem(), getItemId(), getView() }
        // Ctrl + i 를 눌러 한꺼번에 구현할 수 있습니다.
        @Override
        public int getCount() {
            return picID.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200,300));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(5,5,5,5);

            imageView.setImageResource(picID[i]);
            return imageView;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        GridView gv = view.findViewById(R.id.gridView);
        MyGridAdapter gAdapter = new MyGridAdapter(getActivity());

        gv.setAdapter(gAdapter);


        return view;
    }
}