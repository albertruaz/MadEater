package com.example.tab_layout;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ContactAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> data;
    private int resource;
    private String[] from;
    private int[] to;

    public ContactAdapter(Context context, List<Map<String, String>> data, int resource, String[] from, int[] to) {
        this.context = context;
        this.data = data;
        this.resource = resource;
        this.from = from;
        this.to = to;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Map<String, String> getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Map<String, String> currentItem = getItem(position);
        for (int i = 0; i < from.length; i++) {
            String key = from[i];
            int viewId = to[i];
            TextView textView = convertView.findViewById(viewId);
            textView.setText(currentItem.get(key));
        }
        return convertView;
    }

    // 데이터를 업데이트하는 메서드
    public void updateData(List<Map<String, String>> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }
}

