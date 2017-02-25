package com.shuyu.core;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.shuyu.core.uils.DensityUtils;
import com.shuyu.core.uils.KeyBoardUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;

/**
 * Created by Azure on 2016/8/29.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected Toolbar mToolbar;
    private int mToolbarHeight = 48;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        // 使得音量键控制媒体声音
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // 设置竖屏
        if (setOrientationPortrait()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        initToolbar();
        initData();
    }

    protected boolean setOrientationPortrait() {
        return true;
    }

    protected abstract int getLayoutRes();

    protected abstract void initData();

    protected abstract boolean includeToolbar();

    @Override
    protected void onPause() {
        super.onPause();
        KeyBoardUtils.hideSoftKeyboard(this);
    }

    protected void initToolbar() {
        if (!includeToolbar()) return;

        View toolbarView = View.inflate(mContext, R.layout.include_toolbar, null);
        mToolbar = (Toolbar) toolbarView.findViewById(R.id.tb_toolbar);
        if (mToolbar == null) return;

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setOverflowShowingAlways();
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(mContext, mToolbarHeight));
        addContentView(toolbarView, layoutParams);
        setToolbarMarginTop(getToolbarMarginTop());
    }

    private void setToolbarMarginTop(int toolbarMarginTop) {
        FrameLayout.LayoutParams rootParams = (FrameLayout.LayoutParams) getRootView().getLayoutParams();
        rootParams.topMargin = toolbarMarginTop;
        getRootView().setLayoutParams(rootParams);
    }

    protected View getRootView() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    protected void setToolBarMarginTop(int toolbarMarginTop) {
        setToolbarMarginTop(toolbarMarginTop);
    }

    protected void toolbarHide() {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setToolBarMarginTop(0);
    }

    protected void hideToolbarBack() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    protected int getToolbarMarginTop() {
        return DensityUtils.dp2px(mContext, mToolbarHeight);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (Exception e) {
            }
        }
        return super.onPrepareOptionsMenu(menu);

    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
