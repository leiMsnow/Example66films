package cn.com.films66.app.activity;

import butterknife.Bind;
import cn.com.films66.app.R;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class PlayerEventActivity extends AbsEventActivity {

    @Bind(R.id.video_view)
    fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard videoView;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_player_event;
    }

    @Override
    protected void initData() {
        setTitle("视频");
        videoView.setUp("http://video.1udev.com/zzz07.mp4", JCVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
        if (mEvents != null) {
//            videoView.setUp(mEvents.resources_url, JCVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}
