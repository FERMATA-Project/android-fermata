package com.example.fermata;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

// 설명: 리사이클러뷰 아이템 간격 데코레이터
public class WidthItemDecorator extends RecyclerView.ItemDecoration {
    private final int divWidth;

    public WidthItemDecorator(int divWidth)
    {
        this.divWidth = divWidth;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.right = divWidth;
    }
}