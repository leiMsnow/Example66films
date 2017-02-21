package com.shuyu.core;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;

/**
 * Created by zhangleilei on 8/31/16.
 */
public class CoreApplication extends Application {

    private static CoreApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        FileDownloader.init(getApplicationContext());
    }

    public static CoreApplication getApplication() {
        return mApplication;
    }

}
