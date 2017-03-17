package cn.com.films66.app.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
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

    private FilmEvents mCurrentEvent;
    private boolean isOpen = false;

    private int mRryRecognize = 0;

    private SoundPool mSoundPool;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_recognize_result;
    }

    @Override
    protected void initData() {

        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        mSoundPool.load(this, R.raw.tips, 1);

        setTitle("");
        mHandler = new MyHandler(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCustomFile = getIntent().getParcelableExtra(Constants.KEY_RECOGNIZE_RESULT);
        getFilmDetail();

        Intent intent = new Intent(mContext, RecognizeService.class);
        intent.putExtra(Constants.KEY_RECOGNIZE_LOOP, true);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOpen = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOpen = false;
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
        if (mCustomFile == null) {
            mHandler.removeMessages(CHANGE_EVENT);
            return;
        }
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
            if (matchCart(locationCards)) {
                ImageShowUtils.showImage(mContext
                        , locationCards.card_url, ivLocationCard);
                break;
            }
        }
    }

    private void switchEvent() {
        if (isOpen) return;
        for (int i = 0, count = mFilmDetail.events.size(); i < count; i++) {
            FilmEvents event = mFilmDetail.events.get(i);
            if (mCurrentEvent != null && mCurrentEvent.id == event.id && mCurrentEvent.isUserCancel) {
                break;
            }
            if (matchEvent(event)) {
                mSoundPool.play(1, 1, 1, 0, 0, 1);
                if (mCurrentEvent != null) {
                    mCurrentEvent.isUserCancel = false;
                }
                mCurrentEvent = event;
                Class eventActivity = getEventActivity(mCurrentEvent.type);
                if (eventActivity != null) {
                    Intent intent = new Intent(mContext, eventActivity);
                    intent.putExtra(Constants.KEY_EVENT_INFO, event);
                    intent.putExtra(Constants.KEY_RECOGNIZE_OFFSET, mOffset);
                    startActivityForResult(intent, 0);
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                if (mCurrentEvent != null) {
                    mCurrentEvent.isUserCancel = data.getBooleanExtra(Constants.KEY_EVENT_CANCEL, false);
                }
            }
        }
    }

    private int getOffsetTime() {
        if (mOffset == 0 || Math.abs(mCustomFile.play_offset_ms - mOffset) >= 500) {
            mOffset = mCustomFile.play_offset_ms;
        }
        return mOffset;
    }

    private boolean matchCart(LocationCards cards) {
        return (cards.getStartTime() != -1) && mOffset - cards.getStartTime() >= 0;
    }

    private boolean matchEvent(FilmEvents events) {
        if (events.getStartTime() != -1 && events.getEndTime() != -1) {
            if (mCurrentEvent != null && mCurrentEvent.id == events.id && mCurrentEvent.isUserCancel) {
                return false;
            } else {
                return mOffset >= events.getStartTime() && mOffset <= events.getEndTime();
            }
        }
        return false;
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
        if (customFile == null) {
            mRryRecognize++;
            LogUtils.d(RecognizeResultActivity.class.getName(), "未识别到次数：" + mRryRecognize);
            if (mRryRecognize >= 1) {
                mRryRecognize = 0;
                mHandler.removeMessages(CHANGE_EVENT);
            }
            return;
        }
        mRryRecognize = 0;
        if (!mCustomFile.audio_id.equals(customFile.audio_id)) {
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
