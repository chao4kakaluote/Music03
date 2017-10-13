package com.example.administrator.music03.service;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.administrator.music03.Utils.Utility;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chao4kakaluote on 2017/10/4.
 */
public class downloadTask  extends AsyncTask<String,Integer,Integer>
{
    public final int SUCCEED=1;
    public final int FAILED=2;
    public final int PAUSED=3;
    public final int CANCELED=4;

    public boolean isCanceled=false;
    public boolean isPaused=false;
    public int lastProgress=0;
    private downloadListener listener;

    public downloadTask(downloadListener listener)
    {
        this.listener=listener;
    }
    @Override
    //完成下载功能，返回一个结果，onPostExecute方法中根据返回的不同结果调用不同的方法。
    protected Integer doInBackground(String... params)
    {
        InputStream is=null;
        RandomAccessFile savedFile=null;
        File file=null;
        try {
            //先检查目录下的文件已经下载了多少。
            int downloadLength = 0;
            String downloadUrl = params[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Utility.localDownloadMusicUrl;
            file = new File(directory + fileName);
            if (file.exists()) {
                downloadLength = (int) file.length();
            }
            //得到文件的总长度
            Log.d("downloadLength",String.valueOf(downloadUrl));
            int totalLength = getTotalLength(downloadUrl);
            if (downloadLength == totalLength)
                return SUCCEED;
            else if (totalLength == 0)
                return FAILED;

            //发送请求，得到数据。
            String url = params[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().addHeader("RANGE", "bytes=" + downloadLength + "-").url(downloadUrl).build();
            Response response = client.newCall(request).execute();

            //将数据写入文件
            if(response!=null)
            {
                is=response.body().byteStream();
                savedFile=new RandomAccessFile(file,"rw");
                savedFile.seek(downloadLength);
                byte[] b=new byte[1024];
                int total=0;
                int len;
                while((len=is.read(b))!=-1)
                {
                    if(isCanceled)
                        return CANCELED;
                    else if(isPaused)
                        return PAUSED;
                    else
                        total+=len;
                    savedFile.write(b,0,len);
                    int progress=(int)((total+downloadLength)*100/totalLength);
                    publishProgress(progress);

                }
            }
            response.body().close();
            return SUCCEED;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(is!=null)
                    is.close();
                if(savedFile!=null)
                    savedFile.close();;
                if(isCanceled && file!=null)
                    file.delete();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return FAILED;
    }

    //这里根据返回的结果来更新进度条
    @Override
    protected void onProgressUpdate(Integer... values)
    {
       int progress=values[0];
        if(progress>lastProgress)
          listener.onProgress(progress);
        lastProgress=progress;
    }

    //根据不同的结果调用不同的方法。
    @Override
    protected void onPostExecute(Integer statues)
    {
        switch(statues)
        {
            case SUCCEED:
                listener.onSucceed();
                break;
            case FAILED:
                listener.onFailed();
                break;
            case PAUSED:
                listener.onPaused();
                break;
            case CANCELED:
                listener.onCanceled();
            default:
                break;
        }
    }

    public void pauseDownload()
    {
        isPaused=true;
    }
    public void cancelDownload()
    {
        isCanceled=true;
    }
    public int getTotalLength(String downloadUrl)
    {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(downloadUrl).build();
        long length=0;
        Response response=null;
        try {
            response = client.newCall(request).execute();
            if(response!=null && response.isSuccessful()) {
                length = response.body().contentLength();
                Log.d("FileLength", String.valueOf(length));
                return (int) length;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
           response.close();
        }
        Log.d("FileLength",String.valueOf(length));
        return (int)length;
    }
}
