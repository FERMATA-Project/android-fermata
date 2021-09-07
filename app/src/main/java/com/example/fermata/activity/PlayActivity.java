package com.example.fermata.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fermata.FlaskRetrofitClient;
import com.example.fermata.R;
import com.example.fermata.RetrofitClient;
import com.example.fermata.domain.Music;
import com.example.fermata.response.musicResponse;
import com.example.fermata.response.vibrateResponse;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 설명: 음악 재생 화면
// author: dayoung, last modified: 21.08.28
// author: soohyun, last modified: 21.08.30
public class PlayActivity extends AppCompatActivity {
    public static Context context; // PlayActivity context
    ImageButton btn_option; // 재생 목록 옵션 버튼
    BarVisualizer visualizer;
    TextView songName, singerName, songStart, songEnd, nowList, musicInfo; //제목, 가수, 현재 재생 시간, 재생 종료 시간, 현재 재생 목록 (화면 전환), 음악 정보
    Button btnRepeat, btnLike, btnPlay, btnPrev, btnNext, btnVolume, btnSensor; //반복 재생, 좋아요, play(pause), 이전 곡, 다음 곡, 소리 조절, 진동 조절
    SeekBar sbMusic; //음악 재생바
    Thread updateSB; //현재 재생 시간 확인을 위한
    int now_play = 0; // 현재 음악 재생 위치
    int now_music_id = 0; // 현재 재생 음악 id
    ArrayList<Music> playlist = new ArrayList<>(); // 재생 목록
    List<Integer> vibrateList = new ArrayList<>(); // 음악 진동 세기 리스트
    static MediaPlayer mediaPlayer = new MediaPlayer(); // 음악 플레이어
    Thread vibrateThread; // 진동 시간 확인을 위한 스레드
    Vibrator vibrator; // 진동 구현 도구
    String isVibrate = null; // 진동 여부
    int vibrateTime = 0; // 현재 진동 위치
    SimpleDateFormat dateFormat = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    int like = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        context = this;

        // 상단바 안보이게 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 진동 구현을 위한 세팅
        vibrator = (Vibrator)getSystemService(Activity.VIBRATOR_SERVICE);
        SharedPreferences sharedPreferences = getSharedPreferences("vibrate", Activity.MODE_PRIVATE); // 진동 여부 데이터가 저장되어 있는 곳
        isVibrate = sharedPreferences.getString("vibrate","true"); // 저장된 진동 여부 값

        btn_option = findViewById(R.id.btn_option);
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

        // SearchMusicFragment, LikePlaylistActivity, MainAcitivity 로부터 받은 데이터
        Intent intent = getIntent();
        String playlist_title = intent.getStringExtra("playlist_title"); // 재생 목록 이름
        int position = intent.getIntExtra("position", -2); // 음악 재생 위치

        // 재생목록 이름 표시
        nowList.setText(playlist_title + " 재생 목록");

        // 음악 재생 + visualizer
        requestPlaylistNow(position, playlist_title);
      
        //playAudio(playlist.get(now_play).getMusic_id());
        //requestVibrate(playlist.get(position).getMusic_id());

        // 재생 목록 옵션 버튼 클릭한 경우
        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_btn_play_option, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_add_playlist:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        // play, pause 버튼
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                    //vibrateThread.interrupt();
                }
                else{
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                    playVibrate();
                }
            }
        });

        // 이전곡 버튼
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestPlaylistNow(0, -1);
                requestPlaylistNow(now_play-1, playlist_title);
            }
        });

        // 다음곡 버튼
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPlaylistNow(now_play+1, playlist_title);
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

        // 좋아요 버튼
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like == 1) // 좋아요 O -> 좋아요 X
                {
                    requestUpdateLike(0, now_music_id);
                    btnLike.setBackgroundResource(R.drawable.ic_favorite_no);
                }
                else  // 좋아요 X -> 좋아요 O
                {
                    requestUpdateLike(1, now_music_id);
                    btnLike.setBackgroundResource(R.drawable.ic_favorite_yes);
                }
            }
        });

        // 진동 버튼
        btnSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("vibrate", Activity.MODE_PRIVATE); // 진동 여부 데이터가 저장되어 있는 곳
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String isVibrate = sharedPreferences.getString("vibrate","true"); // 저장된 진동 여부 값

                if(isVibrate.equals("true")) { // 진동 켜기 상태인 경우
                    editor.putString("vibrate", "false"); // 진동 끄기로 변경
                    editor.commit();
                    Toast.makeText(context, "진동 끄기", Toast.LENGTH_SHORT).show();
                } else if(isVibrate.equals("false")) { // 진동 끄기 상태인 경우
                    editor.putString("vibrate", "true"); // 진동 켜로 변경
                    editor.commit();
                    Toast.makeText(context, "진동 켜기", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 재생 목록 음악 리스트 (화면 전환)
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

    // 곡 현재 재생 시간 update를 위한 메소드
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

    // Update 재생 날짜 메소드
    private void requestUpdatePlayDate (String play_date, int music_id) {
        RetrofitClient.getApiService().requestUpdatePlayDate(play_date, music_id).enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(context, "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<musicResponse> call, Throwable t) {
                Toast.makeText(context, "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update 좋아요 상태 변경 메소드
    private void requestUpdateLike (int music_id, int like) {
        RetrofitClient.getApiService().requestUpdateLike(music_id, like).enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(context, "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<musicResponse> call, Throwable t) {
                Toast.makeText(context, "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 음악 재생 메소드
    public void playAudio(int music_id) {
        // mediaPlayer 설정
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.reset();
        }
        String url = "http://10.0.2.2:3000/music/play?music_id=" + String.valueOf(music_id);
        try {
            mediaPlayer.reset();
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

    // 선택된 재생목록의 음악 리스트 가져오기 + 음악 재생 메소드
    public void requestPlaylistNow(int position, String playlist_title) {
        RetrofitClient.getApiService().requestPlaylistNow(playlist_title).enqueue(new Callback<musicResponse>() {
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
                            now_play = size - 1;
                        }
                        else {
                            now_play = position % size; // 전달받은 position값(0~)의 음악 재생
                        }

                        // 좋아요
                        like = playlist.get(now_play).getLikes();
                        now_music_id = playlist.get(now_play).getMusic_id();
                        if (like == 1) { btnLike.setBackgroundResource(R.drawable.ic_favorite_yes); } // 좋아요 O
                        else { btnLike.setBackgroundResource(R.drawable.ic_favorite_no); } // 좋아요 X

                        // 재생 날짜 Update
                        requestUpdatePlayDate(dateFormat.format(date), playlist.get(now_play).getMusic_id());

                        // 재생
                        songName.setText(playlist.get(now_play).getMusic_title());
                        singerName.setText(playlist.get(now_play).getSinger());
                        musicInfo.setText("("+ (now_play+1) +"/" + size + ")");

                        //NowPlaylistActivity.tv_musicName.setText(playlist.get(now_play).getMusic_title());
                        //NowPlaylistActivity.tv_singerName.setText(playlist.get(now_play).getSinger());
                        //NowPlaylistActivity.tv_music_info.setText("("+ (now_play + 1) +"/" + playlist.size() + ")");
                        playAudio(playlist.get(now_play).getMusic_id());
                        //requestVibrate(playlist.get(now_play).getMusic_id());
                    }
                }
            }
            @Override
            public void onFailure(Call<musicResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 음악 진동 세기 정보 가져오는 메소드
    public void requestVibrate(int music_id) {
        FlaskRetrofitClient.getApiService().requestVibrate(music_id).enqueue(new Callback<vibrateResponse>() {
            @Override
            public void onResponse(Call<vibrateResponse> call, Response<vibrateResponse> response) {
                if(response.isSuccessful()){
                    vibrateResponse result = response.body();
                    vibrateList.clear();
                    vibrateList = result.amplitude;

                    vibrateTime = 0;
                    playVibrate();
                }
            }
            @Override
            public void onFailure(Call<vibrateResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 음악 진동 구현 메소드
    public void playVibrate() {
        vibrateThread = new Thread() {
            @Override
            public void run() {

                while (mediaPlayer.isPlaying() && vibrateTime <= vibrateList.size()){
                    try{
                        if(isVibrate.equals("true")) { // 진동을 허용했다면 3초마다 진동 구현
                            if(Build.VERSION.SDK_INT >=  26) {
                                vibrator.vibrate(VibrationEffect.createOneShot(1000, (int)(vibrateList.get(vibrateTime) * 2.5))); // 1초 동안 진동
                                System.out.println("진동 " + vibrateTime);
                                vibrateTime += 3;
                            } else {
                                vibrator.vibrate(1000);
                            }
                        }
                        sleep(2000);
                    }
                    catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        vibrateThread.start();
    }
}
