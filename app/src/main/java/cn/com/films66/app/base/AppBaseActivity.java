package cn.com.films66.app.base;

import android.os.Bundle;

import com.shuyu.core.BaseActivity;
import com.shuyu.core.uils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.com.films66.app.R;
import cn.com.films66.app.activity.PlayerEventActivity;
import cn.com.films66.app.model.NoBodyEntity;

/**
 * Created by zhangleilei on 9/10/16.
 */

public abstract class AppBaseActivity extends BaseActivity {

    @Override
    protected boolean includeToolbar() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
            LogUtils.d(this.getClass().getName(), "register");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
            LogUtils.d(this.getClass().getName(), "unregister");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoBodyEntity event) {
        /* Do something */
        LogUtils.d(this.getClass().getName(), "onMessageEvent");
    }
}
