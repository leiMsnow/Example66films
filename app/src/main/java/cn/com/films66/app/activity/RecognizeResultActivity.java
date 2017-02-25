package cn.com.films66.app.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.shuyu.core.uils.ImageShowUtils;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.fragment.PictureFragment;
import cn.com.films66.app.fragment.PlayerFragment;
import cn.com.films66.app.fragment.WebFragment;
import cn.com.films66.app.model.CustomFileEntity;
import cn.com.films66.app.model.FilmEntity;
import cn.com.films66.app.model.FilmEventsEntity;
import cn.com.films66.app.utils.Constants;

public class RecognizeResultActivity extends AppBaseActivity {

    @Bind(R.id.iv_location_card)
    ImageView ivLocationCard;
    @Bind(R.id.fl_container)
    FrameLayout flContainer;

    private CustomFileEntity mCustomFile;
    private int mCurrentEvent = 0;
    private int mCurrentLocation = 0;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_player;
    }

    @Override
    protected void initData() {
        mCustomFile = getIntent().getParcelableExtra(Constants.KEY_RECOGNIZE_RESULT);
        getFilmDetail();
    }

    private void getFilmDetail() {
        if (mCustomFile == null)
            return;

        BaseApi.request(BaseApi.createApi(IServiceApi.class)
                        .getFilmDetail(Integer.parseInt(mCustomFile.audio_id))
                , new BaseApi.IResponseListener<FilmEntity>() {
                    @Override
                    public void onSuccess(FilmEntity filmDetail) {
                        switchFragment(filmDetail);
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }

    private void switchFragment(FilmEntity filmDetail) {
        for (int i = mCurrentLocation; i < filmDetail.location_cards.size(); i++) {
            if (matchEvent(filmDetail.location_cards.get(i).getStartTime(), 0)) {
                mCurrentLocation = i + 1;
                ImageShowUtils.showImage(mContext
                        , filmDetail.location_cards.get(i).card_url, ivLocationCard);
                break;
            }
        }
        for (int i = mCurrentEvent; i < filmDetail.events.size(); i++) {
            FilmEventsEntity event = filmDetail.events.get(i);
            if (matchEvent(event.getStartTime(), 1500)) {
                mCurrentEvent = i + 1;
                Fragment fragment = getFragment(event.type);
                if (fragment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.KEY_EVENT_INFO, event);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fl_container, fragment)
                            .commitAllowingStateLoss();
                    flContainer.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    private boolean matchEvent(int time, int offset) {
        return time != -1 && mCustomFile.play_offset_ms - time >= offset;
    }

    private Fragment getFragment(int type) {
        switch (type) {
            case FilmEventsEntity.TYPE_FILM:
                return new PlayerFragment();
            case FilmEventsEntity.TYPE_PICTURE:
                return new PictureFragment();
            case FilmEventsEntity.TYPE_WEB:
                return new WebFragment();
        }
        return null;
    }
}
