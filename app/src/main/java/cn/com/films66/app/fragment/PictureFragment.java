package cn.com.films66.app.fragment;

import android.widget.ImageView;

import com.shuyu.core.uils.ImageShowUtils;

import butterknife.Bind;
import cn.com.films66.app.R;

/**
 * Created by zhangleilei on 2017/2/25.
 */

public class PictureFragment extends EventFragment {

    @Bind(R.id.iv_picture)
    ImageView ivPicture;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_picture;
    }

    @Override
    protected void initData() {
        if (mEvents != null)
            ImageShowUtils.showImage(mContext, mEvents.resources_url, ivPicture);
    }
}
