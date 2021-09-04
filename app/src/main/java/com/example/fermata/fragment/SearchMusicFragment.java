package com.example.fermata.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fermata.R;
import com.example.fermata.RetrofitClient;
import com.example.fermata.activity.PlayActivity;
import com.example.fermata.adapter.MusicAdapter;
import com.example.fermata.domain.Music;
import com.example.fermata.response.musicResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 설명: 메인 화면 중 하단바 음악 찾기 클릭 -> 음악 목록 화면
// author: soohyun, last modified: 21.08.29
public class SearchMusicFragment extends Fragment {
    ArrayList<Music> musicList = new ArrayList<>(); // 음악 목록 리스트
    MusicAdapter adapter; // 음악 목록 어댑터
    int select_option = 0; // 어떤 옵션을 선택했는지 저장하는 변수, 0 - 최신순 재생한 순 / 1 - 많이 재생한 순 / 2- 가나다

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_music, container, false);

        RecyclerView rv_musicList = view.findViewById(R.id.rv_musicList); // 음악 목록 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        adapter = new MusicAdapter(getContext(), musicList);
        rv_musicList.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_musicList.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결
        rv_musicList.addItemDecoration(new DividerItemDecoration(view.getContext(), 1));

        requestMusicRecent(); // 처음 화면 데이터 세팅

        // 리사이클러뷰의 아이템 클릭 리스너
        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                requestAddMusic(musicList.get(position).getMusic_id()); // 현재 재생 목록에 음악 추가
            }
        });

        ImageButton btn_filter = view.findViewById(R.id.btn_filter); // 정렬 필터 버튼
        // 정렬 필터 버튼 클릭한 경우
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
                                requestMusicRecent();
                                break;
                            case R.id.item_most:
                                requestMusicTimes();
                                break;
                            case R.id.item_alphabet:
                                requestMusicAlphabet();;
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

    // 음악 최신 재생한 순 데이터 요청 메서드
    private void requestMusicRecent() {
        RetrofitClient.getApiService().requestMusicRecent().enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Music> musics = result.music; // 음악 리스트

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
    }

    // 음악 많이 재생한 순 데이터 요청 메서드
    private void requestMusicTimes() {
        RetrofitClient.getApiService().requestMusicTimes().enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Music> musics = result.music; // 음악 리스트

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
    }

    // 음악 가다다 순 데이터 요청 메서드
    private void requestMusicAlphabet() {
        RetrofitClient.getApiService().requestMusicAlphabet().enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Music> musics = result.music; // 음악 리스트

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
    }

    // 현재 재생 목록에 음악 추가
    private void requestAddMusic(int music_id) {
        RetrofitClient.getApiService().requestAddMusic("현재", music_id).enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        Intent intent = new Intent(getContext(), PlayActivity.class);
                        intent.putExtra("playlist_title", "현재"); // 재생목록 이름
                        intent.putExtra("position", -1); // 음악 재생 위치
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onFailure(Call<musicResponse> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (select_option) {
            case 0:
                requestMusicRecent();
                break;
            case 1:
                requestMusicTimes();
                break;
            case 2:
                requestMusicAlphabet();
                break;
        }
    }
}