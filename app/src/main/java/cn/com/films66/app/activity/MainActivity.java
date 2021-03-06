package cn.com.films66.app.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.shuyu.core.uils.ImageShowUtils;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.widget.ChangeColorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.films66.app.R;
import cn.com.films66.app.fragment.MainFragment;
import cn.com.films66.app.fragment.UserCenterFragment;
import cn.com.films66.app.model.CustomFile;
import cn.com.films66.app.service.FloatWindowService;
import cn.com.films66.app.service.RecognizeService;
import cn.com.films66.app.utils.Constants;

public class MainActivity extends AbsRecognizeActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Bind(R.id.ccv_main)
    ChangeColorView ccvMain;
    @Bind(R.id.ccv_me)
    ChangeColorView ccvMe;
    @Bind(R.id.iv_recognize)
    ImageView ivRecognize;
    @Bind(R.id.iv_rec_loading)
    ImageView ivRecLoading;

    private List<Fragment> mFragments = null;
    private List<ChangeColorView> mChangeColorViews = null;
    private int[] mTitles = {R.string.nav_main, R.string.nav_me};

    protected RecognizeService mRecognizeService;
    private boolean mRecognizeState = false;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        toolbarHide();
        initBottomMenu();
        initDefaultFragment();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

        } else {
            startRecognize();
        }
    }

    private void startRecognize() {
        ImageShowUtils.showImage(mContext, R.drawable.eye_rotation, ivRecLoading);
        Intent intent = new Intent(mContext, RecognizeService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecognize();
            } else {
                Toast.makeText(MainActivity.this, "获取录音权限失败！", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick(R.id.iv_recognize)
    public void onRecClick(View view) {
        if (mRecognizeService != null) {
            mRecognizeState = !mRecognizeState;
            setRecognizeState();
        }
    }

    private void setRecognizeState() {
        if (mRecognizeState) {
            ivRecLoading.setVisibility(View.VISIBLE);
            mRecognizeService.startRecognize();
        } else {
            ivRecLoading.setVisibility(View.GONE);
            mRecognizeService.cancelRecognize();
        }
    }

    @Override
    protected void onResume() {
        if (mRecognizeService != null && !mRecognizeState) {
            mRecognizeService.setLoop(false);
        }
        super.onResume();
    }

    private void initDefaultFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(MainFragment.newInstance());
        mFragments.add(UserCenterFragment.newInstance());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            ft.add(R.id.fl_container, mFragments.get(i));
            if (i > 0) ft.hide(mFragments.get(i));
        }
        ft.commit();
    }

    private void initBottomMenu() {
        mChangeColorViews = new ArrayList<>();
        mChangeColorViews.add(ccvMain);
        mChangeColorViews.add(ccvMe);
        for (int i = 0; i < mChangeColorViews.size(); i++) {
            mChangeColorViews.get(i).setOnClickListener(new OnButtonMenuClickListener(i));
        }
        mChangeColorViews.get(0).setIconAlpha(1.0f);
    }

    private class OnButtonMenuClickListener implements View.OnClickListener {
        int position;

        OnButtonMenuClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            setBottomMenu(position);
        }
    }

    private void setBottomMenu(int position) {
        if (mChangeColorViews != null) {
            for (int i = 0; i < mChangeColorViews.size(); i++) {
                mChangeColorViews.get(i).setIconAlpha(0);
            }
            setTitle(mTitles[position]);
            mChangeColorViews.get(position).setIconAlpha(1.0f);
            switchContent(position);
        }
    }

    public void switchContent(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            ft.hide(mFragments.get(i));
        }
        ft.show(mFragments.get(position));
        ft.commit();
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRecognizeService = ((RecognizeService.RecognizeBinder) service).getService();
            mRecognizeState = true;
            setRecognizeState();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.d(MainActivity.class.getName(), "cancelRecognize");
            mRecognizeService.cancelRecognize();
            mRecognizeService = null;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterReceiver();
    }

    @Override
    protected void onRecognizeState(boolean state) {
        mRecognizeState = state;
        setRecognizeState();
    }

    @Override
    protected void onRecognizeResult(CustomFile customFile) {
        if (customFile == null) {
            onRecognizeState(false);
            return;
        }
        Intent intent = new Intent(mContext, RecognizeResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_RECOGNIZE_RESULT, customFile);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void openPlayer() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecognizeService != null)
            unbindService(serviceConnection);

        // 关闭浮窗
        Intent intent = new Intent(mContext, FloatWindowService.class);
        intent.putExtra(Constants.KEY_FLOAT_WINDOW, true);
        startService(intent);
    }
}
