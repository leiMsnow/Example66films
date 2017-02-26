package cn.com.films66.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.shuyu.core.uils.ImageShowUtils;
import com.shuyu.core.uils.LogUtils;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.fragment.EventFragment;
import cn.com.films66.app.fragment.PictureFragment;
import cn.com.films66.app.fragment.PlayerFragment;
import cn.com.films66.app.fragment.WebFragment;
import cn.com.films66.app.model.CustomFileEntity;
import cn.com.films66.app.model.FilmEntity;
import cn.com.films66.app.model.FilmEventsEntity;
import cn.com.films66.app.model.LocationCards;
import cn.com.films66.app.service.RecognizeService;
import cn.com.films66.app.utils.Constants;

public class RecognizeResultActivity extends AbsRecognizeActivity {

    @Bind(R.id.iv_location_card)
    ImageView ivLocationCard;
    @Bind(R.id.fl_container)
    FrameLayout flContainer;

    private CustomFileEntity mCustomFile;
    private FilmEntity mFilmDetail;
    private int mCurrentEvent = 0;
    private int mCurrentLocation = 0;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_player;
    }

    @Override
    protected void initData() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCustomFile = getIntent().getParcelableExtra(Constants.KEY_RECOGNIZE_RESULT);
        getFilmDetail();

        Intent intent = new Intent(mContext, RecognizeService.class);
        intent.putExtra(Constants.KEY_RECOGNIZE_LOOP, true);
        startService(intent);
    }

    private void getFilmDetail() {
        if (mCustomFile == null)
            return;
        BaseApi.request(BaseApi.createApi(IServiceApi.class)
                        .getFilmDetail(Integer.parseInt(mCustomFile.audio_id))
                , new BaseApi.IResponseListener<FilmEntity>() {
                    @Override
                    public void onSuccess(FilmEntity filmDetail) {
                        mFilmDetail = filmDetail;
                        switchFragment();
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }

    private void switchFragment() {
        for (int i = mCurrentLocation, count = mFilmDetail.location_cards.size(); i < count; i++) {
            LocationCards locationCards = mFilmDetail.location_cards.get(i);
            if (matchEvent(locationCards.getStartTime())) {
//                mCurrentLocation = i + 1;
                ImageShowUtils.showImage(mContext
                        , locationCards.card_url, ivLocationCard);
                break;
            }
        }
        for (int i = mCurrentEvent, count = mFilmDetail.events.size(); i < count; i++) {
            final FilmEventsEntity event = mFilmDetail.events.get(i);
            if (matchEvent(event.getStartTime())) {
//                mCurrentEvent = i + 1;
                EventFragment fragment = getFragment(event.type);
                if (fragment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.KEY_EVENT_INFO, event);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fl_container, fragment)
                            .commitAllowingStateLoss();
                    flContainer.setVisibility(View.VISIBLE);
                    fragment.setEventListener(new EventFragment.IEventListener() {
                        @Override
                        public void eventFinish() {
                            LogUtils.d(RecognizeResultActivity.class.getName(), "eventFinish: " + event.type);
                            flContainer.setVisibility(View.GONE);
                        }
                    });
                }
                break;
            }
        }
    }

    private boolean matchEvent(int time) {
        return time != -1 && Math.abs(mCustomFile.play_offset_ms - time) <= 500;
    }
    private EventFragment getFragment(int type) {
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

    @Override
    protected void onRecognizeState(boolean state) {

    }

    @Override
    protected void onRecognizeResult(CustomFileEntity customFile) {
        mCustomFile = customFile;
        switchFragment();
    }
}
