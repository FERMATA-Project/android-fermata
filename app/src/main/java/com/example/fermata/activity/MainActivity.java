package com.example.fermata.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fermata.R;
import com.example.fermata.fragment.PlaylistFragment;
import com.example.fermata.fragment.SearchMusicFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager; // 프래그먼트 매니저
    SearchMusicFragment searchMusicFragment = new SearchMusicFragment(); // 음악 찾기 프래그먼트
    PlaylistFragment playlistFragment = new PlaylistFragment(); // 내 재생목록 프래그먼트
    //PlayerFragment playerFragment = new PlayerFragment(); // 음악 재생 프래그먼트

    //(menifest uses-permission) 런타임 퍼미션 목록
    String[] permission_list = {
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 런타임 퍼미션 확인
        checkPermission();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction(); // 프래그먼트 트랜잭션
        transaction.replace(R.id.frameLayout, searchMusicFragment).commitAllowingStateLoss(); // 첫 화면 설정

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation); // 하단바
        // 하단바 클릭시
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch(item.getItemId()){
                    case R.id.item_searchMusic:
                        transaction.replace(R.id.frameLayout, searchMusicFragment).commitAllowingStateLoss(); // 음악 찾기 프래그먼트로 변환
                        break;
                    case R.id.item_playMusic:
                        Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                        intent.putExtra("playlist_title", "현재 재생 목록");
                        startActivity(intent);
                        break;
                    case R.id.item_playlist:
                        transaction.replace(R.id.frameLayout, playlistFragment).commitAllowingStateLoss(); // 내 재생목록 프래그먼트로 변환
                        break;

                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_search:
                startActivity(new Intent(getApplicationContext(), SearchActivity.class)); // 검색하기 화면으로 이동
                break;
        }

        return  true;
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