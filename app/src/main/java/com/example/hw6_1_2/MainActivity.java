package com.example.hw6_1_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ListView list;
    SwipeRefreshLayout swipeRefreshLayout;
    BaseAdapter adapter;
    private static final String TEXT = "text";
    private static final String SIZE = "size";
    private static final String NOTE = "note";
    private List<Map<String, String>> values = new ArrayList<>();
    private ArrayList<Integer> deletedElements = new ArrayList<>();
    private static final String INDEX = "index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        init();

        if (savedInstanceState != null) {
            deletedElements.addAll(Objects.requireNonNull(savedInstanceState.getIntegerArrayList(INDEX)));
            for (Integer i : deletedElements) {
                values.remove((int) i);
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void init() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.large_text), MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        values.addAll(prepareContent());
        final String s = String.valueOf(values);
        editor.putString(NOTE, s);
        editor.apply();

        adapter = createAdapter(values);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deletedElements.add(position);
                values.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                values.clear();
                deletedElements = new ArrayList<>();

                values.addAll(prepareContent());
                editor.putString(NOTE, s);
                editor.apply();

                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> values) {
        return new SimpleAdapter(this, values, R.layout.texts,
                new String[]{TEXT, SIZE}, new int[]{R.id.textView1, R.id.textView2});
    }

    @NonNull
    private List<Map<String, String>> prepareContent() {
        String[] arrayContent = getString(R.string.large_text).split("\n\n");
        List<Map<String, String>> list = new ArrayList<>();

        for (String s : arrayContent) {
            Map<String, String> map = new HashMap<>();
            map.put(TEXT, s);
            map.put(SIZE, s.length() + "");
            list.add(map);
        }
        return list;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(INDEX, deletedElements);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}