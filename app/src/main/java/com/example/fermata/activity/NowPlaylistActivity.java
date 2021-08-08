package com.example.fermata.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.fermata.R;
import com.example.fermata.adapter.MusicAdapter;
import com.example.fermata.domain.Music;

import java.util.ArrayList;

// 설명: 현재 재생 목록 화면
// author: soohyun, last modified: 21.07.27
public class NowPlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playlist);

        // 상단바 안보이게 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 임시 데이터
        ArrayList<Music> nowPlaylist = new ArrayList<>();
        /*
        nowPlaylist.add(new Music("노래 제목1", "가수 이름1"));
        nowPlaylist.add(new Music("노래 제목2", "가수 이름2"));
        nowPlaylist.add(new Music("노래 제목3", "가수 이름3"));
        nowPlaylist.add(new Music("노래 제목4", "가수 이름4"));
        nowPlaylist.add(new Music("노래 제목5", "가수 이름5"));
         */

        RecyclerView rv_now_playlist = findViewById(R.id.rv_now_playlist); // 현재 재생 목록 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        MusicAdapter adapter = new MusicAdapter(getApplicationContext(), nowPlaylist);
        rv_now_playlist.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_now_playlist.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결

        ImageButton btn_option = findViewById(R.id.btn_option); // 재생 목록 옵션 버튼
        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_btn_option, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_playlist_update:
                                break;
                            case R.id.item_playlist_delete:
                                break;
                            case R.id.item_playlist_share:
                                break;
                        }

                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }
}