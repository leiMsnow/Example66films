package cn.com.films66.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.model.CustomFile;
import cn.com.films66.app.model.EventBusModel;
import cn.com.films66.app.service.FloatWindowService;
import cn.com.films66.app.service.RecognizeService;
import cn.com.films66.app.utils.Constants;
import zhy.com.highlight.HighLight;
import zhy.com.highlight.interfaces.HighLightInterface;
import zhy.com.highlight.position.OnBottomPosCallback;
import zhy.com.highlight.position.OnRightPosCallback;
import zhy.com.highlight.shape.CircleLightShape;

public class MainActivity extends AppBaseActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Bind(R.id.iv_progress)
    ImageView ivProgress;
    @Bind(R.id.iv_play)
    ImageView ivPlay;
    private boolean mRecognizeState = false;
    HighLight mHightLight;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        toolbarHide();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            startRecognize();
        }
    }

    private void startRecognize() {
        Intent intent = new Intent(mContext, RecognizeService.class);
        startService(intent);
        mRecognizeState = !mRecognizeState;
        setRecognizeState();
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

    @OnClick(R.id.iv_play)
    public void onRecClick(View view) {
        mRecognizeState = !mRecognizeState;
        setRecognizeState();
    }

    @OnClick(R.id.iv_user)
    public void openUserInfo(View view) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra("isRecognize", mRecognizeState);
        startActivity(intent);
    }

    @OnClick(R.id.iv_recommend)
    public void openRecommend(View view) {
        Intent intent = new Intent(mContext, RecommendActivity.class);
        startActivity(intent);
    }

    private void setRecognizeState() {
        EventBus.getDefault().post(new EventBusModel.ControlRecognize(mRecognizeState));
        if (mRecognizeState) {
            ivProgress.setVisibility(View.GONE);
            ivPlay.setImageResource(R.mipmap.ic_pause);
        } else {
            ivProgress.setVisibility(View.VISIBLE);
            ivPlay.setImageResource(R.mipmap.ic_play);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNextTipViewOnCreated();
        EventBus.getDefault().post(new EventBusModel.ControlRecognizeLoop(false));
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecognizeState(EventBusModel.RecognizeState state) {
        LogUtils.d(this.getClass().getName(), "收到识别状态：" + state.recognizeState);
        mRecognizeState = state.recognizeState;
        setRecognizeState();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecognizeResult(CustomFile customFile) {
        if (TextUtils.isEmpty(customFile.audio_id)) {
            mRecognizeState = false;
            setRecognizeState();
            return;
        }
        Intent intent = new Intent(mContext, RecognizeResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_RECOGNIZE_RESULT, customFile);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭浮窗
        Intent intent = new Intent(mContext, FloatWindowService.class);
        intent.putExtra(Constants.KEY_FLOAT_WINDOW, true);
        startService(intent);
    }

    public  void showNextTipViewOnCreated(){
        if((boolean)SPUtils.get(mContext,"HighLight",true)) {
            SPUtils.put(mContext,"HighLight",false);
            mHightLight = new HighLight(MainActivity.this)//
                    .autoRemove(false)
                    .enableNext()
                    .setOnLayoutCallback(new HighLightInterface.OnLayoutCallback() {
                        @Override
                        public void onLayouted() {
                            //界面布局完成添加tipview
                            mHightLight.addHighLight(R.id.iv_progress, R.layout.info_high_light_1,
                                    new OnBottomPosCallback(), new CircleLightShape())

                                    .addHighLight(R.id.iv_user, R.layout.info_high_light_2,
                                            new OnRightPosCallback(), new CircleLightShape())

                                    .addHighLight(R.id.iv_recommend, R.layout.info_high_light_3,
                                            new OnBottomPosCallback(), new CircleLightShape());
                            //然后显示高亮布局
                            mHightLight.show();
                        }
                    })
                    .setClickCallback(new HighLight.OnClickCallback() {
                        @Override
                        public void onClick() {
                            mHightLight.next();
                        }
                    });
        }
    }
}
