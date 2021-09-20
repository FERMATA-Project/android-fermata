package com.example.fermata.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.fermata.R;

// 설명: 재생목록에 노래 추가하기 화면
// author: seungyeon, last modified: 21.08.03
public class MakePlaylistActivity extends AppCompatActivity {
    String make_list_name;
    public static Activity makePlaylistActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_playlist);
        makePlaylistActivity = MakePlaylistActivity.this;

        AppCompatButton btn_list_name = findViewById(R.id.btn_list_name); // 재생목록 추가 버튼
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
                make_list_name = et_list_name.getText().toString();
                if(make_list_name.equals("")) {
                    Toast.makeText(getApplicationContext(), "재생목록이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else if(make_list_name.equals("현재 재생 목록")) {
                    Toast.makeText(getApplicationContext(), "현재 재생 목록은 \n재생목록이름으로 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
                }else if(make_list_name.equals("좋아요한 음악목록")) {
                    Toast.makeText(getApplicationContext(), "좋아요한 음악목록은 \n재생목록이름으로 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
                } else{
                    Intent MaketoAdd = new Intent(getApplicationContext(), AddPlaylistActivity.class);
                    MaketoAdd.putExtra("재생목록이름", make_list_name);
                    startActivity(MaketoAdd);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_close, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_close:
                finish(); // 액티비티 종료
                break;
        }

        return  true;
    }
}
