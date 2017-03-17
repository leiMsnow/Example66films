package cn.com.films66.app.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.core.uils.LogUtils;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import butterknife.Bind;
import cn.com.films66.app.R;

public class PlayerEventActivity extends AbsEventActivity {

    @Bind(R.id.video_view)
    UniversalVideoView videoView;
    @Bind(R.id.media_controller)
    UniversalMediaController mediaController;
    @Bind(R.id.tv_complete)
    TextView tvComplete;
    @Bind(R.id.iv_scale)
    ImageView ivScale;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_player_event;
    }

    @Override
    protected void initData() {
        toolbarHide();
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
    }

    private void setPlayUrl() {
        if (mEvents != null) {
//        String url = "http://film-server.b0.upaiyun.com/人物及档案卡/演示用mp4/03A.1.雾桥直播-Untitled%20MPEG-4.mp4";
            String url = getResources_url(mEvents.resources_url);
            LogUtils.d(PlayerEventActivity.class.getName(), url);
            int seek = mOffset - mEvents.getStartTime();
            LogUtils.d(PlayerEventActivity.class.getName(), "当前识别时间： " + mOffset);
            LogUtils.d(PlayerEventActivity.class.getName(), "播放开始时间： " + mEvents.getStartTime());
            seek = seek / 1000;
            if (seek > 0) {
                url += "start=" + seek;
                LogUtils.d(PlayerEventActivity.class.getName(), "调整时间： " + seek);
                videoView.seekTo(seek);
            }
            videoView.setVideoPath(url);
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

    private String getResources_url(String resources_url) {
//        if (!TextUtils.isEmpty(resources_url)) {
//            int lastSplit = resources_url.lastIndexOf("/");
//            if (lastSplit != -1) {
//                String localUrl = SDCardUtils.getSDCardPath() + "midea/" +
//                        URLDecoder.decode(resources_url.substring(lastSplit + 1));
//                File file = new File(localUrl);
//                return file.exists() ? localUrl : resources_url;
//            }
//        }
        return resources_url;
    }
}
