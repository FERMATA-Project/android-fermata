package com.example.fermata.domain;

import com.google.gson.annotations.SerializedName;

public class Playlist {
    @SerializedName("count(music_id)")
    private int singCount; // 노래 개수
    @SerializedName("playlist_title")
    private String listName; // 재생목록 이름

    public Playlist(int singCount, String listName) {
        this.listName = listName;
        this.singCount = singCount;
    }

    public String getListName() {
        return listName;
    }

    public String getSingCount() {
        return "노래 " + String.valueOf(singCount) + "곡";
    }

}
