package com.example.fermata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fermata.R;
import com.example.fermata.domain.Music;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private ArrayList<Music> musicList = null;
    private OnItemClickListener listener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MusicAdapter(Context context, ArrayList<Music> musicList){
        this.context = context;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.search_music_item_musiclist, parent,false);
        return new MusicAdapter.MusicViewHolder(view);
    }

    // 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((MusicViewHolder)viewHolder).tv_musicName.setText(musicList.get(position).getMusicName());
        ((MusicViewHolder)viewHolder).tv_singerName.setText(musicList.get(position).getSingerName());
    }

    // 전체 데이터 개수 리턴
    @Override
    public int getItemCount() {
        return musicList.size();
    }

    // 뷰 홀더
    public class MusicViewHolder extends RecyclerView.ViewHolder{
        TextView tv_musicName;
        TextView tv_singerName;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_musicName = itemView.findViewById(R.id.tv_musicName);
            tv_singerName = itemView.findViewById(R.id.tv_singerName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(listener !=null){
                            listener.onItemClick(v,position);
                        }
                    }
                }
            });
        }
    }
}
