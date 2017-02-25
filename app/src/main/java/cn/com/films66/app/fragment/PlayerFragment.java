package cn.com.films66.app.fragment;

import com.shuyu.core.BaseFragment;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import butterknife.Bind;
import cn.com.films66.app.R;

/**
 * Created by zhangleilei on 2017/2/25.
 */

public class PlayerFragment extends BaseFragment {

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

    }
}
