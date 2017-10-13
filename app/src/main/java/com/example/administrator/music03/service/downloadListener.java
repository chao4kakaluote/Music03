package com.example.administrator.music03.service;

/**
 * Created by chao4kakaluote on 2017/10/5.
 */

public interface downloadListener
{
    public void onSucceed();
    public void onFailed();
    public void onCanceled();
    public void onPaused();
    public void onProgress(int progress);
}
