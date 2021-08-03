package com.example.fermata.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fermata.R;
import com.example.fermata.adapter.MusicAdapter;
import com.example.fermata.domain.Music;

import java.util.ArrayList;

// 설명: 재생목록에 노래 추가하기 화면
// author: seungyeon, last modified: 21.08.03

public class AddPlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);

        // 임시 데이터
        ArrayList<Music> AddPlaylist = new ArrayList<>();
        AddPlaylist.add(new Music("노래 제목1", "가수 이름1"));
        AddPlaylist.add(new Music("노래 제목2", "가수 이름2"));
        AddPlaylist.add(new Music("노래 제목3", "가수 이름3"));
        AddPlaylist.add(new Music("노래 제목4", "가수 이름4"));
        AddPlaylist.add(new Music("노래 제목5", "가수 이름5"));

        RecyclerView rv_add_playlist = findViewById(R.id.rv_add_playlist); // 현재 재생 목록 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        MusicAdapter adapter = new MusicAdapter(getApplicationContext(), AddPlaylist);
        rv_add_playlist.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결

        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(getApplicationContext(), "추가가 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        rv_add_playlist.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결


    }
}
