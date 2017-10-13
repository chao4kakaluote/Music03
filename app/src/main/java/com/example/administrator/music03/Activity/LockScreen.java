package com.example.administrator.music03.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.music03.R;
import com.example.administrator.music03.Utils.Utility;
import com.example.administrator.music03.customView.LycicView;
import com.example.administrator.music03.entries.LrcMusic;
import com.example.administrator.music03.entries.Music;
import com.example.administrator.music03.service.MusicPlayService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LockScreen extends AppCompatActivity implements SensorEventListener {

    private MusicPlayService.MusicControl musicBinder;
    private static boolean isLrcExist=false;
    TextView  currentLrc;
    TextView totalSteps;
    SensorManager mSensorManager;
    //单次有效计步
    Sensor  mStepCount;
    //系统计步累加值
    Sensor  mStepDetector;

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
    //接收消息判断歌曲是否正在播放，如果正在播放则显示歌词。
    public static Handler lrcHandler=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        //得到textview来显示歌词
        currentLrc=(TextView)findViewById(R.id.currentLrc);
        totalSteps=(TextView)findViewById(R.id.TotalSteps);
        //初始化消息
        initHandler();
        //注册传感器
        registerSensor();
        //绑定音乐服务
        bindMusicService();
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|               //这个在锁屏状态下
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        newWakeLock = manager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
//        newWakeLock.acquire();//点亮屏幕(常亮)

        Log.d("showView","true");
    }
    public void initHandler()
    {
        lrcHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d("lockmessage","msg");
                    //接收消息，根据时间滑动到指定位置。
                    Bundle bundle=msg.getData();
                    int currentPosition=bundle.getInt("currentPosition");
                //如果当在播放音乐
                    if(musicBinder!=null && musicBinder.getMusic()!=null)
                    {
                        Music music=musicBinder.getMusic();
                        String path = Utility.localMusicPath + "/lyric/" + musicBinder.getMusic().getCompleteMusicName();
                        path = path.replace(".mp3", ".lrc");
                        Log.d("lrcPath",path);
                        File file = new File(path);
                        List<LrcMusic> lrcList=new ArrayList<>();
                        if(file.exists())
                        {
                            lrcList=Utility.getLrc(file);
                            String lrcText="";
                            int i=0;
                            for(i=0;i<lrcList.size();i++)
                            {
                                if(lrcList.get(i).getTime()>=currentPosition)
                                    break;
                            }
                            if(i>=1)
                                lrcText=lrcList.get(i-1).getLrc();
                            //给textview设置当前歌词
                             currentLrc.setText(lrcText);
                        }
                        }
                    }
                };

        }

    private void bindMusicService()
    {

        Intent intent=new Intent(this,MusicPlayService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    public void registerSensor()
    {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mStepCount = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager.registerListener(this, mStepCount, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            //event.values[0]为计步历史累加值
            totalSteps.setText(event.values[0] + "步");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    @Override
    protected void onDestroy() {
//        newWakeLock.release();//熄灭屏幕
        super.onDestroy();
    }

}
