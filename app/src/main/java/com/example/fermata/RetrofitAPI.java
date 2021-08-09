package com.example.fermata;

import com.example.fermata.response.musicResponse;
import com.example.fermata.response.playlistResponse;

import retrofit2.Call;
import retrofit2.http.POST;

public interface RetrofitAPI {
    // 음악 최신 재생한 순 API
    @POST("/music/recent")
    Call<musicResponse> requestMusicRecent();

    // 음악 많이 재생한 순 API
    @POST("/music/times")
    Call<musicResponse> requestMusicTimes();

    // 음악 가나다 순 API
    @POST("/music/alphabet")
    Call<musicResponse> requestMusicAlphabet();
  
    // 현재 playList
    //@POST("/...")
    //Call<MusicList> ...

    // 최근 재생목록 5개 API
    @POST("/music/playlist_lately")
    Call<musicResponse> requestPlaylistLately();

    // 재생목록 리스트 API
    @POST("/playlist/playlist_list")
    Call<playlistResponse> requestPlaylistList();
}
