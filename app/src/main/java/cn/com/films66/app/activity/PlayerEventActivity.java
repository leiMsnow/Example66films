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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.opendanmaku.DanmakuItem;
import com.opendanmaku.DanmakuView;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.widget.BaseDialog;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.adapter.DanmakuAdapter;
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
    @Bind(R.id.rv_danmaku)
    ListView mDanmakuView;

    private MyHandler myHandler;

    private DanmakuAdapter danmakuAdapter;

    private boolean isPause = false;

    private static final int GET_CURRENT_DANMAKU = 1000;

    private List<MyDanmaku> myDanmakuList;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_player_event;
    }

    @Override
    protected void initData() {
        toolbarHide();
        danmakuAdapter = DanmakuAdapter.createAdapter(mContext);
        mDanmakuView.setAdapter(danmakuAdapter);
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
        myHandler.removeMessages(GET_CURRENT_DANMAKU);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            videoView.seekTo(videoView.getCurrentPosition());
            videoView.start();
            isPause = false;
            myHandler.sendEmptyMessageDelayed(GET_CURRENT_DANMAKU, 1000);
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
                if (msg.what == 100) {
                    weakObj.topView.setVisibility(View.GONE);
                } else if (msg.what == GET_CURRENT_DANMAKU) {
                    weakObj.addDanmaku();
                }
            }
        }
    }

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
        BaseApi.request(BaseApi.createApi(IServiceApi.class).getFilmDanmaku(mEvents.id),
                new BaseApi.IResponseListener<List<MyDanmaku>>() {
                    @Override
                    public void onSuccess(List<MyDanmaku> data) {
                        myDanmakuList = data;
                        addDanmaku();
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }

    private void addDanmaku() {
        MyDanmaku myDanmaku = null;
        for (int i = 0; i < myDanmakuList.size(); i++) {
            myDanmaku = myDanmakuList.get(i);
            LogUtils.d(this.getClass().getName(), "Math.abs: " +
                    Math.abs(myDanmaku.time - mediaController.getCurrentTime()));
            if (Math.abs(myDanmaku.time - mediaController.getCurrentTime()) <= 1) {
                danmakuAdapter.add(myDanmaku);
                break;
            }
        }
        if (myDanmaku != null) {
            myDanmakuList.remove(myDanmaku);
        }
        myHandler.sendEmptyMessageDelayed(GET_CURRENT_DANMAKU, 1000);
    }
}
