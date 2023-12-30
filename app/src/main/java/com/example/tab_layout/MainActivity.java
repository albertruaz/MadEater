package com.example.tab_layout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.FrameLayout;


import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 탭 부분 xml 구현 -
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Contact"));
        tabLayout.addTab(tabLayout.newTab().setText("Photo"));
        tabLayout.addTab(tabLayout.newTab().setText("NBA"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //adapter 생성
        MyAdapter adapter = new MyAdapter(this,getSupportFragmentManager(),tabLayout.getTabCount());

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