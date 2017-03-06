package cn.com.films66.app.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.shuyu.core.uils.DateUtils;
import com.shuyu.core.uils.ImageShowUtils;
import com.shuyu.core.uils.LogUtils;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.model.CustomFile;
import cn.com.films66.app.model.Film;
import cn.com.films66.app.model.FilmEvents;
import cn.com.films66.app.model.LocationCards;
import cn.com.films66.app.service.RecognizeService;
import cn.com.films66.app.utils.Constants;

public class RecognizeResultActivity extends AbsRecognizeActivity {

    @Bind(R.id.iv_location_card)
    ImageView ivLocationCard;

    private static final int CHANGE_EVENT = 0;

    private CustomFile mCustomFile;
    private Film mFilmDetail;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        }
        return true;
    }

    private void getFilmDetail() {
        if (mCustomFile == null)
            return;
        BaseApi.request(BaseApi.createApi(IServiceApi.class)
                        .getFilmDetail(Integer.parseInt(mCustomFile.audio_id))
                , new BaseApi.IResponseListener<Film>() {
                    @Override
                    public void onSuccess(Film filmDetail) {
                        mFilmDetail = filmDetail;
                        getOffsetTime();
                        mHandler.removeMessages(CHANGE_EVENT);
                        mHandler.sendEmptyMessageDelayed(CHANGE_EVENT, 1000);
                        startSwitch();
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }

    private void startSwitch() {
        switchCard();
        switchEvent();
        mOffset += 1000;
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
            FilmEvents event = mFilmDetail.events.get(i);
            if (matchEvent(event.getStartTime(), event.type)) {
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
        if (mOffset == 0) {
            mOffset = mCustomFile.play_offset_ms;
        } else if (Math.abs(mCustomFile.play_offset_ms - mOffset) >= 500) {
            mOffset = mCustomFile.play_offset_ms;
        }
        return mOffset;
    }

    private boolean matchCart(int time) {
        return time != -1 && mOffset - time >= 0;
    }

    private boolean matchEvent(int time, int type) {
        if (type == FilmEvents.TYPE_FILM) {
            return time != -1 && time - mOffset <= 5000;
        }
        return time != -1 && Math.abs(time - mOffset) <= 500;
    }

    private Class<? extends AbsEventActivity> getEventActivity(int type) {
        switch (type) {
            case FilmEvents.TYPE_FILM:
                return PlayerEventActivity.class;
            case FilmEvents.TYPE_PICTURE:
                return PictureEventActivity.class;
            case FilmEvents.TYPE_WEB:
                return WebEventActivity.class;
        }
        return null;
    }

    @Override
    protected void onRecognizeState(boolean state) {

    }

    @Override
    protected void onRecognizeResult(CustomFile customFile) {
        if (mCustomFile == null || !mCustomFile.audio_id.equals(customFile.audio_id)) {
            mCustomFile = customFile;
            getFilmDetail();
        } else {
            mCustomFile = customFile;
            getOffsetTime();
            startSwitch();
        }
    }

    private static class MyHandler extends Handler {

        private WeakReference<RecognizeResultActivity> weakReference;

        MyHandler(RecognizeResultActivity weakObj) {
            weakReference = new WeakReference<>(weakObj);
        }

        @Override
        public void handleMessage(Message msg) {
            RecognizeResultActivity weakObj = weakReference.get();
            if (weakObj != null) {
                if (msg.what == CHANGE_EVENT) {
                    weakObj.startSwitch();
                    sendEmptyMessageDelayed(CHANGE_EVENT, 1000);
                    LogUtils.d("RecognizeReceiver", "mOffset: " + DateUtils.formatTime(weakObj.mOffset));
                }
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
