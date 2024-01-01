package com.example.tab_layout;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


import com.bumptech.glide.Glide;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import android.content.Intent;
import android.widget.Button;


public class Photo extends Fragment implements DataUpdateListener {

    public Photo() {
    }
    private GridAdapter adapter;
    private View view;

    private static final int REQUEST_TAKE_PHOTO= 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    public class GridAdapter extends BaseAdapter {
        private Context context;
        private String[] files = null;
        private String[] galleryfiles = new String[0];
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
        }

        @Override
        public int getCount() {
            return files.length+galleryfiles.length;
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
                    .load(selectString(i))
                    .centerCrop() // 이미지 중앙을 기준으로 잘라냄
                    .into(imageView);

            // 이미지 클릭 이벤트 처리
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFullScreenImage(selectString(i));
                }
            });

            return imageView;
        }


        // 데이터를 업데이트하는 메서드
        public void updateFileData(String newData) {
            String[] newArray = new String[galleryfiles.length + 1];
            System.arraycopy(galleryfiles, 0, newArray, 0, galleryfiles.length);
            newArray[galleryfiles.length] = newData;
            galleryfiles = newArray;
        }

        private String selectString(int i) {
            if(i>=files.length){
                return galleryfiles[i-files.length];
            }
            return "file:///android_asset/images/" + files[i];
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
        view = inflater.inflate(R.layout.fragment_photo, container, false);
        GridView gridView = view.findViewById(R.id.gridView); //xml 부분 가져오기

        adapter = new GridAdapter(getActivity()); // Adapter 데이터 포함
        gridView.setAdapter(adapter);

        // 갤러리에서 사진 불러오기 버튼 클릭 이벤트 처리
        ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 인텐트에서 이미지 데이터를 가져옵니다.
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            saveGalleryImage(selectedImageUri);
                        }
                    }
                }
            });

        Button pickFromGalleryButton = view.findViewById(R.id.pickFromGalleryButton);
        pickFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPickFromGalleryIntent();
            }
            // 갤러리에서 이미지 선택
            private void dispatchPickFromGalleryIntent() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mGetContent.launch(intent);
            }
        });

        return view;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ContentResolver contentResolver = getActivity().getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }

    public void saveGalleryImage(Uri imageUri) {
//        Bitmap bitmap = null;
//        try {
//            bitmap = getBitmapFromUri(imageUri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        adapter.updateFileData(imageUri.toString());
        //uri file 스트링에 저장

    }


}