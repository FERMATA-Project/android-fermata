package com.example.fermata.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fermata.R;

public class SelectPlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_playlist);

        // 상단바 안보이게 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
}