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
/**
 * Created by Administrator on 2017/10/9.
 */
public class LocalMusicAdapter extends ArrayAdapter<Music>
{
    private int resourceId;
    private OnListClickListener Mylistener;
    public LocalMusicAdapter(Context context, int ResourceId, List<Music> musicList,OnListClickListener listener)
    {
        super(context,ResourceId,musicList);
        resourceId=ResourceId;
        Mylistener=listener;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Music music=getItem(position);
        View view;
        ViewHolder holder;
        if(convertView==null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            holder=new ViewHolder();
            holder.musicName=(TextView)view.findViewById(R.id.musicName);
            holder.list_play=(ImageView)view.findViewById(R.id.list_play);
            holder.list_pause=(ImageView)view.findViewById(R.id.list_pause);
            holder.list_replay=(ImageView)view.findViewById(R.id.music_replay);
            view.setTag(holder);
        }
        else
        {
            view=convertView;
            holder=(ViewHolder)view.getTag();
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
        return view;
    }
    class ViewHolder
    {
        public TextView musicName;
        public ImageView list_play;
        public ImageView list_pause;
        public ImageView list_replay;
    }
    public interface  OnListClickListener
    {
        public void onClickListPlay(Music music);
        public void onClickListReplay(Music music);
        public void onClickListPause(Music music);
    }
}
