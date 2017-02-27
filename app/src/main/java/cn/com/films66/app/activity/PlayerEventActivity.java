package cn.com.films66.app.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import butterknife.Bind;
import cn.com.films66.app.R;

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
        videoView.setMediaController(mediaController);
        if (mEvents != null) {
//            videoView.setVideoPath(mEvents.resources_url);
            videoView.setVideoPath("http://video.jiecao.fm/11/23/xin/%E5%81%87%E4%BA%BA.mp4");
            videoView.start();
            mediaController.setBackListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, FloatWindowService.class);
//                    intent.putExtra(Constants.KEY_VIDEO_URL, mEvents.resources_url);
//                    startService(intent);
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
}
