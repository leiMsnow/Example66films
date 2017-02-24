package com.shuyu.core;

import android.app.Application;

import com.chenenyu.router.Router;
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
        Router.initialize(this);
        FileDownloader.init(getApplicationContext());
    }
    public static CoreApplication getApplication() {
        return mApplication;
    }
}
