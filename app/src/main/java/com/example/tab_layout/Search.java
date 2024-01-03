package com.example.tab_layout;

import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;
import java.util.Map;

// SearchFragment.java
public class Search extends Fragment implements DataUpdateListener {

    private SearchView searchView;
    private ListView searchResultsListView;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ContactAdapter adapter;

    public Search() {
        // Required empty public constructor
    }
    @Override
    public void onDataUpdated() {
        searchResultsListView.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        dbHelper = ((MainActivity) getActivity()).getDbHelper();
        db = dbHelper.getWritableDatabase();

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
        List<Map<String, String>> searchResults = getSearchResults(query);

        // 어댑터를 사용하여 검색 결과를 리스트뷰에 설정
        adapter = new ContactAdapter(
                getActivity(),
                searchResults,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "phoneNum"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        searchResultsListView.setAdapter(adapter);

        searchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //List<Map<String, String>> contactList = dbHelper.onSearchContact(db);
                searchResultsListView.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                Map<String, String> clickedContact = searchResults.get(position);
                String idd = clickedContact.get("id");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container_search, ContactDetailFragment.newInstance(idd),"data_display_fragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    private List<Map<String, String>> getSearchResults(String query) {
        List<Map<String, String>> searchResults = dbHelper.search(query);
        return searchResults;
    }
}