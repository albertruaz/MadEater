package com.example.tab_layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

// SearchFragment.java
public class Search extends Fragment implements DataUpdateListener {

    private SearchView searchView;
    private ListView searchResultsListView;

    public Search() {
        // Required empty public constructor
    }
    @Override
    public void onDataUpdated() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        searchResultsListView = view.findViewById(R.id.searchResultsListView);

        // 검색 기능 구현
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색어를 이용하여 검색 수행 및 결과 표시
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색어가 변경될 때마다 호출되는 메서드
                // 여기에서 실시간으로 검색 결과를 업데이트할 수 있음
                return false;
            }
        });

        return view;
    }

    private void performSearch(String query) {
        // 검색어를 사용하여 연락처나 갤러리에서 검색하고 결과를 리스트뷰에 표시하는 로직을 작성
        // 예: SQLite에서 연락처 검색, 갤러리에서 이미지 검색 후 결과를 어댑터에 설정
        // 예제에서는 가상의 메서드로 대체
        List<String> searchResults = getSearchResults(query);

        // 어댑터를 사용하여 검색 결과를 리스트뷰에 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, searchResults);
        searchResultsListView.setAdapter(adapter);
    }

    private List<String> getSearchResults(String query) {
        // 가상의 메서드: 실제로는 여기에서 데이터베이스나 갤러리에서 검색
        // 검색 결과를 리스트로 반환
        List<String> results = new ArrayList<>();
        results.add("Result 1");
        results.add("Result 2");
        // ...

        return results;
    }
}