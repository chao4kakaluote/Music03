package com.example.administrator.music03.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import com.example.administrator.music03.R;
import com.example.administrator.music03.Utils.Utility;
import com.example.administrator.music03.customView.LycicView;
import com.example.administrator.music03.entries.LrcMusic;
import com.example.administrator.music03.entries.Music;
import com.example.administrator.music03.service.MusicPlayService;
import java.io.InputStream;
import java.util.ArrayList;

public class MusicPlay extends AppCompatActivity
{
    private MusicPlayService.MusicControl musicBinder;
    private static SeekBar progressBar;
    private ImageView start;
    private Music myMusic=null;
    LycicView view;
    Handler handler1 ;
    private ArrayList<LrcMusic> lrcs;
    private ArrayList<String> list;
    private ArrayList<Long> list1;
    private int lrc_index;

    public static Handler handler=null;

    private ServiceConnection connection=new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            musicBinder=(MusicPlayService.MusicControl)iBinder;
            musicBinder.setMusic(myMusic);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                Bundle b=msg.getData();
                int duration=b.getInt("duration");
                int currentPosition=b.getInt("currentPosition");
                progressBar.setMax(duration);
                progressBar.setProgress(currentPosition);
            }
        };
        progressBar=(SeekBar)findViewById(R.id.music_progress);
        //得到传入的音乐对象。
        myMusic=getMusic();
        //绑定一个音乐的服务
        startMusicPlayService();
        //点击按钮开始播放音乐
        start=(ImageView)findViewById(R.id.playing_play);
        start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //点击按钮开始播放音乐
                Log.d("play","true");
                musicBinder.play();
                showLrc();
            }
        });
    }
    public void startMusicPlayService()
    {
        Intent intent=new Intent(this,MusicPlayService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }
    public Music getMusic()
    {
        Music music=(Music)getIntent().getSerializableExtra("music");
        return music;
    }
    //活动结束时解除和Service的绑定。
    public void showLrc()
    {
          initViews();
      //    initEvents();
    }
    private void initViews()
    {
        view = (LycicView) findViewById(R.id.view);
    }
//    private void initEvents() {
//        InputStream is = getResources().openRawResource(R.raw.eason_tenyears);
//        list = new ArrayList<String>();
//        list1 = new ArrayList<>();
//        //得到歌词对象的一个列表
//        lrcs = Utility.redLrc(is);
//        for (int i = 0; i < lrcs.size(); i++)
//        {
//            list.add(lrcs.get(i).getLrc());
//            System.out.println(lrcs.get(i).getLrc() + "=====");
//            list1.add(0l);
//        }
//        //给view设置显示的歌词和时间
//        view.setLyricText(list, list1);
//        //view经过一秒后滚动到第0行。
//        view.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                view.scrollToIndex(0);
//            }
//        }, 1000);
//        //view滚动到相应位置。
//
//        //view滑动时的响应
//        view.setOnLyricScrollChangeListener(new LycicView.OnLyricScrollChangeListener() {
//            @Override
//            public void onLyricScrollChange(final int index, int oldindex)
//            {
//                handler = new Handler(new Handler.Callback()
//                {
//                    @Override
//                    public boolean handleMessage(Message msg)
//                    {
//                        if(msg.what == 1){
//                            lrc_index = index;
//                            if(lrc_index == list.size()){
//                                handler.removeMessages(1);
//                            }
//                            lrc_index++;
//                            view.scrollToIndex(lrc_index);
//                            handler.sendEmptyMessageDelayed(1,4000);
//                        }
//                        return false;
//                    }
//                });
//                handler.sendEmptyMessageDelayed(1,4000);
//            }
//        });
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//
//    }
    @Override
    protected void onDestroy()
    {
        unbindService(connection);
        super.onDestroy();
    }
}
