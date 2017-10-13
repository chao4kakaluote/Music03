package com.example.administrator.music03.Utils;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.music03.R;
import com.example.administrator.music03.entries.LrcMusic;
import com.example.administrator.music03.entries.Music;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * Created by Administrator on 2017/10/9.
 */
public class Utility
{
    public static String localMusicPath= Environment.getExternalStorageDirectory().toString()+"/MIUI/music";
    public static String downloadXMLlist="http://172.25.107.133:8080/de/res/xml/musicInfo.xml";
    public static String downloadMp3Url="http://172.25.107.133:8080/de/res/mp3/";
    public static String localDownloadMusicUrl=Environment.getExternalStorageDirectory().toString()+"/MIUI/music/mp3";
    public static List<Music> getLocalMusicList()
    {
        Log.d("path",localMusicPath);
        List<Music> list=new ArrayList<>();
        File mfile=new File(localMusicPath+"/mp3");
        //得到该路径下方的所有文件
        File[] files=mfile.listFiles();
        Log.d("filesSize",String.valueOf(files.length));
        boolean exist_music=false;
        for(int i=0;i<files.length;i++)
        {
            if(checkIsMusicFile(files[i].getPath()))
            {
                exist_music=true;
                Music music=new Music();
                music.setMusicName(files[i].getName());
                music.setImageId(R.drawable.shenglue);
                list.add(music);
            }
        }
        if(list.size()==1)
        {
            list.get(0).setNext(list.get(0));
            list.get(0).setPre(list.get(0));
        }
        else if(list.size()>1) {
            for (int i = 0; i < list.size(); i++) {
                if (i > 0)
                    list.get(i).setPre(list.get(i - 1));
                if (i < list.size() - 1)
                    list.get(i).setNext(list.get(i + 1));
            }
            list.get(0).setPre(list.get(list.size()-1));
            list.get(list.size()-1).setNext(list.get(0));
        }
        return list;
    }

    public static boolean checkIsMusicFile(String fName)
    {
        boolean isMusicFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        if (FileEnd.equals("mp3") || FileEnd.equals("mp4") || FileEnd.equals("wmv") || FileEnd.equals("aac") ) {
            isMusicFile = true;
        }
        else
        {
            isMusicFile = false;
        }
        return isMusicFile;
    }
    public static void sendRequest(String url, Callback callback)
    {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendPostRequest(String url,String argsName[],String args[],Callback callback)
    {
        OkHttpClient client=new OkHttpClient();
        FormBody.Builder form=new okhttp3.FormBody.Builder();
        for(int i=0;i<argsName.length;i++)
        {
            form.add(argsName[i],args[i]);
        }
        RequestBody formBody=form.build();
        Request request=new Request.Builder().url(url).post(formBody).build();
        client.newCall(request).enqueue(callback);
    }


    public static int lrcData(String time)
    {
        time = time.replace(":", "#");
        time = time.replace(".", "#");
        String[] mTime = time.split("#");
        //[03:31.42]
        int mtime = Integer.parseInt(mTime[0]);
        int stime = Integer.parseInt(mTime[1]);
        int mitime = Integer.parseInt(mTime[2]);

        int ctime = (mtime*60+stime)*1000+mitime*10;

        return ctime;
    }
    public static List<LrcMusic> getLrc(File file) {
        List<LrcMusic> lrcMusics=new ArrayList<>();

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String s = "";
            while ((s = br.readLine()) != null) {
                if (!TextUtils.isEmpty(s)) {
                    String lrc = s.replace("[", "");
                    String[] lrcAndTime = lrc.split("]");
                    if (lrcAndTime.length > 1) {
                        String time = lrcAndTime[0];
                        String lrcText = lrcAndTime[1];
                        LrcMusic lrcMusic = new LrcMusic(Utility.lrcData(time), lrcText);
                        lrcMusics.add(lrcMusic);
                        Log.d("time", time);
                        Log.d("text", lrcText);
                    }
                }
            }
            return lrcMusics;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lrcMusics;
    }
}
