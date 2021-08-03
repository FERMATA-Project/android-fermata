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

public class LatelyMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    private ArrayList<Music> latelymusicList = null;
    private LatelyMusicAdapter.OnItemClickListener listener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(LatelyMusicAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public LatelyMusicAdapter(Context context, ArrayList<Music> musicList){
        this.context = context;
        this.latelymusicList = musicList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.lately_music_item_musiclist, parent,false);
        return new LatelyMusicAdapter.LatelyMusicViewHolder(view);
    }

    // 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((LatelyMusicViewHolder)viewHolder).tv_lately_musicName.setText(latelymusicList.get(position).getMusicName());
        ((LatelyMusicViewHolder)viewHolder).tv_lately_singerName.setText(latelymusicList.get(position).getSingerName());
    }

    // 전체 데이터 개수 리턴
    @Override
    public int getItemCount() {
        return latelymusicList.size();
    }

    // 뷰 홀더
    public class LatelyMusicViewHolder extends RecyclerView.ViewHolder{
        TextView tv_lately_musicName;
        TextView tv_lately_singerName;

        public LatelyMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_lately_musicName = itemView.findViewById(R.id.tv_lately_musicName);
            tv_lately_singerName = itemView.findViewById(R.id.tv_lately_singerName);

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
