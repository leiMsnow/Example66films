package cn.com.films66.app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.model.FilmEventsEntity;
import cn.com.films66.app.utils.Constants;

/**
 * Created by Azure on 2017/2/26.
 */

public abstract class AbsEventActivity extends AppBaseActivity {

    private MyHandler myHandler;
    protected FilmEventsEntity mEvents;

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
        if (getIntent() == null)
            return;

        mEvents = getIntent().getParcelableExtra(Constants.KEY_EVENT_INFO);
        myHandler = new MyHandler(this);
        if (mEvents != null) {
            if (mEvents.getEndTime() == 0) {
                return;
            }
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    myHandler.sendEmptyMessage(0);
                }
            }, mEvents.getEndTime() - mEvents.getStartTime());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler = null;
    }
}
