package cn.com.films66.app.activity;

import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.core.uils.ImageShowUtils;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
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

    private int mFilmId;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_film_detail;
    }

    @Override
    protected void initData() {
        mFilmId = getIntent().getIntExtra(Constants.KEY_FILM_DETAIL_ID, 0);
        getFilmDetail();
    }

    private void getFilmDetail() {
        if (mFilmId == 0) {
            return;
        }

        BaseApi.request(BaseApi.createApi(IServiceApi.class).getFilmDetail(mFilmId)
                , new BaseApi.IResponseListener<FilmEntity>() {
                    @Override
                    public void onSuccess(FilmEntity filmDetail) {
                        setTitle(filmDetail.name);
                        ImageShowUtils.showImage(mContext, filmDetail.cover_url, ivVideoBg);
                        tvVideoTitle.setText(filmDetail.name);
                        tvVideoTime.setText("时长：" + filmDetail.getRuntime());
                        tvDesc.setText(filmDetail.introduction);
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }

}
