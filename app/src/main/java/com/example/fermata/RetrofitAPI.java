package com.example.fermata;

import com.example.fermata.response.musicResponse;
import com.example.fermata.response.playlistResponse;

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

    // 음악 play
    //@POST("/music/play")
    //Call<musicResponse> requestMusicPlay();

    // 현재 playList
    @POST("/playlist/now")
    Call<musicResponse> requestPlaylistNow();

    // 최근 재생목록 5개 API
    @POST("/music/playlist_lately")
    Call<musicResponse> requestPlaylistLately();

    // 재생목록 리스트 API
    @POST("/playlist/playlist_list")
    Call<playlistResponse> requestPlaylistList();

    // 재생목록에 음악 추가 API
    @FormUrlEncoded
    @POST("/music/add")
    Call<musicResponse> requestAddMusic(
            @Field("playlist_title") String playlist_title,
            @Field("music_id") int music_id
    );
}
