package cn.com.films66.app.activity;

import android.widget.ImageView;

import com.shuyu.core.uils.ImageShowUtils;

import butterknife.Bind;
import cn.com.films66.app.R;

public class PictureEventActivity extends AbsEventActivity {

    @Bind(R.id.iv_picture)
    ImageView ivPicture;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_pirture_event;
    }

    @Override
    protected void initData() {
        if (mEvents != null)
            ImageShowUtils.showImage(mContext, mEvents.resources_url, ivPicture);
    }
}
