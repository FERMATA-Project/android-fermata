package com.example.fermata.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.fermata.R;
import com.example.fermata.fragment.PlaylistFragment;
import com.example.fermata.fragment.SearchFragment;
import com.example.fermata.fragment.SearchMusicFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager; // 프래그먼트 매니저
    SearchMusicFragment searchMusicFragment = new SearchMusicFragment(); // 음악 찾기 프래그먼트
    PlaylistFragment playlistFragment = new PlaylistFragment(); // 내 재생목록 프래그먼트
    SearchFragment searchFragment = new SearchFragment(); // 검색하기 프래그먼트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_search:
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, searchFragment).commitAllowingStateLoss(); // 검색하기 프래그먼트로 변환
                break;
        }

        return  true;
    }

}