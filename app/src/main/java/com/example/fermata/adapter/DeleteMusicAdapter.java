package com.example.fermata.adapter;

import android.content.Context;
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
        return new DeleteMusicAdapter.DeleteMusicViewHolder(view);
    }


    // 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((DeleteMusicAdapter.DeleteMusicViewHolder)viewHolder).tv_musicName.setText(playlist.get(position).getMusic_title());
        ((DeleteMusicAdapter.DeleteMusicViewHolder)viewHolder).tv_singerName.setText(playlist.get(position).getSinger());
        ((DeleteMusicAdapter.DeleteMusicViewHolder)viewHolder).btn_add.setChecked(false);

        ((DeleteMusicAdapter.DeleteMusicViewHolder)viewHolder).btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((DeleteMusicAdapter.DeleteMusicViewHolder)viewHolder).btn_add.isChecked()) {
                    deleteList.add(playlist.get(position).getMusic_id());

                    if(playlist_title.equals("좋아요한 음악목록")){
                        requestUpdateLike(playlist.get(position).getMusic_id(), 0);
                    }else{
                        requestDelPlaylist(playlist.get(position).getMusic_id());
                    }
                } else {
                    int idx = deleteList.indexOf(playlist.get(position).getMusic_id());
                    deleteList.remove(idx-1);

                    if(playlist_title.equals("좋아요한 음악목록")){
                        requestUpdateLike(playlist.get(position).getMusic_id(), 1);
                    }else{
                        requestAddPlaylist(playlist.get(position).getMusic_id());
                    }
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
    public class DeleteMusicViewHolder extends RecyclerView.ViewHolder{
        TextView tv_musicName;
        TextView tv_singerName;
        CheckBox btn_add;

        public DeleteMusicViewHolder(@NonNull View itemView) {
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



    // 재생목록에 음악 삭제 메서드
    private void requestDelPlaylist (int music_id) {
        RetrofitClient.getApiService().requestDelPlaylist(playlist_title, music_id).enqueue(new Callback<com.example.fermata.domain.AddPlaylist>() {
            @Override
            public void onResponse(Call<com.example.fermata.domain.AddPlaylist> call, Response<com.example.fermata.domain.AddPlaylist> response) {
                final AddPlaylist delplaylist = response.body();
                Toast.makeText(context, "서버에 값을 전달했습니다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<com.example.fermata.domain.AddPlaylist> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "서버와 통신중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 재생목록에 음악 추가 메서드
    private void requestAddPlaylist (int music_id) {
        RetrofitClient.getApiService().requestAddPlaylist(playlist_title, music_id).enqueue(new Callback<com.example.fermata.domain.AddPlaylist>() {
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
    }

    // 좋아요 상태 변경 메서드
    private void requestUpdateLike (int music_id, int like) {
        RetrofitClient.getApiService().requestUpdateLike(music_id, like).enqueue(new Callback<musicResponse>() {
            @Override
            public void onResponse(Call<musicResponse> call, Response<musicResponse> response) {
                if(response.isSuccessful()){
                    musicResponse result = response.body(); // 응답 결과
                    Toast.makeText(context, "서버에 값을 전달했습니다", Toast.LENGTH_SHORT).show();

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