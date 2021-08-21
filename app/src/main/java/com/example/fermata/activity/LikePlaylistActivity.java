package com.example.fermata.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

        TextView playlist_name = findViewById(R.id.tv_playlistName);
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
        rv_like_playlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));

        ImageButton btn_option = findViewById(R.id.btn_option); // 재생 목록 옵션 버튼
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View option_view = inflater.inflate(R.layout.bottomsheet_option, null, false); // 옵션 버튼의 팝업 뷰
        ImageView iv_close = option_view.findViewById(R.id.iv_close); // 팝업 뷰의 닫기 버튼
        TextView tv_delete = option_view.findViewById(R.id.tv_delete); // 팝업 뷰의 재생 목록 삭제
        TextView tv_share = option_view.findViewById(R.id.tv_share); // 팝업 뷰의 재생 목록 공유
        TextView tv_update = option_view.findViewById(R.id.tv_update); // 팝업 뷰의 재생 목록 수정
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getApplicationContext());
        bottomSheetDialog.setContentView(option_view);

        // 옵션 버튼 클릭한 경우
        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                bottomSheetDialog.show();
            }
        });

        // 팝업 뷰의 닫기 클릭한 경우
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        // 팝업 뷰의 재생목록 삭제 클릭한 경우
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        // 팝업 뷰의 재생목록 공유 클릭한 경우
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        // 팝업 뷰의 재생목록 수정 클릭한 경우
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
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