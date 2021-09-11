package com.example.fermata.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fermata.R;
import com.example.fermata.RetrofitClient;
import com.example.fermata.adapter.MyPlayListAdapter;
import com.example.fermata.domain.Playlist;
import com.example.fermata.response.musicResponse;
import com.example.fermata.response.playlistResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 설명: 음악 즐기기 화면에서 재생목록 추가 선택 -> 재생 목록 선택 화면
// author: soohyun, last modified: 21.09.10
public class SelectPlaylistActivity extends AppCompatActivity {
    MyPlayListAdapter myPlayListAdapter;
    ArrayList<Playlist> myPlaylist = new ArrayList<>(); // 재생목록 리스트
    String playlist_title = ""; // 재생목록 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_playlist);

        // 상단바 안보이게 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // PlayActivity 로부터 받은 데이터
        Intent intent = getIntent();
        playlist_title = intent.getStringExtra("playlist_title");
        int music_id = intent.getIntExtra("music_id", 1);

        requestPlaylistList(); // 재생목록 리스트 가져오기

        ImageButton btn_close = findViewById(R.id.btn_close); // 닫기 버튼
        ImageButton btn_add_playlist = findViewById(R.id.btn_add_playlist); // 재생 목록 추가 버튼

        RecyclerView rv_my_playlist = findViewById(R.id.rv_my_playlist); // 재생목록 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext()); // 레이아웃 매니저
        myPlayListAdapter = new MyPlayListAdapter(getApplicationContext(), myPlaylist);
        rv_my_playlist.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_my_playlist.setAdapter(myPlayListAdapter); // 리사이클러뷰와 어댑터 연결
        rv_my_playlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));

        // 닫기 버튼 클릭한 경우
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 액티비티 종료
            }
        });

        // 재생 목록 추가 버튼 클릭한 경우
        btn_add_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Intent intent = new Intent(getApplicationContext(), MakePlaylist2Activity.class);
                intent.putExtra("music_id", music_id);
                startActivity(intent); // 재생 목록 추가 화면으로 이동
            }
        });

        // 재생목록 아이템 클릭한 경우
        myPlayListAdapter.setOnItemClickListener(new MyPlayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                RetrofitClient.getApiService().requestAddMusic(myPlaylist.get(position).getListName(), music_id).enqueue(new Callback<musicResponse>() {
                    @Override
                    public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                        if(response.isSuccessful()){
                            musicResponse result = response.body(); // 응답 결과

                            if(result.code.equals("400")) {
                                Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                            } else if (result.code.equals("200")) {
                                // 토스트 메시지 띄우기
                                Toast toast = new Toast(getApplicationContext());
                                toast.setView(v.inflate(getApplicationContext(), R.layout.playlist_music_add_toast, null));
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                requestPlaylistList();
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
        });
    }

    //재생목록 리스트 데이터 요청 메서드
    private void requestPlaylistList() {
        RetrofitClient.getApiService().requestPlaylistList().enqueue(new Callback<playlistResponse>() {
            @Override
            public void onResponse(Call<playlistResponse> call, Response<playlistResponse> response) {
                if(response.isSuccessful()){
                    playlistResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Playlist> playlists = result.playlist; // 재생목록 리스트
                        myPlaylist.clear();

                        for(Playlist playlist: playlists){
                            if(!playlist.getListName().equals(playlist_title))
                                myPlaylist.add(playlist);
                        }
                        myPlayListAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<playlistResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPlaylistList();
    }
}