package com.example.fermata.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.example.fermata.adapter.DeleteMusicAdapter;
import com.example.fermata.adapter.MusicAdapter;
import com.example.fermata.domain.Music;
import com.example.fermata.domain.Playlist;
import com.example.fermata.response.musicResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 설명: 좋아요한 음악 목록 화면
// author: soohyun, last modified: 21.08.30
// author: seungyeon, last modified: 21.09.11
public class LikePlaylistActivity extends AppCompatActivity {
    ArrayList<Music> likePlaylist = new ArrayList<>();
    MusicAdapter adapter;
    DeleteMusicAdapter deleteadapter;
    String make_list_name;
    String list_music_clip;

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
            requestPlaylistMusic();
        }

        RecyclerView rv_like_playlist = findViewById(R.id.rv_like_playlist); // 현재 재생 목록 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        adapter = new MusicAdapter(getApplicationContext(), likePlaylist);
        deleteadapter = new DeleteMusicAdapter(getApplicationContext(), likePlaylist);
        rv_like_playlist.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_like_playlist.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결
        rv_like_playlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));

        // 재생목록 리스트 클릭한 경우 -> 음악 즐기기 화면으로 이동
        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override

            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("playlist_title", make_list_name); // 재생목록 이름
                intent.putExtra("position", position); // 음악 재생 위치
                startActivity(intent);
            }
        });

        ImageButton btn_option = findViewById(R.id.btn_option); // 재생 목록 옵션 버튼
        Button btn_delete = findViewById(R.id.btn_delete);

        // 옵션 버튼 클릭한 경우
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
                                rv_like_playlist.setAdapter(deleteadapter);
                                btn_delete.setVisibility(View.VISIBLE); //삭제버튼 화면에 보이게하기
                                btn_option.setVisibility(View.GONE);    //옵션버튼 화면에 안보이게하기
                                break;
                            case R.id.item_playlist_share:
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                                //좋아요한 음악목록 or 재생목록 음악목록
                                if(make_list_name.equals("좋아요한 음악목록")){
                                    RetrofitClient.getApiService().requestPlaylistLikes().enqueue(new Callback<musicResponse>() {
                                        @Override
                                        public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                                            if(response.isSuccessful()){
                                                musicResponse result = response.body(); // 응답 결과

                                                if(result.code.equals("400")) {
                                                    Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                                                } else if (result.code.equals("200")) {
                                                    List<Music> musics = result.music;
                                                    list_music_clip = "";

                                                    for(Music music: musics){
                                                        list_music_clip = list_music_clip + "\n" + music.getMusic_title() + " - " + music.getSinger();
                                                    }

                                                    ClipData clip = ClipData.newPlainText(make_list_name, "재생목록 이름: " + make_list_name + "\n" + list_music_clip);
                                                    clipboard.setPrimaryClip(clip);

                                                    Toast cliptoast = new Toast(getApplicationContext());
                                                    cliptoast.setView(View.inflate(getApplicationContext(), R.layout.clip_copy_toast, null));
                                                    cliptoast.setGravity(Gravity.CENTER, 0, 0);
                                                    cliptoast.show();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<musicResponse> call, Throwable t) {
                                            Toast.makeText(getApplicationContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    RetrofitClient.getApiService().requestPlaylistGetmusic(make_list_name).enqueue(new Callback<musicResponse>() {
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

                                                    ClipData clip = ClipData.newPlainText(make_list_name, "재생목록 이름: " + make_list_name + "\n" + list_music_clip);
                                                    clipboard.setPrimaryClip(clip);

                                                    Toast cliptoast = new Toast(getApplicationContext());
                                                    cliptoast.setView(View.inflate(getApplicationContext(), R.layout.clip_copy_toast, null));
                                                    cliptoast.setGravity(Gravity.CENTER, 0, 0);
                                                    cliptoast.show();
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

        // 삭제 버튼 클릭한 경우
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deleteadapter.deleteList.size() != 0) {
                    int[] deleteList = new int[deleteadapter.deleteList.size()];
                    for(int i=0; i<deleteList.length; i++) {
                        deleteList[i] = deleteadapter.deleteList.get(i);
                        System.out.println(deleteList[i]);
                    }

                    if(make_list_name.equals("좋아요한 음악목록")) {
                        requestUpdateLikes(view, deleteList);
                    } else {
                        requestDeleteMusic(view, deleteList);
                    }
                }

                rv_like_playlist.setAdapter(adapter);
                btn_option.setVisibility(View.VISIBLE); //옵션버튼 화면에 보이게하기
                btn_delete.setVisibility(View.GONE);    //삭제버튼 화면에 안보이게하기
            }
        });
    }

    // 좋아요한 음악 목록 데이터 요청 메서드
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

    // 나머지 재생목록 데이터 요청 메서드
    private void requestPlaylistMusic() {
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

    // 좋아요한 음악 목록에서 음악 삭제하는 메서드
    private void requestUpdateLikes(View view, int[] deleteList) {
        RetrofitClient.getApiService().requestUpdateLikes(deleteList).enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        requestPlaylistLikes(); // 재생 목록 갱신

                        // 토스트 메시지 띄우기
                        Toast toast = new Toast(getApplicationContext());
                        toast.setView(view.inflate(getApplicationContext(), R.layout.delete_toast, null));
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        deleteadapter.deleteList.clear(); // 삭제할 음악 목록 초기화
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

    // 나머지 재생목록에서 음악 삭제하는 메서드
    private void requestDeleteMusic(View view, int[] deleteList) {
        RetrofitClient.getApiService().requestDeleteMusic(make_list_name, deleteList).enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        requestPlaylistMusic(); // 재생 목록 갱신

                        // 토스트 메시지 띄우기
                        Toast toast = new Toast(getApplicationContext());
                        toast.setView(view.inflate(getApplicationContext(), R.layout.delete_toast, null));
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        deleteadapter.deleteList.clear(); // 삭제할 음악 목록 초기화
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

    @Override
    protected void onResume() {
        super.onResume();
        if(make_list_name.equals("좋아요한 음악목록")){
            requestPlaylistLikes();
        }else{
            requestPlaylistMusic();
        }
    }
}