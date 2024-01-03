package com.example.tab_layout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

public class Map extends Fragment implements DataUpdateListener {

    private View view;

//    private static final int PERMISSION_REQUEST_CODE = 100;
//    private static final String[] PERMISSION = {
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//    };

    private FusedLocationSource mLocationSource;
    private NaverMap mNaverMap;

    public Map() {
    }

//    @Override
//    public  void onCreate(){
//        mLocationSource =
//                new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
//    }
    @Override
    public void onDataUpdated() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull NaverMap naverMap) {

                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(36.36409483542151, 127.35876172153934 ));
                naverMap.moveCamera(cameraUpdate);

                //경로가 제대로 뜨지는 않는 상태
                LocationOverlay locationOverlay = naverMap.getLocationOverlay();
                locationOverlay.setVisible(true);
                locationOverlay.setPosition(new LatLng(36.36409483542151, 127.35876172153934 ));

                // 마커 하나
//                Marker marker = new Marker();
//                marker.setTag("1");
//                marker.setCaptionText("요시다");
//                marker.setPosition(new LatLng(36.363583769325736, 127.35864516758305));
//                marker.setOnClickListener(new Overlay.OnClickListener() {
//                    @Override
//                    public boolean onClick(@NonNull Overlay overlay) {
//                        String clickedMarkerId = (String) overlay.getTag();
//                        onMarkerClicked(clickedMarkerId);
//                        return false;
//                    }
//                });
//                marker.setMap(naverMap);


                String[] name = {
                        "청춘",
                        "궁손칼국수",
                        "세번째우물",
                        "엉클부대찌개",
                        "슬림비빔밥",
                        "매력에 취한 밤"
                };
                LatLng[] lat = {
                        new LatLng(36.36310264257666, 127.3583114800962),
                        new LatLng(36.363419906557965, 127.35693965585136),
                        new LatLng(36.362673540236976, 127.35790839680124),
                        new LatLng(36.363165498300916, 127.35838697800051),
                        new LatLng(36.36372354128543, 127.35861516774486),
                        new LatLng(36.3630357986784, 127.3580632604604)
                };

                Marker[] markers = new Marker[5];
                for(int i=0; i < 6; i++){
                    markers[i] = new Marker();
                    markers[i].setTag(String.valueOf(i+1));
                    markers[i].setCaptionText(name[i]);
                    markers[i].setPosition(lat[i]);
                    markers[i].setMap(naverMap);
                    markers[i].setOnClickListener(new Overlay.OnClickListener() {
                        @Override
                        public boolean onClick(@NonNull Overlay overlay) {
                            String clickedMarkerId = (String) overlay.getTag();
                            onMarkerClicked(clickedMarkerId);
                            return false;
                        }
                    });
                    markers[i].setMap(naverMap);
                }
            }
        });

        return view;
    }
    private void onMarkerClicked(String clickedMarkerId) {
        Activity activity = getActivity();
        if (activity != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_map, ContactDetailFragment.newInstance(clickedMarkerId),"data_display_fragment");
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
