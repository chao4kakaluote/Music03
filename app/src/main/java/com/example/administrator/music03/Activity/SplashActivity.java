package com.example.administrator.music03.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.List;
import com.example.administrator.music03.R;

import static org.litepal.LitePalApplication.getContext;
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        else
        {
            try {
                Thread.sleep(2000);
                runApp();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case 1:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                        runApp();
                }
                else
                {
                    Toast.makeText(getContext(),"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
    public void runApp()
    {
        Intent intent=new Intent(this,Login.class);
        startActivity(intent);
    }

}
