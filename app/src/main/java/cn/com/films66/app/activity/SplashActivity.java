package cn.com.films66.app.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;

public class SplashActivity extends AppBaseActivity {

    private static final int MESSAGE_GOTO_MAIN = 1;
    private static final int MESSAGE_COUNTDOWN = 0;

    TextView tvCountdown;

    private MyHandler mMyHandler;
    private Timer mTimer;
    private int mSecond = 3;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData() {
        toolbarHide();
        mMyHandler = new MyHandler(this);
        tvCountdown = (TextView) findViewById(R.id.tv_countdown);
        startCountdown();
    }

    private void startCountdown() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mSecond <= 0) {
                    Message message = new Message();
                    message.what = MESSAGE_GOTO_MAIN;
                    mMyHandler.sendMessage(message);
                    return;
                }
                Message message = new Message();
                message.what = MESSAGE_COUNTDOWN;
                message.arg1 = mSecond;
                mMyHandler.sendMessage(message);
                mSecond--;
            }
        }, 1000, 1000);
    }


    private static class MyHandler extends Handler {

        private WeakReference<SplashActivity> activityWeakReference;

        MyHandler(SplashActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            SplashActivity activity = activityWeakReference.get();
            if (activity != null) {
                if (msg.what == MESSAGE_GOTO_MAIN) {
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.finish();
                } else if (msg.what == MESSAGE_COUNTDOWN) {
                    activity.tvCountdown.setVisibility(View.VISIBLE);
                    activity.tvCountdown.setText(msg.arg1 + "s");
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
