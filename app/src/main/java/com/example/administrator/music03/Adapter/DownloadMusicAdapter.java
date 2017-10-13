package com.example.administrator.music03.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.music03.R;
import com.example.administrator.music03.entries.Music;

import java.util.List;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by Administrator on 2017/10/12.
 */

public class DownloadMusicAdapter  extends ArrayAdapter<Music> {
    private int resourceId;
    private DownloadMusicAdapter.OnListClickListener Mylistener;
    public DownloadMusicAdapter(Context context, int ResourceId, List<Music> musicList, DownloadMusicAdapter.OnListClickListener listener)
    {
        super(context,ResourceId,musicList);
        resourceId=ResourceId;
        Mylistener=listener;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Music music=getItem(position);
        View view;
        DownloadMusicAdapter.ViewHolder holder;
        if(convertView==null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            holder=new DownloadMusicAdapter.ViewHolder();
            holder.musicName=(TextView)view.findViewById(R.id.musicName);
            holder.list_download=(ImageView)view.findViewById(R.id.music_download);
            holder.list_play=(ImageView)view.findViewById(R.id.list_play);
            holder.list_pause=(ImageView)view.findViewById(R.id.list_pause);
            holder.list_replay=(ImageView)view.findViewById(R.id.music_replay);
            view.setTag(holder);
        }
        else
        {
            view=convertView;
            holder=(DownloadMusicAdapter.ViewHolder)view.getTag();
        }
        //给播放和暂停按钮添加功能
        holder.musicName.setText(music.getMusicName());
        final ImageView play=holder.list_play;
        holder.list_play.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Mylistener.onClickListPlay(music);
            }
        });

        holder.list_pause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Mylistener.onClickListPause(music);
            }
        });

        holder.list_replay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Mylistener.onClickListReplay(music);
            }
        });
        holder.list_download.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Mylistener.onDownloadMusic(music);
            }
        });
        return view;
    }
    class ViewHolder
    {
        public TextView musicName;
        public ImageView list_download;
        public ImageView list_play;
        public ImageView list_pause;
        public ImageView list_replay;
    }
    public interface  OnListClickListener
    {
        public void onDownloadMusic(Music music);
        public void onClickListPlay(Music music);
        public void onClickListReplay(Music music);
        public void onClickListPause(Music music);
    }
}
