package cn.com.films66.app.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.widget.BaseDialog;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.model.Film;
import cn.com.films66.app.model.MyDanmaku;
import cn.com.films66.app.utils.VideoUtils;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.Danmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.util.IOUtils;
import master.flame.danmaku.ui.widget.DanmakuView;

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
    private BaseDanmakuParser mParser;

    MyHandler myHandler;
    private boolean isPause = false;

    private DanmakuContext mDanmakuContext;
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
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
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
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
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
        initDanmaku();
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

    private void initDanmaku() {
        LogUtils.d(this.getClass().getName(), "initDanmaku");
        mDanmakuContext = DanmakuContext.create();
        // 设置最大行数,从右向左滚动(有其它方向可选)
        maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3);
        // 设置是否禁止重叠
        overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_LR, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);

        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3) //设置描边样式
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f) //是否启用合并重复弹幕
                .setScaleTextSize(1.2f) //设置弹幕滚动速度系数,只对滚动弹幕有效
                // 默认使用{@link SimpleTextCacheStuffer}只支持纯文字显示,
                // 如果需要图文混排请设置{@link SpannedCacheStuffer}
                // 如果需要定制其他样式请扩展{@link SimpleTextCacheStuffer}|{@link SpannedCacheStuffer}
                .setMaximumLines(maxLinesPair) //设置最大显示行数
                .preventOverlapping(overlappingEnablePair); //设置防弹幕重叠，null为允许重叠

        mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
            @Override
            public void updateTimer(DanmakuTimer timer) {
                LogUtils.d(this.getClass().getName(), "mDanmakuView - updateTimer");
            }

            @Override
            public void drawingFinished() {
                LogUtils.d(this.getClass().getName(), "mDanmakuView - drawingFinished");
            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {
                LogUtils.d(this.getClass().getName(), "mDanmakuView - danmakuShown");
            }

            @Override
            public void prepared() {
                LogUtils.d(this.getClass().getName(), "mDanmakuView - prepared");
                mDanmakuView.start();
            }
        });
        mDanmakuView.showFPS(false); //是否显示FPS
        mDanmakuView.enableDanmakuDrawingCache(true);
        getDanmakuInfo();
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

    /**
     * 添加文本弹幕
     */
    private void addDanmaku(List<MyDanmaku> data) {
        LogUtils.d(this.getClass().getName(), "addDanmaku");
        for (MyDanmaku myDanmaku : data) {
            BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
            if (danmaku == null) {
                break;
            }
            danmaku.text = myDanmaku.content;
            danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
            danmaku.padding = 5;
            //0 表示可能会被各种过滤器过滤并隐藏显示 //1 表示一定会显示, 一般用于本机发送的弹幕
            danmaku.priority = 0;
            danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
            danmaku.textColor = Color.RED;
            danmaku.textShadowColor = Color.WHITE; //阴影/描边颜色
            danmaku.borderColor = Color.GREEN; //边框颜色，0表示无边框
            mDanmakuView.addDanmaku(danmaku);
            LogUtils.d(this.getClass().getName(), "addDanmaku：" + myDanmaku.content);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mDanmakuView.getConfig().setDanmakuMargin(20);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mDanmakuView.getConfig().setDanmakuMargin(40);
        }
    }
}
