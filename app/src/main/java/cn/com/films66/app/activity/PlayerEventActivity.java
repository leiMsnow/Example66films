package cn.com.films66.app.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.shuyu.core.proxy.HttpGetProxy;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.SDCardUtils;

import java.io.File;
import java.net.URLDecoder;

import butterknife.Bind;
import cn.com.films66.app.R;

public class PlayerEventActivity extends AbsEventActivity {

    private static final String TAG = PlayerEventActivity.class.getName();

    @Bind(R.id.video_view)
    VideoView videoView;
    //    @Bind(R.id.media_controller)
//    UniversalMediaController mediaController;
    @Bind(R.id.tv_complete)
    TextView tvComplete;
    @Bind(R.id.iv_scale)
    ImageView ivScale;

    private HttpGetProxy proxy;
    private long startTimeMills;
    private static final int PRE_BUFFER_SIZE = 4 * 1024 * 1024;

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
                onBackPressed();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPlayUrl();
    }

    private void setPlayUrl() {
//        if (mEvents != null) {
        String url = "http://video.jiecao.fm/11/23/xin/%E5%81%87%E4%BA%BA.mp4";//getResources_url(mEvents.resources_url);
        LogUtils.d(PlayerEventActivity.class.getName(), url);
        // 初始化VideoView
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtils.d(PlayerEventActivity.class.getName(), "当前识别时间： " + mOffset);
//            int seek = mOffset - mEvents.getStartTime();
//            LogUtils.d(PlayerEventActivity.class.getName(), "播放开始时间： " + mEvents.getStartTime());
//            if (seek > 0) {
//                LogUtils.d(PlayerEventActivity.class.getName(), "调整时间： " + seek);
//                videoView.seekTo(seek);
//            }
                videoView.start();
                long duration = System.currentTimeMillis() - startTimeMills;
                LogUtils.e(TAG, "首次缓冲时间:" + duration);
            }
        });

        // 初始化代理服务器
        proxy = new HttpGetProxy(PRE_BUFFER_SIZE, 10);
        try {
            proxy.startDownload(url, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startTimeMills = System.currentTimeMillis();
        String proxyUrl = proxy.getLocalURL(proxy.getId());
        videoView.setVideoPath(proxyUrl);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtils.d(PlayerEventActivity.class.getName(), "播放完成，关闭");
                finish();
            }
        });
//        }
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
