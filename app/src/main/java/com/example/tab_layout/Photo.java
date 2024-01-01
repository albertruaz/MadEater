package com.example.tab_layout;

import static java.security.AccessController.getContext;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tab_layout.FullScreenImageFragment;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
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



import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


public class Photo extends Fragment implements DataUpdateListener {

    public Photo() {
    }
    private GridAdapter adapter;

    public class GridAdapter extends BaseAdapter {
        private Context context;
        private String[] files = null;
        private ImageView imageView;
        private AssetManager assetManager;
        private int screenWidth;
        private int imageSize;

        public int getScreenWidth() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }

        public GridAdapter(Context c){
            context = c;
            assetManager = this.context.getAssets();

            screenWidth = getScreenWidth();
            imageSize = screenWidth / 3;

            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(imageSize,imageSize));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(0,0,0,0);

            try {
                files = assetManager.list("images");
            } catch (IOException e) {
                e.printStackTrace();
            }
//            List<String> list = Arrays.asList(files);
//            files = list.subList(1, 10).toArray(new String[0]);

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
            ImageView imageView = (ImageView) view;

            if (imageView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(imageSize,imageSize));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(0,0,0,0);
            }

            Glide.with(context)
                    .load("file:///android_asset/images/" + files[i])
                    .centerCrop() // 이미지 중앙을 기준으로 잘라냄
                    .into(imageView);

            // 이미지 클릭 이벤트 처리
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFullScreenImage(files[i]);
                }
            });

            return imageView;
        }
    }

    @Override
    public void onDataUpdated() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void showFullScreenImage(String imagePath) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FullScreenImageFragment fullScreenImageFragment = new FullScreenImageFragment(imagePath);
        fragmentTransaction.replace(android.R.id.content, fullScreenImageFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // view생성, fragment기 때문에
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        GridView gridView = view.findViewById(R.id.gridView); //xml 부분 가져오기

        adapter = new GridAdapter(getActivity()); // Adapter 데이터 포함
        gridView.setAdapter(adapter);

        return view;
    }

}