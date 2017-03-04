package cn.com.films66.app.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.shuyu.core.widget.ChangeColorView;
import com.shuyu.core.widget.WhewView;

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
import cn.com.films66.app.utils.AssetsCopyToSDCard;
import cn.com.films66.app.utils.Constants;

public class MainActivity extends AbsRecognizeActivity {

    @Bind(R.id.ccv_main)
    ChangeColorView ccvMain;
    @Bind(R.id.ccv_me)
    ChangeColorView ccvMe;
    @Bind(R.id.iv_recognize)
    ImageView ivRecognize;
    @Bind(R.id.wv_view)
    WhewView whewView;

    private List<Fragment> mFragments = null;
    private List<ChangeColorView> mChangeColorViews = null;
    private int[] mTitles = {R.string.nav_main, R.string.nav_me};

    protected RecognizeService mRecognizeService;

    private String[] acrFiles = {
            "acrcloud/afp.df",
            "acrcloud/afp.iv",
            "acrcloud/afp.op"
    };

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        toolbarHide();
        initBottomMenu();
        initDefaultFragment();
        copyAssert();
        Intent intent = new Intent(mContext, RecognizeService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void copyAssert() {
        AssetsCopyToSDCard assetsCopyTOSDcard = new AssetsCopyToSDCard(getApplicationContext());
        for (String path : acrFiles) {
            assetsCopyTOSDcard.assetToSD(path,
                    Environment.getExternalStorageDirectory().toString()
                            + "/" + path);
        }
    }

    @OnClick(R.id.iv_recognize)
    public void onRecClick(View view) {
        if (mRecognizeService != null) {
            mRecognizeService.startRecognize();
        }
    }

    @Override
    protected void onResume() {
        if (mRecognizeService != null) {
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

    class OnButtonMenuClickListener implements View.OnClickListener {
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
//            mRecognizeService.startRecognize();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
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
        if (state) {
            whewView.start();
            ivRecognize.setEnabled(false);
        } else {
            whewView.stop();
            ivRecognize.setEnabled(true);
        }
    }

    @Override
    protected void onRecognizeResult(CustomFile customFile) {
        Intent intent = new Intent(mContext, RecognizeResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_RECOGNIZE_RESULT, customFile);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecognizeService != null)
            unbindService(serviceConnection);

        Intent intent = new Intent(mContext, FloatWindowService.class);
        intent.putExtra(Constants.KEY_FLOAT_WINDOW, true);
        startService(intent);
    }
}
