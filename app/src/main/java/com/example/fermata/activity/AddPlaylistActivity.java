package com.example.fermata.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fermata.R;
import com.example.fermata.RetrofitClient;
import com.example.fermata.adapter.AddMusicAdapter;
import com.example.fermata.adapter.MusicAdapter;
import com.example.fermata.domain.Music;
import com.example.fermata.response.musicResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 설명: 재생목록에 노래 추가하기 화면
// author: seungyeon, last modified: 21.08.15

public class  AddPlaylistActivity extends AppCompatActivity {
    ArrayList<Music> AddPlaylist = new ArrayList<>();
    AddMusicAdapter adapter;
    String make_list_title;
    ImageView search_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);

        // 상단바 안보이게 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //리스트 이름 가져오기
        Intent AddforMake = getIntent();
        make_list_title = AddforMake.getStringExtra("재생목록이름");
        Toast.makeText(getApplicationContext(), make_list_title, Toast.LENGTH_SHORT).show();

        requestMusicAlphabet(); // 음악 전체곡 알파벳순서로 가져오기

        RecyclerView rv_add_playlist = findViewById(R.id.rv_add_playlist); // 현재 재생 목록 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false); // 레이아웃 매니저
        adapter = new AddMusicAdapter(getApplicationContext(), AddPlaylist, make_list_title);
        rv_add_playlist.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_add_playlist.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결
        rv_add_playlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));

        /*
        adapter.setOnItemClickListener(new AddMusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                final Call<com.example.fermata.domain.AddPlaylist> addplaylist = RetrofitClient.getApiService().requestAddPlaylist(make_list_title, AddPlaylist.get(position).getMusic_id());

                addplaylist.enqueue(new Callback<com.example.fermata.domain.AddPlaylist>() {
                    @Override
                    public void onResponse(Call<com.example.fermata.domain.AddPlaylist> call, Response<com.example.fermata.domain.AddPlaylist> response) {
                        final AddPlaylist addplaylist = response.body();
                        Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<com.example.fermata.domain.AddPlaylist> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getApplicationContext(), "서버와 통신중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(getApplicationContext(), "추가가 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });*/

        EditText et_search = findViewById(R.id.et_search); // 음악 검색창
        // 엔터키 이벤트 처리
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //키패드 내리기
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(et_search.getWindowToken(), 0);

                    RecyclerView rv_add_playlist = findViewById(R.id.rv_add_playlist); // 음악 목록 리사이클러뷰
                    LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
                    adapter = new AddMusicAdapter(getApplicationContext(), AddPlaylist, make_list_title);
                    rv_add_playlist.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
                    rv_add_playlist.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결
                    rv_add_playlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));

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

                                    AddPlaylist.clear(); // 음악 목록 리스트 초기화
                                    for(Music music: musics){
                                        AddPlaylist.add(music);
                                    }
                                    adapter.notifyDataSetChanged();
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

        search_close = findViewById(R.id.iv_search_close);
        search_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
                et_search.setText(null);
                et_search.clearFocus();
            }
        });
    }


    // 음악 가다다 순 데이터 요청 메서드
    private void requestMusicAlphabet() {
        RetrofitClient.getApiService().requestMusicAlphabet().enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Music> musics = result.music; // 음악 리스트

                        AddPlaylist.clear(); // 음악 목록 리스트 초기화
                        for(Music music: musics){
                            AddPlaylist.add(music);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<musicResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
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
