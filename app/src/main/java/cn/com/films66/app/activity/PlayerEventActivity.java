package cn.com.films66.app.activity;

import android.content.Intent;
import android.media.MediaPlayer;
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

import java.net.URLDecoder;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.service.FloatWindowService;
import cn.com.films66.app.utils.Constants;

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
                finish();
            }
        });

        ivScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEvents != null && !TextUtils.isEmpty(mEvents.resources_url)) {
                    Intent intent = new Intent(mContext, FloatWindowService.class);
                    intent.putExtra(Constants.KEY_VIDEO_URL, mEvents.resources_url);
                    startService(intent);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoView.setMediaController(mediaController);
//        videoView.setVideoPath(getResources_url("http://film-server.b0.upaiyun.com/movie/%E7%96%AF%E5%AD%90a.mp4"));
//        videoView.start();
        if (mEvents != null) {
            videoView.setVideoPath(getResources_url(mEvents.resources_url));
            videoView.start();
            mediaController.setBackListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finish();
                }
            });
        }
    }

    private String getResources_url(String resources_url) {
        if (!TextUtils.isEmpty(resources_url)) {
            int lastSplit = resources_url.lastIndexOf("/");
            if (lastSplit != -1) {

                String localUrl = SDCardUtils.getSDCardPath() + "midea/" +
                        URLDecoder.decode(resources_url.substring(lastSplit + 1));
                LogUtils.d(PlayerEventActivity.class.getName(), localUrl);
                return localUrl;
            }
        }
        return resources_url;
    }
}
