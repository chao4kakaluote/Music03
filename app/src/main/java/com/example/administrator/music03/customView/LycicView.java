package com.example.administrator.music03.customView;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.administrator.music03.Utils.Utility;
import com.example.administrator.music03.entries.LrcMusic;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
/**
 * Created by 饶建雄 on 2016/8/21.
 */
public class LycicView extends ScrollView {
    LinearLayout rootView;
    LinearLayout lrcViewLayout;

    ArrayList<LrcMusic> lrcTextLsit = new ArrayList<LrcMusic>();
    ArrayList<Integer> heights = new ArrayList<Integer>();
    ArrayList<TextView> lrcTextViewList = new ArrayList<TextView>();

    int width = 0;
    int height = 0;

    public LycicView(Context context) {
        super(context);
    }

    public LycicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LycicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void getLrc(File file) {
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
                        lrcTextLsit.add(lrcMusic);
                        Log.d("time", time);
                        Log.d("text", lrcText);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        addLayout();
    }

    public void refreshView(File file) {
        if (lrcTextLsit != null)
            lrcTextLsit.clear();
        if (heights != null)
            heights.clear();
        if (file != null && file.exists()) {
            getLrc(file);
            addLayout();
        }
    }

    public void addLayout() {
        //重置
        if (this != null)
            removeAllViews();
        if (rootView != null)
            rootView.removeAllViews();
        rootView = new LinearLayout(getContext());
        rootView.setOrientation(LinearLayout.VERTICAL);
        //得到高度，设置上下的两个布局
        ViewTreeObserver vto1 = rootView.getViewTreeObserver();
        vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                height = LycicView.this.getHeight();
                width = LycicView.this.getWidth();
            }
        });
        LinearLayout blank1 = new LinearLayout(getContext());
        LinearLayout blank2 = new LinearLayout(getContext());
        //高度平分
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(500, 400);
        rootView.addView(blank1, params1);

        if (lrcViewLayout != null)
            lrcViewLayout.removeAllViews();
        if (heights.size() != 0) {
            heights.clear();
        }
        if (lrcTextViewList.size() != 0) {
            lrcTextViewList.clear();
        }

        lrcViewLayout = new LinearLayout(getContext());
        lrcViewLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < lrcTextLsit.size(); i++) {
            final TextView textView = new TextView(getContext());
            Log.d("lrctext", lrcTextLsit.get(i).getLrc());
            textView.setText(lrcTextLsit.get(i).getLrc());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            textView.setLayoutParams(params);


            ViewTreeObserver vto = textView.getViewTreeObserver();
            final int index = i;
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);//api 要在16以上 >=16
                    heights.add(index, textView.getHeight());//将高度添加到对应的item位置
                    Log.d("textViewheight", String.valueOf(textView.getHeight()));
                }
            });
            lrcViewLayout.addView(textView);
            lrcTextViewList.add(textView);
        }
        rootView.addView(lrcViewLayout);
        //加上下面的布局
        //     rootView.addView(blank2,params1);
        addView(rootView);
    }

    public void scrollToTime(int currentTime, int TotalTime) {
        int index = (int) ((float) currentTime / (float) TotalTime * lrcTextLsit.size());
        scrollToIndex(index);
        for (int i = 0; i < lrcTextViewList.size(); i++) {
            if (i == index) {
                lrcTextViewList.get(i).setTextColor(Color.BLUE);
            } else {
                lrcTextViewList.get(i).setTextColor(Color.WHITE);
            }
        }
    }

    public void scrollToTime(int currentTime) {
        if (currentTime == 0)
            scrollTo(0, 0);
        else {
            int sum = 0;
            int index = 0;
            for (index = 0; index < lrcTextLsit.size(); index++) {
                if (lrcTextLsit.get(index).getTime() >= currentTime) break;
            }
            Log.d("sum", String.valueOf(sum));
            Log.d("currentTime", String.valueOf(currentTime));
            scrollToIndex(index);
            Log.d("lrcSize", String.valueOf(lrcTextViewList.size()));
            for (int i = 0; i < lrcTextViewList.size(); i++) {
                lrcTextViewList.get(i).setTextColor(Color.WHITE);
            }
            if (index >= 1)
                lrcTextViewList.get(index - 1).setTextColor(Color.BLUE);
            else
                lrcTextViewList.get(0).setTextColor(Color.BLUE);
        }
    }

    public void scrollToIndex(int index) {
        Log.d("index", String.valueOf(index));
        int sum = 0;
        for (int i = 0; i < index; i++) {
            sum += heights.get(i);
        }
        scrollTo(0, sum);
    }

    public int getIndex(int height) {
        int sum = 0;
        int i = 0;
        while (sum <= height && i < heights.size()) {
            sum += heights.get(i);
            i++;
        }
        return i;
    }

    //得到当前播放百分比
    public float getCurrentPercent(int height) {
        return (float) getIndex(height) / (float) heights.size();
    }

   public String getCurrentLrc(int currentTime)
   {
       int i=0;
       for(i=0;i<lrcTextLsit.size();i++)
       {
           if(lrcTextLsit.get(i).getTime()>=currentTime)
               break;
       }
       if(i>=1)
           return lrcTextLsit.get(i-1).getLrc();
       else
           return "";
   }
}
