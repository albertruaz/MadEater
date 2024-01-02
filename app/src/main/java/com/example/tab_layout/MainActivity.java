package com.example.tab_layout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.FrameLayout;


import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements DataUpdateListener {

    TabLayout tabLayout;
    ViewPager viewPager;

    private DBHelper dbHelper;
    private MyAdapter adapter;

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    public void onDataUpdated() {
        // 데이터 변경을 처리하는 로직
        int currentItem = viewPager.getCurrentItem();
        Fragment currentFragment = (Fragment) adapter.instantiateItem(viewPager, currentItem);
        if (currentFragment != null) {
            ((DataUpdateListener) currentFragment).onDataUpdated();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = DBHelper.getInstance(this);

        // 탭 부분 xml 구현 -
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Contact"));
        tabLayout.addTab(tabLayout.newTab().setText("Photo"));
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //adapter 생성
        adapter = new MyAdapter(this,getSupportFragmentManager(),tabLayout.getTabCount());

        // viewpager 설정(framelayout 썼어도 되긴함)
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
