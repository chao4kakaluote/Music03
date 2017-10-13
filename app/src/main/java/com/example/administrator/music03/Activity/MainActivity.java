package com.example.administrator.music03.Activity;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.content.ServiceConnection;
        import android.content.res.ColorStateList;
        import android.graphics.Color;
        import android.os.Handler;
        import android.os.IBinder;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.HorizontalScrollView;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.TextView;
        import com.example.administrator.music03.Adapter.LocalMusicAdapter;
        import com.example.administrator.music03.R;
        import com.example.administrator.music03.Utils.Utility;
        import com.example.administrator.music03.customView.LycicView;
        import com.example.administrator.music03.entries.LrcMusic;
        import com.example.administrator.music03.entries.Music;
        import com.example.administrator.music03.service.MusicPlayService;

        import java.util.ArrayList;
        import java.util.List;
public class MainActivity extends AppCompatActivity {
    //    private HorizontalScrollView hs_activity_tabbar;
    private RadioGroup myRadioGroup;
    private List<String> titleList;
    private LinearLayout ll_activity_tabbar_content;
    private String channel;
    private ListView musicList;
    private LocalMusicAdapter Localadapter;
    private ArrayAdapter<String> Downloadadapter;
    private List<Music> musicLocalData=new ArrayList<>();
    private List<String> musicDownloadData=new ArrayList<>();

    private MusicPlayService.MusicControl musicBinder;
    private LinearLayout layout;
    private ImageView musicImage;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            musicBinder=(MusicPlayService.MusicControl)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleList = new ArrayList<String>();
        titleList.add("本地音乐");
        titleList.add("下载音乐");
        musicList=(ListView)findViewById(R.id.musicList);
        layout=(LinearLayout)findViewById(R.id.musicPicLayout);
        musicImage=(ImageView)findViewById(R.id.musicPic);
        startMusicService();
        initGroup();
    }
    public void setMusicImageClick(final Music music)
    {
        musicImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                  EnterMusicPlay(music);
            }
        });
    }
    //将音乐列表显示出来
    public void setLocalList()
    {
        musicLocalData.clear();
        musicLocalData= Utility.getLocalMusicList();
//        Localadapter=new LocalMusicAdapter(this, R.layout.localmusicitem, musicLocalData, new LocalMusicAdapter.OnListClickListener() {
//            @Override
//            public void onClickListPlay(Music music,ImageView play)
//            {
//                //为下面的图片设置监听跳转
//                setMusicImageClick(music);
//                //如果没有播放，则判断是否处于暂停状态并且继续播放，否则重新播放。
//                 if(!musicBinder.isPlay())
//                 {
//                     if(musicBinder.getMusic()!=null && music.getMusicName().equals(musicBinder.getMusic().getMusicName()))
//                     {
//                         musicBinder.continuePlay();
//                     }
//                     else
//                     {
//                         musicBinder.setMusic(music);
//                         musicBinder.play();
//                         layout.setVisibility(View.VISIBLE);
//                         musicImage.setImageResource(R.drawable.music_disc);
//                     }
//                     play.setImageResource(R.drawable.pause);
//                 }
//                 //如果正在播放并且点击了另一首音乐，则重新播放另一首音乐。
//                 else
//                 {
//                       if(musicBinder.getMusic().getMusicName().equals(musicBinder.getMusic().getMusicName()))
//                       {
//                           musicBinder.pausePlay();
//                       }
//                       else if(!musicBinder.getMusic().getMusicName().equals(musicBinder.getMusic().getMusicName()))
//                       {
//                           musicBinder.setMusic(music);
//                           musicBinder.play();
//                           layout.setVisibility(View.VISIBLE);
//                           musicImage.setImageResource(R.drawable.music_disc);
//                       }
//                       play.setImageResource(R.drawable.play);
//                 }
//
//            }
//            @Override
//            public void onClickListReplay(Music music)
//            {
//                 musicBinder.setMusic(music);
//                 musicBinder.play();
//            }
//        });
        musicList.setAdapter(Localadapter);
        setOnLocalClick();
    }

    public void EnterMusicPlay(Music music)
    {
        Bundle bundle=new Bundle();
        Intent intent=new Intent(MainActivity.this,MusicPlay.class);
        intent.putExtra("music",music);
        startActivity(intent);
    }
    public void setOnLocalClick()
    {
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                      Music music=musicLocalData.get(position);
                      EnterMusicPlay(music);
            }
        });
    }
    public void setDownloadList()
    {
        musicDownloadData.clear();
        for(int i=0;i<20;i++)
        {
            musicDownloadData.add("music"+i+2);
        }
        Downloadadapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,musicDownloadData);
        musicList.setAdapter(Downloadadapter);
    }

    //开始播放
    public void startMusicService()
    {
            Intent intent=new Intent(this,MusicPlayService.class);
            bindService(intent,connection,BIND_AUTO_CREATE);
    }
    //暂停播放
    public void pauseMusicService()
    {
            if(musicBinder!=null)
                musicBinder.pausePlay();
    }
    private void initGroup()
    {
        ll_activity_tabbar_content= (LinearLayout) this.findViewById(R.id.ll_activity_tabbar_content);
        //对RadioButton进行设置
        myRadioGroup = new RadioGroup(this);
        myRadioGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        myRadioGroup.setOrientation(LinearLayout.HORIZONTAL);
        ll_activity_tabbar_content.addView(myRadioGroup);
        for (int i = 0; i < titleList.size(); i++)
        {
            String channel = titleList.get(i);
            RadioButton radio = new RadioButton(this);
            radio.setButtonDrawable(android.R.color.transparent);
            ColorStateList csl = getResources().getColorStateList(R.color.colorPrimaryDark);
            radio.setTextColor(csl);
            LinearLayout.LayoutParams l = new LinearLayout.LayoutParams((int) dp2px(this, 175), ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            //设置加入的按钮
            radio.setLayoutParams(l);
            radio.setTextSize(15);
            radio.setGravity(Gravity.CENTER);
            radio.setText(channel);
            radio.setTag(channel);
            myRadioGroup.addView(radio);
        }

        myRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                int radioButtonId = group.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton) findViewById(radioButtonId);
                channel = (String) rb.getTag();
                if(channel.equals("本地音乐"))
                {
                    Log.d("Local","true");
                    setLocalList();
                }
                else
                    setDownloadList();
            }
        });
        //设定默认被选中的选项卡为第一项
        if (!titleList.isEmpty())
        {
            myRadioGroup.check(myRadioGroup.getChildAt(0).getId());
           // setLocalList();
        }
    }
    public static float dp2px(Context context, float dp)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dp * scale);
    }
}
