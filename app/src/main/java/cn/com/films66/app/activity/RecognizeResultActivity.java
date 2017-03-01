package cn.com.films66.app.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;

import com.shuyu.core.uils.ImageShowUtils;

import java.lang.ref.WeakReference;

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

    private static final int CHANGE_EVENT = 0;

    private CustomFileEntity mCustomFile;
    private FilmEntity mFilmDetail;
    private MyHandler mHandler;
    private int mOffset = 0;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_recognize_result;
    }

    @Override
    protected void initData() {
        setTitle("");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCustomFile = getIntent().getParcelableExtra(Constants.KEY_RECOGNIZE_RESULT);
        getFilmDetail();

        mHandler = new MyHandler(this);

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
                        startSwitch();
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }

    private void startSwitch() {
        mHandler.sendEmptyMessageDelayed(CHANGE_EVENT, 1000);
        mOffset += 1000;
        switchCard();
        switchEvent();
    }

    private void switchCard() {
        for (int i = mFilmDetail.location_cards.size() - 1; i >= 0; i--) {
            LocationCards locationCards = mFilmDetail.location_cards.get(i);
            if (matchCart(locationCards.getStartTime())) {
                ImageShowUtils.showImage(mContext
                        , locationCards.card_url, ivLocationCard);
                break;
            }
        }
    }

    private void switchEvent() {
        for (int i = 0, count = mFilmDetail.events.size(); i < count; i++) {
            FilmEventsEntity event = mFilmDetail.events.get(i);
            if (matchEvent(event.getStartTime())) {
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

    private int getOffsetTime() {
        if (Math.abs(mCustomFile.play_offset_ms - mOffset) <= 500) {
            mOffset = mCustomFile.play_offset_ms;
        }
        return mOffset;
    }

    private boolean matchCart(int time) {
        return time != -1 && mOffset - time >= 0;
    }

    private boolean matchEvent(int time) {
        return time != -1 && Math.abs(mOffset - time) <= 500;
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
        if (mCustomFile == null || mCustomFile.audio_id.equals(customFile.audio_id)) {
            mCustomFile = customFile;
            getOffsetTime();
            getFilmDetail();
        } else {
            mCustomFile = customFile;
            getOffsetTime();
            startSwitch();
        }
    }

    private static class MyHandler extends Handler {

        private WeakReference<RecognizeResultActivity> weakReference;

        public MyHandler(RecognizeResultActivity weakObj) {
            weakReference = new WeakReference<>(weakObj);
        }

        @Override
        public void handleMessage(Message msg) {
            RecognizeResultActivity weakObj = weakReference.get();
            if (weakObj != null) {
                weakObj.startSwitch();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(CHANGE_EVENT);
        mHandler = null;
    }
}
