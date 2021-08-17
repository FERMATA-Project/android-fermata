package com.example.fermata.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fermata.R;
import com.example.fermata.RetrofitClient;
import com.example.fermata.adapter.MusicAdapter;
import com.example.fermata.domain.Music;
import com.example.fermata.response.musicResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 설명: 상단바의 검색 아이콘 클릭 -> 음악 검색 화면
// author: soohyun, last modified: 21.08.09
public class SearchActivity extends AppCompatActivity {
    ArrayList<Music> musicList = new ArrayList<>(); // 검색된 음악 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        View line = findViewById(R.id.line);
        line.setVisibility(View.GONE); // 구분선 안보이게

        EditText et_search = findViewById(R.id.et_search); // 음악 검색창
        // 엔터키 이벤트 처리
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //키패드 내리기
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(et_search.getWindowToken(), 0);

                    RecyclerView rv_search_musicList = findViewById(R.id.rv_search_musicList); // 음악 목록 리사이클러뷰
                    LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
                    MusicAdapter adapter = new MusicAdapter(getApplicationContext(), musicList);
                    rv_search_musicList.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
                    rv_search_musicList.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결
                    rv_search_musicList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));

                    String search_word = et_search.getText().toString().trim(); // 검색한 단어

                    // 서버에 검색된 음악 정보를 가져옴
                    RetrofitClient.getApiService().requestSearch(search_word).enqueue(new Callback<musicResponse>() {
                        @Override
                        public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                            if(response.isSuccessful()){
                                musicResponse result = response.body(); // 응답 결과

                                if(result.code.equals("400")) {
                                    Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                                } else if (result.code.equals("200")) {
                                    List<Music> musics = result.music; // 음악 리스트

                                    if(musics.size() == 0) {
                                        Toast.makeText(getApplicationContext(), "검색된 음악이 없습니다.", Toast.LENGTH_SHORT).show();
                                    }

                                    musicList.clear(); // 음악 목록 리스트 초기화
                                    for(Music music: musics){
                                        musicList.add(music);
                                    }
                                    adapter.notifyDataSetChanged();
                                    line.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<musicResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return true;
                }
                return false;
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