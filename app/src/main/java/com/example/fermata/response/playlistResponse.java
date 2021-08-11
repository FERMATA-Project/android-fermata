package com.example.fermata.response;

import com.example.fermata.domain.Playlist;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class playlistResponse {
    @SerializedName("code")
    public String code;
    @SerializedName("playlist")
    public List<Playlist> playlist;
}
