package cn.com.films66.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.service.FloatWindowService;
import cn.com.films66.app.utils.Constants;

public class PlayerEventActivity extends AbsEventActivity {


    @Bind(R.id.video_view)
    UniversalVideoView videoView;
    @Bind(R.id.media_controller)
    UniversalMediaController mediaController;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_player_event;
    }

    @Override
    protected void initData() {
        toolbarHide();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("视频");
        videoView.setMediaController(mediaController);
        videoView.setVideoPath("http://video.1udev.com/zzz07.mp4");
        videoView.start();

        mediaController.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FloatWindowService.class);
                intent.putExtra(Constants.KEY_VIDEO_URL, "http://video.1udev.com/zzz07.mp4");
                startService(intent);
                finish();
            }
        });

        if (mEvents != null) {

        }
    }

    @Override
    protected boolean setOrientationPortrait() {
        return false;
    }
}
