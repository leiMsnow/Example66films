package cn.com.films66.app.base;

import com.shuyu.core.BaseActivity;

import cn.com.films66.app.R;

/**
 * Created by zhangleilei on 9/10/16.
 */

public abstract class AppBaseActivity extends BaseActivity {
    @Override
    protected boolean hasToolbar() {
        return true;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        if (mToolbar != null) {
            mToolbar.setBackgroundResource(R.color.toolbar_main_color);
        }
    }
}
