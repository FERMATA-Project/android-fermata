package com.example.fermata.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.fermata.R;
import com.example.fermata.adapter.MusicAdapter;
import com.example.fermata.domain.Music;

import java.util.ArrayList;

public class SearchMusicFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_music, container, false);

        // 임시 데이터
        ArrayList<Music> musicList = new ArrayList<>();
        musicList.add(new Music("노래 제목1", "가수 이름1"));
        musicList.add(new Music("노래 제목2", "가수 이름2"));
        musicList.add(new Music("노래 제목3", "가수 이름3"));
        musicList.add(new Music("노래 제목4", "가수 이름4"));
        musicList.add(new Music("노래 제목5", "가수 이름5"));

        RecyclerView rv_musicList = view.findViewById(R.id.rv_musicList); // 음악 목록 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        MusicAdapter adapter = new MusicAdapter(getContext(), musicList);
        rv_musicList.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_musicList.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결

        ImageButton btn_filter = view.findViewById(R.id.btn_filter); // 정렬 필터 버튼
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PopupMenu popupMenu = new PopupMenu(getContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_btn_filter, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_recent:
                                break;
                            case R.id.item_most:
                                break;
                            case R.id.item_hanguel:
                                break;
                        }

                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return view;
    }
}