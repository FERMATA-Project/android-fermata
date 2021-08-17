package com.example.fermata.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fermata.R;
import com.example.fermata.adapter.MusicAdapter;
import com.example.fermata.domain.Music;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

// 설명: 재생목록 클릭 -> 재생목록 세부 화면
// author: soohyun, last modified: 21.08.17
public class PlaylistDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_detail, container, false);

        // 임시 데이터
        ArrayList<Music> likePlaylist = new ArrayList<>();
        /*
        likePlaylist.add(new Music("노래 제목1", "가수 이름1"));
        likePlaylist.add(new Music("노래 제목2", "가수 이름2"));
        likePlaylist.add(new Music("노래 제목3", "가수 이름3"));
        likePlaylist.add(new Music("노래 제목4", "가수 이름4"));
        likePlaylist.add(new Music("노래 제목5", "가수 이름5"));
         */

        RecyclerView rv_like_playlist = view.findViewById(R.id.rv_like_playlist); // 현재 재생 목록 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        MusicAdapter adapter = new MusicAdapter(getContext(), likePlaylist);
        rv_like_playlist.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_like_playlist.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결

        ImageButton btn_option = view.findViewById(R.id.btn_option); // 재생 목록 옵션 버튼
        View option_view = inflater.inflate(R.layout.bottomsheet_option, null, false); // 옵션 버튼의 팝업 뷰
        ImageView iv_close = option_view.findViewById(R.id.iv_close); // 팝업 뷰의 닫기 버튼
        TextView tv_delete = option_view.findViewById(R.id.tv_delete); // 팝업 뷰의 재생 목록 삭제
        TextView tv_share = option_view.findViewById(R.id.tv_share); // 팝업 뷰의 재생 목록 공유
        TextView tv_update = option_view.findViewById(R.id.tv_update); // 팝업 뷰의 재생 목록 수정
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
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

        return view;
    }
}
