package com.example.fermata.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.fermata.R;
import com.example.fermata.activity.MakePlaylistActivity;
import com.example.fermata.adapter.LatelyMusicAdapter;
import com.example.fermata.adapter.MyPlayListAdapter;
import com.example.fermata.domain.Music;
import com.example.fermata.domain.Playlist;

import java.util.ArrayList;

// 설명: 메인 화면 중 하단바 내 재생목록 클릭 -> 재생 목록 화면
// author: seungyeon, last modified: 21.08.03
public class PlaylistFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        // 최근 들은 노래 임시 데이터
        ArrayList<Music> lately_musicList = new ArrayList<>();
        /*
        lately_musicList.add(new Music("노래 이름1", "가수1"));
        lately_musicList.add(new Music("노래 이름2", "가수2"));
        lately_musicList.add(new Music("노래 이름3", "가수3"));
        lately_musicList.add(new Music("노래 이름4", "가수4"));
        lately_musicList.add(new Music("노래 이름5", "가수5"));
         */

        RecyclerView rv_lately_musicList = view.findViewById(R.id.rv_lately_list); // 최근들은노래 리사이클러뷰
        LinearLayoutManager manager_lately = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false); // 레이아웃 매니저
        LatelyMusicAdapter adapter_lately = new LatelyMusicAdapter(getContext(), lately_musicList);
        rv_lately_musicList.setLayoutManager(manager_lately); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_lately_musicList.setAdapter(adapter_lately); // 리사이클러뷰와 어댑터 연결


        // 재생 목록 임시 데이터
        ArrayList<Playlist> playList = new ArrayList<>();
        playList.add(new Playlist("좋아요한 음악목록", 1));
        playList.add(new Playlist("재생목록 이름1", 2));
        playList.add(new Playlist("재생목록 이름2", 3));
        playList.add(new Playlist("재생목록 이름3", 4));
        playList.add(new Playlist("재생목록 이름4", 5));
        playList.add(new Playlist("재생목록 이름5", 6));

        RecyclerView rv_my_playlist = view.findViewById(R.id.rv_my_playlist); // 재생목록 리사이클러뷰
        LinearLayoutManager manager_playlist = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        MyPlayListAdapter adapter_playlist = new MyPlayListAdapter(getContext(), playList);
        rv_my_playlist.setLayoutManager(manager_playlist); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_my_playlist.setAdapter(adapter_playlist); // 리사이클러뷰와 어댑터 연결



        ImageButton btn_add_list = view.findViewById(R.id.btn_add_list); // 재생목록추가 버튼
        btn_add_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(getActivity(), MakePlaylistActivity.class));
            }
        });

        return view;
    }
}