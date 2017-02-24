package cn.com.films66.app.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.shuyu.core.widget.ChangeColorView;
import com.shuyu.core.widget.WhewView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.fragment.MainFragment;
import cn.com.films66.app.fragment.UserCenterFragment;
import cn.com.films66.app.service.RecognizeService;

public class MainActivity extends AppBaseActivity {

    @Bind(R.id.ccv_main)
    ChangeColorView ccvMain;
    @Bind(R.id.ccv_me)
    ChangeColorView ccvMe;
    @Bind(R.id.wv_view)
    WhewView whewView;

    private List<Fragment> mFragments = null;
    private List<ChangeColorView> mChangeColorViews = null;
    private int[] mTitles = {R.string.nav_main, R.string.nav_me};
    private RecognizeService recognizeService;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        hideToolbarBack();
        initBottomMenu();
        initDefaultFragment();
        Intent intent = new Intent(mContext, RecognizeService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            recognizeService = ((RecognizeService.RecognizeBinder) service).getService();
            recognizeService.setRecognizeListener(recognizeListener);
            recognizeService.startRecognize();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            recognizeService.cancelRecognize();
            recognizeService = null;
        }
    };

    private RecognizeService.IRecognizeListener recognizeListener =
            new RecognizeService.IRecognizeListener() {
                @Override
                public void onRecognizeState(boolean processing) {
                    if (processing) {
                        whewView.start();
                    } else {
                        whewView.stop();
                    }
                }
            };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @OnClick(R.id.iv_recognize)
    public void onRecClick(View view) {
        recognizeService.startRecognize();
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
}
