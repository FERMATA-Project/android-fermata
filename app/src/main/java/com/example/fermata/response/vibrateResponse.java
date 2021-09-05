package com.example.fermata.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class vibrateResponse {
    @SerializedName("amplitude")
    public List<Integer> amplitude; // 진동 세기
}
