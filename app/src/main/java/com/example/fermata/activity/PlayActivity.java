package com.example.fermata.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.fermata.R;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    ImageView imageView;
    TextView songName, singerName, songStart, songEnd, now;
    Button btnRepeat, btnLike, btnPlay, btnPrev, btnNext, btnVolume, btnSensor;
    SeekBar sbMusic;

    static MediaPlayer mediaPlayer;

    Thread updateSB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);


        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = findViewById(R.id.iv_music);

        songName = findViewById(R.id.tv_songName);
        singerName = findViewById(R.id.tv_singerName);
        songStart = findViewById(R.id.tv_start);
        songEnd = findViewById(R.id.tv_end);
        now = findViewById(R.id.tv_now);

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

        now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NowPlaylistActivity.class));
            }
        });
    }

    public String createTime(int duration)
    {
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
