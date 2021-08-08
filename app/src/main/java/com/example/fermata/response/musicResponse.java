package com.example.fermata.response;

import com.example.fermata.domain.Music;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class musicResponse {
    @SerializedName("code")
    public String code;
    @SerializedName("music")
    public List<Music> music;
}
