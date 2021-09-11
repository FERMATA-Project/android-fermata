package com.example.fermata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.fermata.RetrofitClient;
import com.example.fermata.domain.Music;
import com.example.fermata.response.musicResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 설명: 음악 즐기기 화면에서 재생 목록 추가 선택 -> 새로운 재생 목록 만들기 -> 재생 목록 추가 화면
// author: soohyun, last modified: 21.09.10
public class MakePlaylist2Activity extends AppCompatActivity {
    String make_list_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_playlist);

        AppCompatButton btn_list_name = findViewById(R.id.btn_list_name); // 재생목록 추가 버튼
        EditText et_list_name = findViewById(R.id.et_list_name); // 재생목록 이름 입력창

        // SelectPlaylistActivity 로부터 받은 데이터
        Intent intent = getIntent();
        int music_id = intent.getIntExtra("music_id", 1);

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
                make_list_name = et_list_name.getText().toString().trim();

                if(make_list_name.equals("")) {
                    Toast.makeText(getApplicationContext(), "재생목록이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else{
                    RetrofitClient.getApiService().requestAddMusic(make_list_name, music_id).enqueue(new Callback<musicResponse>() {
                        @Override
                        public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                            if(response.isSuccessful()){
                                musicResponse result = response.body(); // 응답 결과

                                if(result.code.equals("400")) {
                                    Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                                } else if (result.code.equals("200")) {
                                    // 토스트 메시지 띄우기
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setView(view.inflate(getApplicationContext(), R.layout.playlist_music_add_toast, null));
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<musicResponse> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(getApplicationContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
                        }
                    });
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
