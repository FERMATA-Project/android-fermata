package com.example.fermata.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fermata.R;

// 설명: 재생목록에 노래 추가하기 화면
// author: seungyeon, last modified: 21.08.03
public class MakePlaylistActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_playlist);

        Button btn_list_name = findViewById(R.id.btn_list_name); // 재생목록 추가 버튼
        EditText et_list_name = findViewById(R.id.et_list_name); // 재생목록 이름 입력 창


        // 엔터키 이벤트 처리
        et_list_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //키패드 내리기
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(et_list_name.getWindowToken(), 0);

                    btn_list_name.performClick();

                    return true;
                }
                return false;
            }
        });

        btn_list_name.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPlaylistActivity.class));
            }
        });
    }
}