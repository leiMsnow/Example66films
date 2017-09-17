package cn.com.films66.app.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.opendanmaku.DanmakuItem;
import com.opendanmaku.DanmakuView;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.widget.BaseDialog;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.model.MyDanmaku;
import cn.com.films66.app.utils.VideoUtils;

public class PlayerEventActivity extends AbsEventActivity {

    @Bind(R.id.video_view)
    UniversalVideoView videoView;
    @Bind(R.id.media_controller)
    UniversalMediaController mediaController;
    @Bind(R.id.tv_complete)
    TextView tvComplete;
    @Bind(R.id.iv_scale)
    ImageView ivScale;
    @Bind(R.id.rl_top)
    View topView;
    @Bind(R.id.view_danmaku)
    DanmakuView mDanmakuView;

    MyHandler myHandler;
    private boolean isPause = false;

    private HashMap<Integer, Integer> maxLinesPair;// 弹幕最大行数
    private HashMap<Integer, Boolean> overlappingEnablePair;// 设置是否重叠

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_player_event;
    }

    @Override
    protected void initData() {
        toolbarHide();
        topView.setVisibility(View.GONE);
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDialog.Builder builder = new BaseDialog.Builder(mContext);
                builder.setMessage("退出后将无法继续播放此视频。是否退出？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                }).show();
            }
        });

        ivScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
        isPause = true;
        if (mDanmakuView != null && (!mDanmakuView.isPaused())) {
            //hide and pause playing:
            mDanmakuView.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            videoView.seekTo(videoView.getCurrentPosition());
            videoView.start();
            isPause = false;
        }
        if (mDanmakuView != null && (mDanmakuView.isPaused())) {
            //hide and pause playing:
            mDanmakuView.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDanmakuView != null && (!mDanmakuView.isPaused())) {
            //hide and pause playing:
            mDanmakuView.hide();
            mDanmakuView.clear();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoView.setMediaController(mediaController);
        setPlayUrl();
        myHandler = new MyHandler(this);
    }

    private static class MyHandler extends Handler {
        private WeakReference<PlayerEventActivity> weakReference;

        MyHandler(PlayerEventActivity weakObj) {
            weakReference = new WeakReference<>(weakObj);
        }

        @Override
        public void handleMessage(Message msg) {
            PlayerEventActivity weakObj = weakReference.get();
            if (weakObj != null) {
                weakObj.topView.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPlayUrl() {
        if (mEvents == null) {
            LogUtils.d(PlayerEventActivity.class.getName(), "没有视频资源");
            return;
        }
        String url = VideoUtils.getLocalURL(mEvents.resources_url);
        videoView.setVideoPath(url);
        LogUtils.d(PlayerEventActivity.class.getName(), "视频网络地址： " + mEvents.resources_url);
        LogUtils.d(PlayerEventActivity.class.getName(), "视频本地地址： " + url);
        int seek = mOffset - mEvents.getStartTime();
        LogUtils.d(PlayerEventActivity.class.getName(), "当前识别时间： " + mOffset);
        LogUtils.d(PlayerEventActivity.class.getName(), "播放开始时间： " + mEvents.getStartTime());
        if (seek > 0) {
            LogUtils.d(PlayerEventActivity.class.getName(), "调整时间： " + seek);
            videoView.seekTo(seek);
        }
        videoView.start();
        getDanmakuInfo();
        mediaController.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtils.d(PlayerEventActivity.class.getName(), "播放完成，关闭");
                finish();
            }
        });
        mediaController.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    topView.setVisibility(View.VISIBLE);
                    myHandler.sendEmptyMessageDelayed(100, 2000);
                }
                return false;
            }
        });
    }

    private void getDanmakuInfo() {
        LogUtils.d(this.getClass().getName(), "getDanmakuInfo");
        BaseApi.request(BaseApi.createApi(IServiceApi.class).getFilmDanmaku(mEvents.id),
                new BaseApi.IResponseListener<List<MyDanmaku>>() {
                    @Override
                    public void onSuccess(List<MyDanmaku> data) {
                        addDanmaku(data);
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }

    private void addDanmaku(List<MyDanmaku> data) {
        for (MyDanmaku myDanmaku : data) {
            mDanmakuView.addItem(new DanmakuItem(this, myDanmaku.content,
                    mDanmakuView.getWidth()));
            //show danmaku and play animation:
            mDanmakuView.show();
        }
    }
}
