package com.example.fermata.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.fermata.R;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

// 설명: 음악 재생 화면
// author: dayoung, last modified: 21.08.10

public class PlayActivity extends AppCompatActivity {

    BarVisualizer visualizer;
    TextView songName, singerName, songStart, songEnd, nowList; //제목, 가수, 현재 재생 시간, 재생 종료 시간, 현재 재생 목록 (화면 전환)
    Button btnRepeat, btnLike, btnPlay, btnPrev, btnNext, btnVolume, btnSensor; //반복 재생, 좋아요, play(pause), 이전 곡, 다음 곡, 소리 조절, 진동 조절
    SeekBar sbMusic; //음악 재생바
    Thread updateSB; //현재 재생 시간 확인을 위한
    static MediaPlayer mediaPlayer; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // 상단바 안보이게 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //getSupportActionBar().setTitle("Now Playing");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        visualizer = findViewById(R.id.vi_bar);
        songName = findViewById(R.id.tv_songName);
        singerName = findViewById(R.id.tv_singerName);
        songStart = findViewById(R.id.tv_start);
        songEnd = findViewById(R.id.tv_end);
        nowList = findViewById(R.id.tv_now);
        btnRepeat = findViewById(R.id.btn_repeat);
        btnLike = findViewById(R.id.btn_like);
        btnPlay = findViewById(R.id.btn_play);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnVolume = findViewById(R.id.btn_volume);
        btnSensor = findViewById(R.id.btn_sensor);
        sbMusic = findViewById(R.id.sb_music);

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

        //재생, visualizer 실행
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(PlayActivity.this, R.raw.cantinaband60); // (음악 서버 구축 후 선택된 곡으로 재생 & setText 되도록 수정해야함)
        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1) {
            visualizer.setAudioSessionId(audioSessionId);
        }
        mediaPlayer.start();

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

        // 반복 재생 버튼
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
                startActivity(new Intent(getApplicationContext(), NowPlaylistActivity.class));
            }
        });

    }

    //액티비티 종료 시
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (visualizer != null)
            visualizer.release();
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

}
