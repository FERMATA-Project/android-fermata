package com.example.fermata.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

// 설명: 음악 재생 화면 재생목록 탭 클릭 -> 재생 목록 화면
// author: soohyun, last modified: 21.08.30
// author: dayoung, last modified: 21.09.04

public class NowPlaylistActivity extends AppCompatActivity {
    ArrayList<Music> nowPlaylist = new ArrayList<>();
    MusicAdapter nowAdapter; // 음악 목록 어댑터
    int now_play = 0; // 음악 현재 재생 위치
    String list_music_clip = "";
    static TextView tv_musicName, tv_singerName, tv_music_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playlist);

        tv_musicName = findViewById(R.id.tv_musicName); // 노래 제목
        tv_singerName = findViewById(R.id.tv_singerName); // 가수 이름
        tv_music_info = findViewById(R.id.tv_music_info); // 음악 정보
        ImageButton btn_play = findViewById(R.id.btn_play); // 재생 버튼
        ImageButton btn_next = findViewById(R.id.btn_next); // 다음곡 재생 버튼
        TextView tv_playlistName = findViewById(R.id.tv_playlistName); // 재생 목록 이름
        ImageButton btn_option = findViewById(R.id.btn_option); // 재생 목록 옵션 버튼

        // 상단바 안보이게 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // PlayActivity 로부터 받은 데이터
        Intent intent = getIntent();
        nowPlaylist = (ArrayList<Music>)intent.getSerializableExtra("playlist");
        String playlist_title = intent.getStringExtra("playlist_title");
        now_play = intent.getIntExtra("now_play", 0);

        // 보여지는 정보 세팅
        tv_playlistName.setText(playlist_title); // 재생목록 이름
        if(PlayActivity.mediaPlayer.isPlaying()) { // 음악 재생 중인 경우
            btn_play.setBackgroundResource(R.drawable.ic_pause);
        } else { // 음악 재생 중이 아닌 경우
            btn_play.setBackgroundResource(R.drawable.now_playlist_btn_play);
        }
        requestPlaylistInfo(now_play, playlist_title);

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
                // 곡 재생 + 정보 변경
                ((PlayActivity)PlayActivity.context).requestPlaylistNow(now_play+1, playlist_title);
                now_play = now_play + 1;
                requestPlaylistInfo(now_play, playlist_title);
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
                            case R.id.item_playlist_delete:
                                break;
                            case R.id.item_playlist_share:
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                                if(playlist_title.equals("현재 재생 목록")) {
                                    RetrofitClient.getApiService().requestPlaylistGetmusic("현재 재생 목록").enqueue(new Callback<musicResponse>() {
                                        @Override
                                        public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                                            if(response.isSuccessful()){
                                                musicResponse result = response.body(); // 응답 결과

                                                if(result.code.equals("400")) {
                                                    Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                                                } else if (result.code.equals("200")) {
                                                    List<Music> musics = result.music; // 음악 리스트
                                                    list_music_clip = "";

                                                    for(Music music: musics){
                                                        list_music_clip = list_music_clip + "\n" + music.getMusic_title().toString() + " - " + music.getSinger().toString();
                                                    }

                                                    ClipData clip = ClipData.newPlainText("현재 재생 목록", "재생목록 이름: 현재 재생 목록" + "\n" + list_music_clip);
                                                    clipboard.setPrimaryClip(clip);

                                                    Toast.makeText(getApplicationContext(), "재생목록이 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<musicResponse> call, Throwable t) {
                                            t.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    RetrofitClient.getApiService().requestPlaylistGetmusic(playlist_title).enqueue(new Callback<musicResponse>() {
                                        @Override
                                        public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                                            if(response.isSuccessful()){
                                                musicResponse result = response.body(); // 응답 결과

                                                if(result.code.equals("400")) {
                                                    Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                                                } else if (result.code.equals("200")) {
                                                    List<Music> musics = result.music; // 음악 리스트
                                                    list_music_clip = "";

                                                    for(Music music: musics){
                                                        list_music_clip = list_music_clip + "\n" + music.getMusic_title().toString() + " - " + music.getSinger().toString();
                                                    }

                                                    ClipData clip = ClipData.newPlainText(playlist_title, "재생목록 이름: " + playlist_title + "\n" + list_music_clip);
                                                    clipboard.setPrimaryClip(clip);

                                                    Toast.makeText(getApplicationContext(), "재생목록이 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
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
                                break;
                        }

                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }
    // 선택된 재생목록의 음악 리스트 가져오기 + 음악 재생 메소드
    public void requestPlaylistInfo(int now_play, String playlist_title) {
        RetrofitClient.getApiService().requestPlaylistNow(playlist_title).enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Music> musics = result.music; // 음악 리스트
                        int size = musics.size();

                        // 보여지는 정보 세팅
                        tv_musicName.setText(musics.get(now_play).getMusic_title());
                        tv_singerName.setText(musics.get(now_play).getSinger());
                        tv_music_info.setText("("+ (now_play+1) +"/" + size + ")");
                    }
                }
            }
            @Override
            public void onFailure(Call<musicResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

}