package cn.com.films66.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import cn.com.films66.app.model.CustomFile;
import cn.com.films66.app.model.FilmEvents;
import cn.com.films66.app.utils.Constants;

/**
 * Created by Azure on 2017/2/26.
 */

public abstract class AbsEventActivity extends AbsRecognizeActivity {

    private MyHandler myHandler;
    protected FilmEvents mEvents;
    protected int mOffset = -1;

    private static class MyHandler extends Handler {
        private WeakReference<AbsEventActivity> weakReference;

        public MyHandler(AbsEventActivity weakObj) {
            weakReference = new WeakReference<>(weakObj);
        }

        @Override
        public void handleMessage(Message msg) {
            AbsEventActivity weakObj = weakReference.get();
            if (weakObj != null) {
                weakObj.finish();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent(getIntent());
    }

    private void initIntent(Intent intent) {
        if (intent == null)
            return;
        mEvents = intent.getParcelableExtra(Constants.KEY_EVENT_INFO);
        if (mEvents != null) {

            if (mEvents.getEndTime() == 0) {
                return;
            }

            if (myHandler == null) {
                myHandler = new MyHandler(this);
            }

            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (myHandler != null)
                        myHandler.sendEmptyMessage(0);
                }
            }, mEvents.getEndTime() - mEvents.getStartTime());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initIntent(intent);
    }

    @Override
    protected void onRecognizeState(boolean state) {

    }

    @Override
    protected void onRecognizeResult(CustomFile customFile) {
        mOffset = customFile.play_offset_ms;
        if (mEvents != null && mEvents.type != FilmEvents.TYPE_FILM) {
            if (mOffset >= mEvents.getEndTime() || mOffset < mEvents.getStartTime()) {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler = null;
    }
}
