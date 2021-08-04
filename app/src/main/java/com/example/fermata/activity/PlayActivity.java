package com.example.fermata.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.fermata.R;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

public class PlayActivity extends AppCompatActivity {

    //(menifest uses-permission) 런타임 퍼미션 목록
    String[] permission_list = {
            Manifest.permission.RECORD_AUDIO
    };

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

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // 런타임 퍼미션 확인
        checkPermission();

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

    public void checkPermission() {
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        //안드로이드6.0 (마시멜로) 이후 버전부터 유저 권한설정 필요
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){

                //권한 허용 여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                //허용되었다면
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                }
                else {
                    //권한을 허용하지 않는다면 앱 종료
                    Toast.makeText(getApplicationContext(),"앱 권한을 허용하세요",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }


}
