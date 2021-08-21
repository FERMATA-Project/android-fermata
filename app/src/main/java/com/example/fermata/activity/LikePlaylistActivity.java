package com.example.fermata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

// 설명: 좋아요한 음악 목록 화면
// author: soohyun, last modified: 21.07.27
// author: seungyeon, last modified: 21.08.21
public class LikePlaylistActivity extends AppCompatActivity {
    ArrayList<Music> likePlaylist = new ArrayList<>();
    MusicAdapter adapter;
    String make_list_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_playlist);

        //리스트 이름 가져오기
        Intent AddforMake = getIntent();
        make_list_name = AddforMake.getStringExtra("재생목록이름");

        TextView playlist_name = findViewById(R.id.tv_like_playlist);
        playlist_name.setText(make_list_name);

        //좋아요한 음악목록 or 재생목록 음악목록
        if(make_list_name.equals("좋아요한 음악목록")){
            requestPlaylistLikes();
        }else{
            RetrofitClient.getApiService().requestPlaylistGetmusic(make_list_name).enqueue(new Callback<musicResponse>() {
                @Override
                public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                    if(response.isSuccessful()){
                        musicResponse result = response.body(); // 응답 결과

                        if(result.code.equals("400")) {
                            Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                        } else if (result.code.equals("200")) {
                            List<Music> musics = result.music; // 음악 리스트

                            likePlaylist.clear(); // 음악 목록 리스트 초기화
                            for(Music music: musics){
                                likePlaylist.add(music);
                            }
                            adapter.notifyDataSetChanged();
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

        RecyclerView rv_like_playlist = findViewById(R.id.rv_like_playlist); // 현재 재생 목록 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        adapter = new MusicAdapter(getApplicationContext(), likePlaylist);
        rv_like_playlist.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_like_playlist.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결

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

    // 좋아요한 음악 데이터 요청 메서드
    private void requestPlaylistLikes() {
        RetrofitClient.getApiService().requestPlaylistLikes().enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Music> musics = result.music; // 음악 리스트

                        likePlaylist.clear(); // 음악 목록 리스트 초기화
                        for(Music music: musics){
                            likePlaylist.add(music);
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
}