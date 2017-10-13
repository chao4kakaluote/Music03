package com.example.administrator.music03.service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import com.example.administrator.music03.Activity.LockScreen;
import com.example.administrator.music03.Activity.MainActivity1;
import com.example.administrator.music03.Activity.MusicPlay;
import com.example.administrator.music03.Activity.MusicPlay1;
import com.example.administrator.music03.R;
import com.example.administrator.music03.Utils.Utility;
import com.example.administrator.music03.entries.Music;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;

import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MusicPlayService extends Service
{
    public Music preMusic;
    public Music myMusic;
    private MediaPlayer myMediaPlayer;

    private ButtonBroadcastReceiver receiver;
    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private NotificationCompat.Builder mBuilder;
    public static String ACTION_BUTTON="send Broadcast";
    public static final int PRE_STATUS=1;
    public static final int PLAY_STATUS=2;
    public static final int PAUSE_STATUS=3;
    public static final int NEXT_STATUS=4;


    private MusicControl musicControl=null;
    private static Timer timer;
    public boolean isPlaying=false;

    @Override
    public void onCreate() {
        super.onCreate();
        initBroadcastReceiver();
        initLockActivity();
    }

    public MusicPlayService()
    {
    }
    public void setMusic(Music music)
    {
        if(this.myMusic!=null)
            preMusic=myMusic;
        this.myMusic=music;
    }
    public void Play()
    {
        try
        {
            myMediaPlayer=new MediaPlayer();
            myMediaPlayer.reset();
            Log.d("url",Utility.localMusicPath + "/mp3/" + myMusic.getCompleteMusicName());
            myMediaPlayer.setDataSource(Utility.localMusicPath + "/mp3/" + myMusic.getCompleteMusicName());
            myMediaPlayer.prepare();
            myMediaPlayer.start();
            isPlaying=true;
            showNotification();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void playMusic()
    {
         if(myMediaPlayer==null)
         {
             Play();
         }
         else
         {
             myMediaPlayer.release();
             Play();
         }
         //更新进度条
        updateProgress();
         //更新按钮
        updatePlayButton();
        //更新锁屏歌词
        updateLockProgress();
    }
    public void playOnline()
    {
        try
        {
            myMediaPlayer=new MediaPlayer();
            myMediaPlayer.reset();
            Log.d("url",Utility.localMusicPath + "/mp3/" + myMusic.getCompleteMusicName());
            myMediaPlayer.setDataSource("http://172.107.133:8080/de/res/mpp3/"+myMusic.getCompleteMusicName());
            myMediaPlayer.prepare();
            myMediaPlayer.start();
            isPlaying=true;
            showNotification();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void playOnLineMusic()
    {
        if(myMediaPlayer==null)
        {
            playOnline();
        }
        else
        {
            myMediaPlayer.release();
            playOnline();
        }
    }

    public void pausePlay()
    {
        myMediaPlayer.pause();
        isPlaying=false;
        updatePlayButton();
    }
    public void continuePlay()
    {
        myMediaPlayer.start();
        isPlaying=true;
        updatePlayButton();
    }
    public void playNext()
    {
        setMusic(myMusic.getNext());
        playMusic();
        Log.d("myMusicName",myMusic.getMusicName());
        updateLrc();
    }
    public void playPre()
    {
        setMusic(myMusic.getPre());
        playMusic();
        updateLrc();
    }
    public void seekTo(int progress)
    {
        myMediaPlayer.seekTo(progress);
    }
    public void updatePlayButton()
    {
        Message message=new Message();
        if(isPlaying)
            message.what=1;
        else
            message.what=0;
        final Message msg=message;
        if(MusicPlay1.playHandler!=null)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run() {
                    MusicPlay1.playHandler.sendMessage(msg);
                }
            }).start();
        }
    }
    public void updateLrc()
    {
        Message message=new Message();
        message.what=1;
        final Message msg=message;
        if(MusicPlay1.lrcHandler!=null)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run() {
                    MusicPlay1.lrcHandler.sendMessage(msg);
                }
            }).start();
        }
    }
    public void updateLockProgress()
    {
         Timer timer1;
            timer1=new Timer();
            timer1.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    int duration=myMediaPlayer.getDuration();
                    int currentPosition=myMediaPlayer.getCurrentPosition();
                    Log.d("schedule1",String.valueOf(Thread.currentThread().getId()));
                    Message msg=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putInt("currentPosition",currentPosition);
                    msg.setData(bundle);
                    msg.what=0;
                    if(LockScreen.lrcHandler!=null)
                        LockScreen.lrcHandler.sendMessage(msg);
                }
            },5,500);
    }
    public void updateProgress()
    {
        if(timer==null)
        {
            timer=new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    int duration=myMediaPlayer.getDuration();
                    int currentPosition=myMediaPlayer.getCurrentPosition();
                    Log.d("schedule1",String.valueOf(Thread.currentThread().getId()));
                    Message msg=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putInt("duration",duration);
                    bundle.putInt("currentPosition",currentPosition);
                    msg.setData(bundle);
                    msg.what=0;
                    if(MusicPlay1.handler!=null)
                    MusicPlay1.handler.sendMessage(msg);
                }
            },5,500);
        }
    }

    public void showNotification()
    {
        mNotificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mBuilder= new NotificationCompat.Builder(this);
        mRemoteViews=new RemoteViews(getPackageName(), R.layout.notification);

   //     mRemoteViews.setImageViewResource(R.id.notification_image_view,R.drawable.play_disc_halo);
        mRemoteViews.setTextViewText(R.id.notification_text_view, myMusic.getMusicName());
        mRemoteViews.setTextColor(R.id.notification_text_view, Color.BLACK);

        //点击的事件处理
        Intent buttonIntent = new Intent(ACTION_BUTTON);
		/* 上一首按钮 */
        buttonIntent.putExtra("buttonId", PRE_STATUS);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_pre, intent_prev);
		/* 播放/暂停  按钮 */
        buttonIntent.putExtra("buttonId", PLAY_STATUS);
        PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_play, intent_paly);
        /* 播放/暂停  按钮 */
        buttonIntent.putExtra("buttonId", PAUSE_STATUS);
        PendingIntent intent_pause = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_pause, intent_pause);
		/* 下一首 按钮  */
        buttonIntent.putExtra("buttonId", NEXT_STATUS);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 4, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_next, intent_next);
        Intent intent=new Intent(this, MainActivity1.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        mBuilder.setContent(mRemoteViews)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("正在播放")
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.music_disc));
        //加载
        mNotificationManager.notify(1, mBuilder.build());
    }
    public void initBroadcastReceiver()
    {
        receiver=new ButtonBroadcastReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        registerReceiver(receiver,intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        musicControl=new MusicControl();
        return musicControl;
    }
    public class MusicControl extends Binder
    {
        public void setMusic(Music music)
        {
            myMusic=music;
        }
        public Music getMusic()
        {
            return myMusic;
        }
        public void play()
        {
            MusicPlayService.this.playMusic();
            Log.d("binderPlay","true");
        }
        public void playOnline()
        {
            MusicPlayService.this.playOnLineMusic();
        }
        public void pausePlay()
        {
            MusicPlayService.this.pausePlay();
        }
        public void continuePlay()
        {
            MusicPlayService.this.continuePlay();
        }
        public void playNextMusic()
        {
            playNext();
        }
        public void playPreMusic()
        {
            playPre();
        }
        public void seekTo(int progress)
        {
            MusicPlayService.this.seekTo(progress);
        }
        public int getDuration()
        {
            int duration=0;
            if(myMediaPlayer!=null && getMusic()!=null)
            {
                 duration=myMediaPlayer.getDuration();
            }
            return duration;
        }

        public boolean isPlay()
        {
            return isPlaying;
        }
    }


    public class ButtonBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
              String action=intent.getAction();
              if(action.equals(ACTION_BUTTON))
              {
                  int buttonId=intent.getIntExtra("buttonId",0);
                  switch(buttonId)
                  {
                      case PRE_STATUS:
                          Log.d("pre_status","true");
                          if(myMusic!=null)
                          {
                              playPre();
                          }
                          break;
                      case PLAY_STATUS:
                          Log.d("play_status","true");
                          if(myMusic!=null && myMediaPlayer!=null && !isPlaying)
                          {
                              continuePlay();
                          }
                          break;
                      case PAUSE_STATUS:
                          Log.d("pause_status","true");
                          if(isPlaying)
                          {
                              pausePlay();
                          }
                          break;
                      case NEXT_STATUS:
                          Log.d("next_status","true");
                          if(myMusic!=null)
                          {
                              playNext();
                          }
                          break;
                  }
              }
        }
    }
    private ScreenBroadcastReceiver mScreenReceiver;
    private void initLockActivity()
    {
        mScreenReceiver=new ScreenBroadcastReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_SCREEN_OFF);
        registerReceiver(mScreenReceiver,intentFilter);
    }

    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action))
            {
                // 开屏
            }
            else if (ACTION_SCREEN_OFF.equals(action)) {
                //如果当前音乐不为空
                if(myMusic!=null) {
                    Intent intent1 = new Intent(MusicPlayService.this, LockScreen.class);
                    intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
                // 锁屏
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                // 解锁
            }
        }
    }
}
