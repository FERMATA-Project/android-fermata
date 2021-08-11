package com.example.fermata;

import com.example.fermata.domain.Music;
import com.example.fermata.response.musicResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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

    // 음악 검색 API
    @FormUrlEncoded
    @POST("/music/search")
    Call<musicResponse> requestSearch(@Field("search_word") String search_word);
  
    // 현재 playList
    //@POST("/...")
    //Call<MusicList> ...
}
