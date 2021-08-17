package com.example.fermata.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fermata.DividerItemDecorator;
import com.example.fermata.R;
import com.example.fermata.RetrofitClient;
import com.example.fermata.WidthItemDecorator;
import com.example.fermata.activity.MakePlaylistActivity;
import com.example.fermata.adapter.LatelyMusicAdapter;
import com.example.fermata.adapter.MyPlayListAdapter;
import com.example.fermata.domain.Music;
import com.example.fermata.domain.Playlist;
import com.example.fermata.response.musicResponse;
import com.example.fermata.response.playlistResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 설명: 메인 화면 중 하단바 내 재생목록 클릭 -> 재생 목록 화면
// author: seungyeon, last modified: 21.08.10
public class PlaylistFragment extends Fragment {
    ArrayList<Music> lately_musicList = new ArrayList<>();
    ArrayList<Playlist> playList = new ArrayList<>();
    LatelyMusicAdapter adapter_lately;
    MyPlayListAdapter adapter_playlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        requestPlaylistLately();   //최근 들은 노래 리스트

        RecyclerView rv_lately_musicList = view.findViewById(R.id.rv_lately_list); // 최근들은노래 리사이클러뷰
        LinearLayoutManager manager_lately = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false); // 레이아웃 매니저
        adapter_lately = new LatelyMusicAdapter(getContext(), lately_musicList);
        rv_lately_musicList.setLayoutManager(manager_lately); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_lately_musicList.setAdapter(adapter_lately); // 리사이클러뷰와 어댑터 연결
        rv_lately_musicList.addItemDecoration(new WidthItemDecorator(24));


        //재생 목록 리스트
        requestPlaylistLikes();
        requestPlaylistList();

        RecyclerView rv_my_playlist = view.findViewById(R.id.rv_my_playlist); // 재생목록 리사이클러뷰
        LinearLayoutManager manager_playlist = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        adapter_playlist = new MyPlayListAdapter(getContext(), playList);
        rv_my_playlist.setLayoutManager(manager_playlist); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_my_playlist.setAdapter(adapter_playlist); // 리사이클러뷰와 어댑터 연결
        rv_my_playlist.addItemDecoration(new DividerItemDecoration(view.getContext(), 1));



        ImageButton btn_add_list = view.findViewById(R.id.btn_add_list); // 재생목록추가 버튼
        btn_add_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(getActivity(), MakePlaylistActivity.class));
            }
        });

        return view;
    }

    //최근 들은 노래(5개) 데이터 요청 메서드
    private void requestPlaylistLately() {
        RetrofitClient.getApiService().requestPlaylistLately().enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Music> musics = result.music; // 음악 리스트

                        lately_musicList.clear(); // 음악 목록 리스트 초기화
                        for(Music music: musics){
                            lately_musicList.add(music);
                        }
                        adapter_lately.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<musicResponse> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //좋아요 개수 데이터 요청 메서드
    private void requestPlaylistLikes() {
        RetrofitClient.getApiService().requestPlaylistLikes().enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Music> likescount = result.music; // 좋아요 개수세는 리스트
                        int count = 0;

                        playList.clear(); // 재생 목록 리스트 초기화
                        for(Music music: likescount){
                            count += 1;
                        }
                        if(count == 0)
                            playList.add(new Playlist(0,"좋아요한 음악목록"));
                        else
                            playList.add(new Playlist(count,"좋아요한 음악목록"));
                        adapter_playlist.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<musicResponse> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Playlist> playlists = result.playlist; // 재생목록 리스트

                        for(Playlist playlist: playlists){
                            playList.add(playlist);
                        }
                        adapter_playlist.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<playlistResponse> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }
}