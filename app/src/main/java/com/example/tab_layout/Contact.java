package com.example.tab_layout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Contact extends Fragment {
    public Contact() {
        // Required empty public constructor
    }

    public List<Map<String, String>> extract() {
        try {
            //jsonString 추출
            InputStream inputStream = getResources().openRawResource(R.raw.contact_data);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder StringBuilder = new StringBuilder();
            for (int data = reader.read(); data != -1; data = reader.read()) {
                StringBuilder.append((char)data);
            }
            String jsonString = StringBuilder.toString();

            //jsonString 파싱
            List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            for (JsonNode addressNode : rootNode.path("contact")) {
                String name = addressNode.path("name").asText();
                String phoneNum = addressNode.path("phone_num").asText();

                Map<String, String> contact = new HashMap<String, String>(2);
                contact.put("name", name);
                contact.put("phoneNum", phoneNum);
                contactList.add(contact);
            }
            System.out.println(contactList.size());
            return contactList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<Map<String, String>> contactList = extract();
        // contact adapter 설정

        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        SimpleAdapter adapter = new SimpleAdapter(
                getActivity(),
                contactList,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "phoneNum"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        // 리스트 뷰 찾기 및 어댑터 설정
        ListView listContact = view.findViewById(R.id.listContact);
        listContact.setAdapter(adapter);

        return view;
    }
}