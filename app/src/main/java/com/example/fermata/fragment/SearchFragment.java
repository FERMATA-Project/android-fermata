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
import android.widget.Toast;

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

// 설명: 상단바의 검색 아이콘 클릭 -> 음악 검색 화면
// author: soohyun, last modified: 21.08.09
public class SearchFragment extends Fragment {
    ArrayList<Music> musicList = new ArrayList<>(); // 검색된 음악 리스트

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

                    RecyclerView rv_search_musicList = view.findViewById(R.id.rv_search_musicList); // 음악 목록 리사이클러뷰
                    LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
                    MusicAdapter adapter = new MusicAdapter(getContext(), musicList);
                    rv_search_musicList.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
                    rv_search_musicList.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결

                    String search_word = et_search.getText().toString().trim(); // 검색한 단어

                    // 서버에 검색된 음악 정보를 가져옴
                    RetrofitClient.getApiService().requestSearch(search_word).enqueue(new Callback<musicResponse>() {
                        @Override
                        public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                            if(response.isSuccessful()){
                                musicResponse result = response.body(); // 응답 결과

                                if(result.code.equals("400")) {
                                    Toast.makeText(getContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                                } else if (result.code.equals("200")) {
                                    List<Music> musics = result.music; // 음악 리스트

                                    if(musics.size() == 0) {
                                        Toast.makeText(getContext(), "검색된 음악이 없습니다.", Toast.LENGTH_SHORT).show();
                                    }

                                    musicList.clear(); // 음악 목록 리스트 초기화
                                    for(Music music: musics){
                                        musicList.add(music);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<musicResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return true;
                }
                return false;
            }
        });

        return view;
    }
}