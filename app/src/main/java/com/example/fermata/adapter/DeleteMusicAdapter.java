package com.example.fermata.adapter;

import android.content.ClipData;
import android.content.Context;
import android.view.Gravity;
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
import com.example.fermata.domain.AddPlaylist;
import com.example.fermata.domain.Music;
import com.example.fermata.response.musicResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private ArrayList<Music> playlist = null;
    public ArrayList<Integer> deleteList =  new ArrayList<>();
    private String playlist_title = ""; // 재생목록 이름
    private OnItemClickListener listener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public DeleteMusicAdapter(Context context, ArrayList<Music> playlist, String playlist_title){
        this.context = context;
        this.playlist = playlist;
        this.playlist_title = playlist_title;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.add_playlist_item_musiclist, parent,false);
        return new DeleteMusicAdapter.AddMusicViewHolder(view);
    }


    // 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((AddMusicViewHolder)viewHolder).tv_musicName.setText(playlist.get(position).getMusic_title());
        ((AddMusicViewHolder)viewHolder).tv_singerName.setText(playlist.get(position).getSinger());
        ((AddMusicViewHolder)viewHolder).btn_add.setChecked(false);

        ((AddMusicViewHolder)viewHolder).btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((AddMusicViewHolder)viewHolder).btn_add.isChecked()) {
                    deleteList.add(playlist.get(position).getMusic_id());
                }
                else {
                    int idx = deleteList.indexOf(playlist.get(position).getMusic_id());
                    deleteList.remove(idx-1);
                }
            }
        });
    }

    // 전체 데이터 개수 리턴
    @Override
    public int getItemCount() {
        return playlist.size();
    }

    // 뷰 홀더
    public class AddMusicViewHolder extends RecyclerView.ViewHolder{
        TextView tv_musicName;
        TextView tv_singerName;
        CheckBox btn_add;

        public AddMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_musicName = itemView.findViewById(R.id.tv_musicName);
            tv_singerName = itemView.findViewById(R.id.tv_singerName);
            btn_add = itemView.findViewById(R.id.btn_add);

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
}
