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
import com.example.fermata.domain.Playlist;

import java.util.ArrayList;

public class MyPlayListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    private ArrayList<Playlist> myplayList = null;
    private MyPlayListAdapter.OnItemClickListener listener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(MyPlayListAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public MyPlayListAdapter(Context context, ArrayList<Playlist> musicList){
        this.context = context;
        this.myplayList = musicList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.playlist_list_item, parent,false);
        return new MyPlayListAdapter.MyPlayListViewHolder(view);
    }

    // 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((MyPlayListViewHolder)viewHolder).tv_listName.setText(myplayList.get(position).getListName());
        ((MyPlayListViewHolder)viewHolder).tv_singCount.setText(myplayList.get(position).getSingCount());
    }

    // 전체 데이터 개수 리턴
    @Override
    public int getItemCount() {
        return myplayList.size();
    }

    // 뷰 홀더
    public class MyPlayListViewHolder extends RecyclerView.ViewHolder{
        TextView tv_listName;
        TextView tv_singCount;

        public MyPlayListViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_listName = itemView.findViewById(R.id.tv_listName);
            tv_singCount = itemView.findViewById(R.id.tv_singCount);

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
