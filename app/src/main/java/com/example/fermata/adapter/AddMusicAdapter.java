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
import com.example.fermata.domain.AddPlaylist;
import com.example.fermata.domain.Music;
import com.example.fermata.response.musicResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private ArrayList<Music> AddPlayList = null;
    private String make_list_title = "";
    private OnItemClickListener listener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AddMusicAdapter(Context context, ArrayList<Music> Addplaylist, String make_list_title){
        this.context = context;
        this.AddPlayList = Addplaylist;
        this.make_list_title = make_list_title;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.add_playlist_item_musiclist, parent,false);
        return new AddMusicAdapter.AddMusicViewHolder(view);
    }

    // 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((AddMusicViewHolder)viewHolder).tv_musicName.setText(AddPlayList.get(position).getMusic_title());
        ((AddMusicViewHolder)viewHolder).tv_singerName.setText(AddPlayList.get(position).getSinger());
        ((AddMusicViewHolder)viewHolder).btn_add.setChecked(false);

        ((AddMusicViewHolder)viewHolder).btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((AddMusicViewHolder)viewHolder).btn_add.isChecked()) {
                    requestAddPlaylist(AddPlayList.get(position).getMusic_id());
                }
            }
        });
    }

    // 전체 데이터 개수 리턴
    @Override
    public int getItemCount() {
        return AddPlayList.size();
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

    // 좋아요 상태 변경 메서드
    private void requestAddPlaylist (int music_id) {
        final Call<com.example.fermata.domain.AddPlaylist> addplaylist = RetrofitClient.getApiService().requestAddPlaylist(make_list_title, music_id);

        addplaylist.enqueue(new Callback<com.example.fermata.domain.AddPlaylist>() {
            @Override
            public void onResponse(Call<com.example.fermata.domain.AddPlaylist> call, Response<com.example.fermata.domain.AddPlaylist> response) {
                final AddPlaylist addplaylist = response.body();
                Toast.makeText(context, "서버에 값을 전달했습니다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<com.example.fermata.domain.AddPlaylist> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "서버와 통신중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(context, "추가가 완료되었습니다." + make_list_title + music_id, Toast.LENGTH_SHORT).show();
    }
}
