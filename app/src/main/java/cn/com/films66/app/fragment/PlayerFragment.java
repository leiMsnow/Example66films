package cn.com.films66.app.fragment;

import android.media.MediaPlayer;

import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import butterknife.Bind;
import cn.com.films66.app.R;

/**
 * Created by zhangleilei on 2017/2/25.
 */

public class PlayerFragment extends EventFragment {

    @Bind(R.id.video_view)
    UniversalVideoView videoView;
    @Bind(R.id.media_controller)
    UniversalMediaController mediaController;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_player;
    }

    @Override
    protected void initData() {
        if (mEvents != null) {
            videoView.setMediaController(mediaController);
            videoView.setVideoPath(mEvents.resources_url);
//            videoView.setVideoPath("http://video.1udev.com/zzz07.mp4");
            videoView.requestFocus();
            videoView.start();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });
        }
    }
}
