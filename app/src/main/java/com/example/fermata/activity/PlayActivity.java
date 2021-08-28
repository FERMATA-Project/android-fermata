package com.example.fermata.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fermata.R;
import com.example.fermata.RetrofitClient;
import com.example.fermata.domain.Music;
import com.example.fermata.response.musicResponse;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 설명: 음악 재생 화면
// author: dayoung, last modified: 21.08.21
// author: soohyun, last modified: 21.08.12

public class PlayActivity extends AppCompatActivity {
    BarVisualizer visualizer;
    TextView songName, singerName, songStart, songEnd, nowList, musicInfo; //제목, 가수, 현재 재생 시간, 재생 종료 시간, 현재 재생 목록 (화면 전환), 음악 정보
    Button btnRepeat, btnLike, btnPlay, btnPrev, btnNext, btnVolume, btnSensor; //반복 재생, 좋아요, play(pause), 이전 곡, 다음 곡, 소리 조절, 진동 조절
    SeekBar sbMusic; //음악 재생바
    Thread updateSB; //현재 재생 시간 확인을 위한
    int now_play = 0; // 현재 음악 재생 위치
    public ArrayList<Music> playlist = new ArrayList<>(); // 재생 목록
    static MediaPlayer mediaPlayer = new MediaPlayer(); // 음악 플레이어

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // 상단바 안보이게 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        visualizer = findViewById(R.id.vi_bar);
        songName = findViewById(R.id.tv_songName);
        singerName = findViewById(R.id.tv_singerName);
        songStart = findViewById(R.id.tv_start);
        songEnd = findViewById(R.id.tv_end);
        nowList = findViewById(R.id.tv_now);
        musicInfo = findViewById(R.id.tv_music_info);
        btnRepeat = findViewById(R.id.btn_repeat);
        btnLike = findViewById(R.id.btn_like);
        btnPlay = findViewById(R.id.btn_play);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnVolume = findViewById(R.id.btn_volume);
        btnSensor = findViewById(R.id.btn_sensor);
        sbMusic = findViewById(R.id.sb_music);

        // SearchMusicFragment 로부터 받은 데이터
        Intent intent = getIntent();
        String playlist_title = intent.getStringExtra("playlist_title"); // 재생 목록 이름
        int position = intent.getIntExtra("position", -2); // 음악 재생 위치

        // 재생목록 이름 표시
        nowList.setText(playlist_title);

        // 음악 재생 + visualizer
        if(position == -2 || position == -1) { // 음악 즐기기 클릭한 경우, 음악 목록에서 음악 선택한 경우
            requestPlaylistNow(position, 0); // 현재 재생 목록 가져오기
        }

        // 버튼 설정
        // play, pause 버튼
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else{
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        // 이전곡
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPlaylistNow(0, -1);
            }
        });

        // 다음곡
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPlaylistNow(0, 1);
            }
        });

        // 반복 재생
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null)
                {
                    if (mediaPlayer.isLooping()) // 현재 looping으로 설정되어 있으면
                    {
                        mediaPlayer.setLooping(false);
                        btnRepeat.setBackgroundResource(R.drawable.ic_repeat);
                    }
                    else
                    {
                        mediaPlayer.setLooping(true);
                        btnRepeat.setBackgroundResource(R.drawable.ic_repeat_one);
                    }
                }
            }
        });

        // 현재 재생 목록 (화면 전환)
        nowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_playlist = new Intent(getApplicationContext(), NowPlaylistActivity.class);
                intent_playlist.putExtra("playlist", playlist);
                intent_playlist.putExtra("playlist_title", playlist_title);
                intent_playlist.putExtra("now_play", now_play);
                startActivity(intent_playlist);
            }
        });

    }

    // 액티비티 종료 시
    @Override
    protected void onDestroy() {
        if (visualizer != null)
            visualizer.release();
        super.onDestroy();
    }

    // 곡 현재 재생 시간 update를 위한
    public String createTime(int duration) {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time += min +":";
        if (sec<10){
            time += "0";
        }
        time += sec;

        return time;
    }

    // 음악을 재생하는 메소드
    public void playAudio(int music_id) {
        // mediaPlayer 설정
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset(); //release
        }
        String url = "http://10.0.2.2:3000/music/play?music_id=" + String.valueOf(music_id);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 현재 재생 시간 update를 위한 스레드
        updateSB = new Thread(){
            @Override
            public void run() {
                int totalDur = mediaPlayer.getDuration();
                int currentPos = 0;

                while (currentPos < totalDur){
                    try{
                        sleep(500);
                        currentPos = mediaPlayer.getCurrentPosition();
                        sbMusic.setProgress(currentPos);
                    }
                    catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        //visualizer 설정
        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1) {
            visualizer.setAudioSessionId(audioSessionId);
        }

        // seekbar, 재생 시작 시간, 재생 종료 시간 설정
        String endTime = createTime(mediaPlayer.getDuration());
        songEnd.setText(endTime);
        final Handler handler = new Handler();
        final int delay = 1000; //1초
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                songStart.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);
        sbMusic.setMax(mediaPlayer.getDuration());
        updateSB.start();
        sbMusic.getProgressDrawable().setColorFilter(getResources().getColor(com.google.android.material.R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
        sbMusic.getThumb().setColorFilter(getResources().getColor(com.google.android.material.R.color.design_default_color_primary), PorterDuff.Mode.SRC_IN);
        sbMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { mediaPlayer.seekTo(sbMusic.getProgress()); }
        });

    }

    // 현재 플레이리스트 불러오기 + 음악 재생
    public void requestPlaylistNow(int position, int prevNext) { //position : playActivity 가 어디서 실행되었는지 / prevNext : 지금곡(0), 다음곡(1), 이전곡(-1)
        RetrofitClient.getApiService().requestPlaylistNow().enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(getApplicationContext(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    } else if (result.code.equals("200")) {
                        List<Music> musics = result.music; // 음악 리스트

                        playlist.clear(); // 음악 목록 리스트 초기화
                        for(Music music: musics) {
                            playlist.add(music);
                        }

                        // 음악 재생, 정보 표시
                        int size = playlist.size();
                        if(position == -2) {  // 음악 즐기기 클릭한 경우 (첫번째 곡 재생)
                            now_play = 0;
                        }
                        else if(position == -1) { // 음악 목록에서 음악 선택한 경우 (마지막 곡 재생)
                            now_play = size - 1; // 음악 재생 위치를 마지막 위치로 설정
                        }
                        else if(position == 0) { //prev next 버튼 클릭 시
                            if (now_play + prevNext >= 0)
                                now_play = (now_play + prevNext) % size;
                            else
                                now_play = size - 1;
                        }
                        songName.setText(playlist.get(now_play).getMusic_title());
                        singerName.setText(playlist.get(now_play).getPlay_date());
                        musicInfo.setText("("+ (now_play+1) +"/" + size + ")");

                        playAudio(playlist.get(now_play).getMusic_id());
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
