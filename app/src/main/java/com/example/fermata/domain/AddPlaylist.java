package com.example.fermata.domain;

import com.google.gson.annotations.SerializedName;

public class AddPlaylist {
    @SerializedName("playlist_title")
    private String playlist_title; // 리스트 이름
    @SerializedName("music_id")
    private int music_id; // 음악 id

    public AddPlaylist(String playlist_title, int music_id) {
        this.playlist_title = playlist_title;
        this.music_id = music_id;
    }

    public String getPlaylist_title() {
        return playlist_title;
    }

    public int getMusic_id() {
        return music_id;
    }
}
