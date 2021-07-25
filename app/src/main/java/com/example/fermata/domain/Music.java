package com.example.fermata.domain;

public class Music {
    private String musicName; // 노래 제목
    private String singerName; // 가수 이름

    public Music(String musicName, String singerName) {
        this.musicName = musicName;
        this.singerName = singerName;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getSingerName() {
        return singerName;
    }
}
