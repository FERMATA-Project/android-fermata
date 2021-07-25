package com.example.fermata.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.fermata.R;
import com.example.fermata.adapter.MusicAdapter;
import com.example.fermata.domain.Music;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        EditText et_search = view.findViewById(R.id.et_search); // 음악 검색창
        // 엔터키 이벤트 처리
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //키패드 내리기
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(et_search.getWindowToken(), 0);

                    // 임시 데이터
                    ArrayList<Music> musicList = new ArrayList<>();
                    musicList.add(new Music("노래 제목1", "가수 이름1"));
                    musicList.add(new Music("노래 제목2", "가수 이름2"));
                    musicList.add(new Music("노래 제목3", "가수 이름3"));
                    musicList.add(new Music("노래 제목4", "가수 이름4"));
                    musicList.add(new Music("노래 제목5", "가수 이름5"));

                    RecyclerView rv_search_musicList = view.findViewById(R.id.rv_search_musicList); // 음악 목록 리사이클러뷰
                    LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
                    MusicAdapter adapter = new MusicAdapter(getContext(), musicList);
                    rv_search_musicList.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
                    rv_search_musicList.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결

                    return true;
                }
                return false;
            }
        });

        return view;
    }
}