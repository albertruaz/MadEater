package com.example.tab_layout;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class Photo extends Fragment {

    public Photo() {
        // Required empty public constructor
    }
    public class MyGridAdapter extends BaseAdapter {
        Context context;
        String[] files = null;

        public MyGridAdapter(Context c){
            context = c;
            AssetManager assetManager = getActivity().getAssets();

            try {
                files = assetManager.list("images");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("try");
            for(int j=0;j<files.length;j++)
                System.out.println(files[j]);

            List<String> list = Arrays.asList(files);
            files = list.subList(1, 10).toArray(new String[0]);

        }

        @Override
        public int getCount() {
            return files.length;
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
            imageView.setLayoutParams(new ViewGroup.LayoutParams(300,300));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(5,5,5,5);

            AssetManager assetManager = getContext().getAssets();
            try {
                InputStream is = assetManager.open("images/"+files[i]);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

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