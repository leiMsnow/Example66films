package cn.com.films66.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.SDCardUtils;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.File;
import java.net.URLDecoder;

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
            String url = getResources_url(mEvents.resources_url);
            LogUtils.d(PlayerEventActivity.class.getName(), url);
            videoView.setVideoPath(url);
            videoView.start();
            int seek = mOffset - mEvents.getStartTime();
            LogUtils.d(PlayerEventActivity.class.getName(), "当前识别时间： " + mOffset);
            LogUtils.d(PlayerEventActivity.class.getName(), "播放开始时间： " + mEvents.getStartTime());
            if (seek > 0) {
                LogUtils.d(PlayerEventActivity.class.getName(), "调整时间： " + seek);
                videoView.seekTo(seek);
            }
            mediaController.setBackListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
//            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    LogUtils.d(PlayerEventActivity.class.getName(), "播放完成，关闭");
//                    finish();
//                }
//            });
        }
    }

    private String getResources_url(String resources_url) {
        if (!TextUtils.isEmpty(resources_url)) {
            int lastSplit = resources_url.lastIndexOf("/");
            if (lastSplit != -1) {
                String localUrl = SDCardUtils.getSDCardPath() + "midea/" +
                        URLDecoder.decode(resources_url.substring(lastSplit + 1));
                File file = new File(localUrl);
                return file.exists() ? localUrl : resources_url;
            }
        }
        return resources_url;
    }
}
