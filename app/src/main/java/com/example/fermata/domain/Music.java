package com.example.fermata.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Music implements Serializable {
    @SerializedName("music_id")
    private int music_id; // 음악 id
    @SerializedName("music_title")
    private String music_title; // 노래 제목
    @SerializedName("singer")
    private String singer; // 가수 이름
    @SerializedName("count")
    private int count; // 음악 재생 횟수
    @SerializedName("play_date")
    private String play_date; // 음악 재생 날짜
    @SerializedName("likes")
    private int likes; // 좋아요 여부 (0 - false, 1 - true)

    public Music(int music_id, String music_title, String singer, int count, String play_date, int likes ) {
        this.music_id = music_id;
        this.music_title = music_title;
        this.singer = singer;
        this.count = count;
        this.play_date = play_date;
        this.likes = likes;
    }

    public int getMusic_id() {
        return music_id;
    }

    public String getMusic_title() {
        return music_title;
    }

    public String getSinger() {
        return singer;
    }

    public int getCount() {
        return count;
    }

    public String getPlay_date() {
        return play_date;
    }

    public int getLikes() {
        return likes;
    }
}
