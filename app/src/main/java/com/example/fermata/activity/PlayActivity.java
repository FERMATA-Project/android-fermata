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

    String[] permission_list = {
            Manifest.permission.RECORD_AUDIO
    };

    TextView songName, singerName, songStart, songEnd, nowList;
    Button btnRepeat, btnLike, btnPlay, btnPrev, btnNext, btnVolume, btnSensor;
    SeekBar sbMusic;
    BarVisualizer visualizer;
    static MediaPlayer mediaPlayer;
    Thread updateSB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        checkPermission();

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(PlayActivity.this, R.raw.cantinaband60); //음원 지정
        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1) {
            visualizer.setAudioSessionId(audioSessionId);
        }
        mediaPlayer.start();


        String endTime = createTime(mediaPlayer.getDuration());
        songEnd.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

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
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(sbMusic.getProgress());
            }
        });


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

        nowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NowPlaylistActivity.class));
            }
        });
    }

    //onCreate 외부 함수
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (visualizer != null)
            visualizer.release();
    }

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
                //허용됐다면
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                }
                else {
                    //권한을 하나라도 허용하지 않는다면 앱 종료
                    Toast.makeText(getApplicationContext(),"앱 권한을 허용하세요",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }


}
