package cn.com.films66.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.WindowManager;

import com.shuyu.core.uils.LogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import cn.com.films66.app.model.CustomFile;
import cn.com.films66.app.model.FilmEvents;
import cn.com.films66.app.utils.Constants;

/**
 * Created by Azure on 2017/2/26.
 */

public abstract class AbsEventActivity extends AppBaseActivity {

    private MyHandler myHandler;
    protected FilmEvents mEvents;
    protected long mOffset = 0;
    private CustomFile mCustomFile;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initIntent(getIntent());
    }

    private void initIntent(Intent intent) {
        if (intent == null) {
            finish();
            return;
        }
        mEvents = intent.getParcelableExtra(Constants.KEY_EVENT_INFO);
        mOffset = intent.getLongExtra(Constants.KEY_RECOGNIZE_OFFSET, 0L);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecognizeResult(CustomFile customFile) {
        if (TextUtils.isEmpty(customFile.audio_id)) {
            finish();
            return;
        }
        if (mCustomFile == null) {
            mCustomFile = customFile;
        } else if (!mCustomFile.audio_id.equals(customFile.audio_id)) {
            finish();
            return;
        }
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
        LogUtils.d("mCurrentEvent", "onBackPressed");
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
