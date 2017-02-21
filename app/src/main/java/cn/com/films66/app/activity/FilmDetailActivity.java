package cn.com.films66.app.activity;

import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.core.uils.ImageShowUtils;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.model.FilmEntity;
import cn.com.films66.app.utils.Constants;

/**
 * Created by zhangleilei on 21/02/2017.
 */

public class FilmDetailActivity extends AppBaseActivity {

    @Bind(R.id.iv_video_bg)
    ImageView ivVideoBg;
    @Bind(R.id.tv_video_title)
    TextView tvVideoTitle;
    @Bind(R.id.tv_video_time)
    TextView tvVideoTime;
    @Bind(R.id.tv_desc)
    TextView tvDesc;

    private FilmEntity mFilmsEntity;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_film_detail;
    }

    @Override
    protected void initData() {
        mFilmsEntity = getIntent().getParcelableExtra(Constants.KEY_FILM_DETAIL);
        getFilmDetail();
    }

    private void getFilmDetail() {
        if (mFilmsEntity == null)
            return;

        setTitle(mFilmsEntity.name);
        ImageShowUtils.showImage(mContext, mFilmsEntity.cover_url, ivVideoBg);
        tvVideoTitle.setText(mFilmsEntity.name);
        tvVideoTime.setText("时长：" + mFilmsEntity.runtime);
        tvDesc.setText(mFilmsEntity.introduction);
    }
}
