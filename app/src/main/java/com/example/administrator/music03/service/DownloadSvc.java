package com.example.administrator.music03.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.administrator.music03.Activity.MainActivity1;
import com.example.administrator.music03.R;

import java.io.File;

public class DownloadSvc extends Service {
    private downloadTask taskDownload;
    private String downloadUrl;
    private DownloadBinder binder=new DownloadBinder();
    private downloadListener listener=new downloadListener() {
        @Override
        public void onSucceed()
        {
            taskDownload=null;
            //将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Success",-1));
            Toast.makeText(DownloadSvc.this,"Download Success",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed()
        {
            taskDownload=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Failed",-1));
            Toast.makeText(DownloadSvc.this,"Download FAILED",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled()
        {
            taskDownload=null;
            stopForeground(true);
            Toast.makeText(DownloadSvc.this,"Canceled",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused()
        {
            taskDownload=null;
            Toast.makeText(DownloadSvc.this,"Paused",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgress(int progress)
        {
            getNotificationManager().notify(1,getNotification("Downloading...",progress));
        }
    };
    public DownloadSvc()
    {

    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    private NotificationManager getNotificationManager()
    {
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    private Notification getNotification(String title,int progress)
    {
        Intent intent=new Intent(this, MainActivity1.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if(progress>0)
        {
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return new DownloadBinder();
    }
    public class DownloadBinder extends Binder{
        public void startDownload(String url)
        {
            if(taskDownload==null)
            {
                downloadUrl=url;
                taskDownload=new downloadTask(listener);
                taskDownload.execute(downloadUrl);
                startForeground(1,getNotification("Downloading...",0));
                Toast.makeText(DownloadSvc.this,"Downloading...",Toast.LENGTH_SHORT).show();
            }
        }
        public void pauseDownload()
        {
            if(taskDownload!=null)
            {
                taskDownload.pauseDownload();
            }
        }
        public void cancelDownload()
        {
            if(taskDownload!=null)
            {
                taskDownload.cancelDownload();
            }
            else
            {
                if(downloadUrl!=null)
                {
                    String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory= Environment.getExternalStorageDirectory().toString();
                    File file=new File(directory+fileName);
                    if(file.exists())
                        file.delete();
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadSvc.this,"Canceled",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

