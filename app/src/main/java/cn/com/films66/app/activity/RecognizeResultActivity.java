package cn.com.films66.app.activity;

import android.content.Intent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.shuyu.core.uils.ImageShowUtils;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.model.CustomFileEntity;
import cn.com.films66.app.model.FilmEntity;
import cn.com.films66.app.model.FilmEventsEntity;
import cn.com.films66.app.model.LocationCards;
import cn.com.films66.app.service.RecognizeService;
import cn.com.films66.app.utils.Constants;

public class RecognizeResultActivity extends AbsRecognizeActivity {

    @Bind(R.id.iv_location_card)
    ImageView ivLocationCard;

    private CustomFileEntity mCustomFile;
    private FilmEntity mFilmDetail;
    private int mCurrentEvent = 0;
    private int mCurrentLocation = 0;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_recognize_result;
    }

    @Override
    protected void initData() {
        setTitle("正在识别...");
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
        for (int i = mFilmDetail.location_cards.size() - 1; i >= mCurrentLocation; i--) {
            LocationCards locationCards = mFilmDetail.location_cards.get(i);
            if (matchCart(locationCards.getStartTime())) {
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
                Class eventActivity = getEventActivity(event.type);
                if (eventActivity != null) {
                    Intent intent = new Intent(mContext, eventActivity);
                    intent.putExtra(Constants.KEY_EVENT_INFO, event);
                    startActivity(intent);
                }
                break;
            }
        }
    }

    private boolean matchCart(int time) {
        return time != -1 && mCustomFile.play_offset_ms - time >= 0;
    }

    private boolean matchEvent(int time) {
        return time != -1 && Math.abs(mCustomFile.play_offset_ms - time) <= 1500;
    }

    private Class<? extends AbsEventActivity> getEventActivity(int type) {
        switch (type) {
            case FilmEventsEntity.TYPE_FILM:
                return PlayerEventActivity.class;
            case FilmEventsEntity.TYPE_PICTURE:
                return PictureEventActivity.class;
            case FilmEventsEntity.TYPE_WEB:
                return WebEventActivity.class;
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
