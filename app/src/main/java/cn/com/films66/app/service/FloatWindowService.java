package cn.com.films66.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import cn.com.films66.app.utils.Constants;
import cn.com.films66.app.utils.MyWindowManager;

public class FloatWindowService extends Service {

    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            boolean stop = intent.getBooleanExtra(Constants.KEY_FLOAT_WINDOW, false);
            if (stop) {
                stopSelf();
            }
            final String url = intent.getStringExtra(Constants.KEY_VIDEO_URL);
            if (!TextUtils.isEmpty(url) && !MyWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.createSmallWindow(getApplicationContext(), url);
                    }
                });
            } else {
                stopSelf();
            }

        } else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MyWindowManager.isWindowShowing()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyWindowManager.removeSmallWindow(getApplicationContext());
                }
            });
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}  
