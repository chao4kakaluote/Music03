package com.example.administrator.music03.Activity;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.administrator.music03.R;
public class FingerAuthen extends AppCompatActivity
{
    private Button startAuthen;
    private final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS=1;
    FingerprintManager manager;
    KeyguardManager mKeyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_authen);
        startAuthen=(Button)findViewById(R.id.authen);
        manager=(FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        startAuthen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (isFinger()) {
//                    Toast.makeText(FingerAuthen.this, "请进行指纹识别", Toast.LENGTH_LONG).show();
//                    startListening(null);
                }
            }
        });
    }

    private void checkFingerPrint(){
        FingerprintManagerCompat.from(this).authenticate(null,0,null,new MyCallBack(),null);
    }
    public class MyCallBack extends FingerprintManagerCompat.AuthenticationCallback{
        private static final String TAG = "MyCallBack";
        @Override
        public void onAuthenticationFailed(){
            Toast.makeText(FingerAuthen.this,"识别失败",Toast.LENGTH_SHORT).show();
        }
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result){
           Intent intent=new Intent(FingerAuthen.this,MainActivity1.class);
            startActivity(intent);
        }
    }

    public boolean isFinger() {
        if (!manager.isHardwareDetected()) {
            Toast.makeText(this, "没有指纹识别模块", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mKeyManager.isKeyguardSecure()) {
            Toast.makeText(this, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!manager.hasEnrolledFingerprints()) {
            Toast.makeText(this, "没有录入指纹", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();
    //回调方法
    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
            Toast.makeText(FingerAuthen.this, errString, Toast.LENGTH_SHORT).show();
            showAuthenticationScreen();
        }
        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

            Toast.makeText(FingerAuthen.this, helpString, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Toast.makeText(FingerAuthen.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(FingerAuthen.this, "指纹识别失败", Toast.LENGTH_SHORT).show();
        }
    };
    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return;
        }
        manager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);

    }

    private void showAuthenticationScreen() {
        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "测试指纹识别");
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            // Challenge completed, proceed with using cipher
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "识别成功", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(FingerAuthen.this,MainActivity1.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "识别失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void Log(String tag, String msg) {
        Log.d(tag, msg);
    }
}
