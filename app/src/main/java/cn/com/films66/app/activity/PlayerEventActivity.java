package cn.com.films66.app.activity;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.widget.BaseDialog;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import cn.com.films66.app.R;
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

    MyHandler myHandler;

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
                builder.setMessage("是否退出观看？");
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
//                if (mEvents != null && !TextUtils.isEmpty(mEvents.resources_url)) {
//                    Intent intent = new Intent(mContext, FloatWindowService.class);
//                    intent.putExtra(Constants.KEY_VIDEO_URL, mEvents.resources_url);
//                    startService(intent);
                onBackPressed();
//                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoView.setMediaController(mediaController);
        setPlayUrl();
        myHandler = new MyHandler(this);
        mediaController.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    topView.setVisibility(View.VISIBLE);
                    myHandler.sendEmptyMessageDelayed(100, 3000);
                }
                return false;
            }
        });
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

    private void setPlayUrl() {
        if (mEvents == null) {
            return;
        }
        String url = VideoUtils.getLocalURL(mEvents.resources_url);
        videoView.setVideoPath(url);
        LogUtils.d(PlayerEventActivity.class.getName(), "视频本地地址： " + url);
        int seek = mOffset - mEvents.getStartTime();
        LogUtils.d(PlayerEventActivity.class.getName(), "当前识别时间： " + mOffset);
        LogUtils.d(PlayerEventActivity.class.getName(), "播放开始时间： " + mEvents.getStartTime());
        if (seek > 0) {
            LogUtils.d(PlayerEventActivity.class.getName(), "调整时间： " + seek);
            videoView.seekTo(seek);
        }
        videoView.start();
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
    }
}
