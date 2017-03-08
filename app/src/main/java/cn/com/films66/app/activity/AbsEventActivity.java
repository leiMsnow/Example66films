package cn.com.films66.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.shuyu.core.uils.LogUtils;

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
    protected int mOffset = 0;

    private static class MyHandler extends Handler {
        private WeakReference<AbsEventActivity> weakReference;

        public MyHandler(AbsEventActivity weakObj) {
            weakReference = new WeakReference<>(weakObj);
        }

        @Override
        public void handleMessage(Message msg) {
            AbsEventActivity weakObj = weakReference.get();
            if (weakObj != null && !weakObj.canFinish()) {
                sendEmptyMessageDelayed(0, 1000);
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
        mOffset = intent.getIntExtra(Constants.KEY_RECOGNIZE_OFFSET, 0);
        if (mEvents != null) {
            if (myHandler == null) {
                myHandler = new MyHandler(this);
                myHandler.sendEmptyMessageDelayed(0, 200);
            }
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
        if (customFile == null)
            return;
        mOffset = customFile.play_offset_ms;
        canFinish();
    }

    private boolean canFinish() {
        if (mEvents != null && mEvents.getEndTime() > 0) {
            if (mOffset >= mEvents.getEndTime() || mOffset < mEvents.getStartTime()) {
                LogUtils.d(AbsEventActivity.class.getName(), "需要关闭当前event：" + mEvents);
                finish();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_EVENT_CANCEL, true);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler = null;
    }
}
