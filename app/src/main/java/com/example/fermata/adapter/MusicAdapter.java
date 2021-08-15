package com.example.fermata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fermata.R;
import com.example.fermata.RetrofitClient;
import com.example.fermata.domain.Music;
import com.example.fermata.response.musicResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        ((MusicViewHolder)viewHolder).tv_musicName.setText(musicList.get(position).getMusic_title());
        ((MusicViewHolder)viewHolder).tv_singerName.setText(musicList.get(position).getSinger());
        if(musicList.get(position).getLikes() == 1) {
            ((MusicViewHolder)viewHolder).btn_like.setChecked(true);
        } else {
            ((MusicViewHolder)viewHolder).btn_like.setChecked(false);
        }

        ((MusicViewHolder)viewHolder).btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((MusicViewHolder)viewHolder).btn_like.isChecked()) {
                    requestUpdateLike(musicList.get(position).getMusic_id(), 1);
                } else {
                    requestUpdateLike(musicList.get(position).getMusic_id(), 0);
                }
            }
        });
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
        CheckBox btn_like;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_musicName = itemView.findViewById(R.id.tv_musicName);
            tv_singerName = itemView.findViewById(R.id.tv_singerName);
            btn_like = itemView.findViewById(R.id.btn_like);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(listener != null){
                            listener.onItemClick(v,position);
                        }
                    }
                }
            });
        }
    }

    // 좋아요 상태 변경 메서드
    private void requestUpdateLike (int music_id, int like) {
        RetrofitClient.getApiService().requestUpdateLike(music_id, like).enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과

                    if(result.code.equals("400")) {
                        Toast.makeText(context, "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<musicResponse> call, Throwable t) {
                Toast.makeText(context, "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
