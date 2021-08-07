package com.example.fermata.domain;

public class Playlist {
    private String listName; // 재생목록 이름
    private Integer singCount; // 노래 개수

    public Playlist(String listName, Integer singCount) {
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
