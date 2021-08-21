package com.example.fermata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fermata.R;
import com.example.fermata.adapter.MusicAdapter;
import com.example.fermata.domain.Music;

import java.util.ArrayList;

// 설명: 음악 재생 화면 재생목록 탭 클릭 -> 재생 목록 화면
// author: soohyun, last modified: 21.07.27
// author: dayoung, last modified: 21.08.10

public class NowPlaylistActivity extends AppCompatActivity {
    ArrayList<Music> nowPlaylist = new ArrayList<>();
    MusicAdapter nowAdapter; // 음악 목록 어댑터
    int now_play = 0; // 음악 현재 재생 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playlist);

        // 상단바 안보이게 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        TextView tv_musicName = findViewById(R.id.tv_musicName); // 노래 제목
        TextView tv_singerName = findViewById(R.id.tv_singerName); // 가수 이름
        ImageButton btn_play = findViewById(R.id.btn_play); // 재생 버튼
        ImageButton btn_next = findViewById(R.id.btn_next); // 다음곡 재생 버튼
        TextView tv_playlistName = findViewById(R.id.tv_playlistName); // 재생 목록 이름
        TextView tv_music_info = findViewById(R.id.tv_music_info); // 음악 정보
        ImageButton btn_option = findViewById(R.id.btn_option); // 재생 목록 옵션 버튼

        // PlayActivity 로부터 받은 데이터
        Intent intent = getIntent();
        nowPlaylist = (ArrayList<Music>)intent.getSerializableExtra("playlist");
        String playlist_title = intent.getStringExtra("playlist_title");
        now_play = intent.getIntExtra("now_play", 0);

        // 보여지는 정보 세팅
        tv_musicName.setText(nowPlaylist.get(now_play).getMusic_title()); // 노래 제목
        tv_singerName.setText(nowPlaylist.get(now_play).getSinger()); // 가수 이름
        tv_playlistName.setText(playlist_title); // 재생목록 이름
        tv_music_info.setText("("+ (now_play+1) +"/" + nowPlaylist.size() + ")"); // 음악 정보
        if(PlayActivity.mediaPlayer.isPlaying()) { // 음악 재생 중인 경우
            btn_play.setBackgroundResource(R.drawable.ic_pause);
        } else { // 음악 재생 중이 아닌 경우
            btn_play.setBackgroundResource(R.drawable.ic_play);
        }

        RecyclerView rv_now_playlist = findViewById(R.id.rv_now_playlist); // 현재 재생 목록 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        nowAdapter = new MusicAdapter(getApplicationContext(), nowPlaylist);
        rv_now_playlist.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_now_playlist.setAdapter(nowAdapter); // 리사이클러뷰와 어댑터 연결
        rv_now_playlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));
        nowAdapter.notifyDataSetChanged();

        // 재생 버튼 클릭한 경우
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PlayActivity.mediaPlayer.isPlaying()){
                    btn_play.setBackgroundResource(R.drawable.ic_play);
                    PlayActivity.mediaPlayer.pause();
                }
                else{
                    btn_play.setBackgroundResource(R.drawable.ic_pause);
                    PlayActivity.mediaPlayer.start();
                }
            }
        });

        // 다음곡 재생 버튼 클릭한 경우
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 재생 목록 옵션 버튼 클릭한 경우
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