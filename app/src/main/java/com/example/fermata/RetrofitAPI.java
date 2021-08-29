package com.example.fermata;

import com.example.fermata.domain.AddPlaylist;
import com.example.fermata.response.musicResponse;
import com.example.fermata.response.playlistResponse;
import com.example.fermata.response.vibrateResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    @POST("/playlist/now")
    Call<musicResponse> requestPlaylistNow();

    // 최근 재생목록 5개 API
    @POST("/music/playlist_lately")
    Call<musicResponse> requestPlaylistLately();

    // 재생목록 리스트 API
    @POST("/playlist/playlist_list")
    Call<playlistResponse> requestPlaylistList();

    //좋아요 리스트 API
    @POST("/music/playlist_likes")
    Call<musicResponse> requestPlaylistLikes();

    //리스트에 음악 저장 API
    @FormUrlEncoded
    @POST("/playlist/playlist_add")
    Call<AddPlaylist> requestAddPlaylist(
            @Field("playlist_title") String playlist_title,
            @Field("music_id") int music_id
    );

    // 리스트에 음악 저장 취소 API
    @FormUrlEncoded
    @POST("/playlist/playlist_del")
    Call<AddPlaylist> requestDelPlaylist(
            @Field("playlist_title") String playlist_title,
            @Field("music_id") int music_id
    );

    // 재생목록에 음악 추가 API
    @FormUrlEncoded
    @POST("/music/add")
    Call<musicResponse> requestAddMusic(
            @Field("playlist_title") String playlist_title,
            @Field("music_id") int music_id
    );

    // 좋아요 상태 변경 API
    @FormUrlEncoded
    @POST("/music/like")
    Call<musicResponse> requestUpdateLike(
            @Field("like") int like,
            @Field("music_id") int music_id
    );

    // 재생 날짜 변경 API
    @FormUrlEncoded
    @POST("/music/playdate")
    Call<musicResponse> requestUpdatePlayDate(
            @Field("play_date") String play_date,
            @Field("music_id") int music_id
    );

    //재생목록 음악 가져오기 API
    @FormUrlEncoded
    @POST("/music/playlist_getmusic")
    Call<musicResponse> requestPlaylistGetmusic(
            @Field("playlist_title") String playlist_title
    );

    // 음악 진동 세기 가져오기 API
    @GET("/vibrate")
    Call<vibrateResponse> requestVibrate(@Query("music_id") int music_id);
}
