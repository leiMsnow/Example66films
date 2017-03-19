package cn.com.films66.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.shuyu.core.uils.AppUtils;
import com.shuyu.core.uils.DateUtils;
import com.shuyu.core.uils.ImageShowUtils;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.widget.BaseDialog;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.model.CustomFile;
import cn.com.films66.app.model.Film;
import cn.com.films66.app.model.FilmEvents;
import cn.com.films66.app.model.LocationCards;
import cn.com.films66.app.service.DownloadService;
import cn.com.films66.app.service.RecognizeService;
import cn.com.films66.app.utils.Constants;
import cn.com.films66.app.utils.VideoUtils;

public class RecognizeResultActivity extends AbsRecognizeActivity {

    @Bind(R.id.iv_location_card)
    ImageView ivLocationCard;
    @Bind(R.id.rl_wait)
    View waitView;

    private static final int CHANGE_EVENT = 0;

    private CustomFile mCustomFile;
    private Film mFilmDetail;
    private MyHandler mHandler;
    private int mOffset = 0;

    private FilmEvents mCurrentEvent;
    private int mRryRecognize = 0;
    private boolean isPause = false;

    private SoundPool mSoundPool;
    private int sampleId = 0;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_recognize_result;
    }

    @Override
    protected void initData() {
        setTitle("");

        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        sampleId = mSoundPool.load(this, R.raw.tips, 1);

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
    protected void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
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

        if (isPause && !AppUtils.isBackground()) return;

        for (int i = 0, count = mFilmDetail.events.size(); i < count; i++) {
            FilmEvents event = mFilmDetail.events.get(i);
            LogUtils.i(RecognizeResultActivity.class.getName(), "matchEvent-event：" + event);
            LogUtils.i(RecognizeResultActivity.class.getName(), "matchEvent-offset：" + mOffset);
            if (matchEvent(event)) {
                mSoundPool.play(sampleId, 1, 1, 1, 1, 1);
                if (mCurrentEvent != null) {
                    mCurrentEvent.isUserCancel = false;
                }
                mCurrentEvent = event;
                if (event.type == FilmEvents.TYPE_FILM &&
                        !VideoUtils.hasLocalURL(event.resources_url)) {
                    isPause = true;
                    LogUtils.d(RecognizeResultActivity.class.getName(), "识别到未下载的视频");
                    BaseDialog.Builder builder = new BaseDialog.Builder(mContext);
                    builder.setCancelable(false)
                            .setMessage("识别到精彩剧集啦，下载观看吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isPause = false;
                                    mCurrentEvent.isUserCancel = true;
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isPause = true;
                                    waitView.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(mContext, DownloadService.class);
                                    intent.putExtra(Constants.KEY_EVENT_INFO, mCurrentEvent);
                                    startService(intent);
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    Class eventActivity = getEventActivity(mCurrentEvent.type);
                    startEventActivity(eventActivity);
                }
                break;
            }
        }
    }

    private void startEventActivity(Class eventActivity) {
        if (eventActivity != null) {
            Intent intent = new Intent(mContext, eventActivity);
            intent.putExtra(Constants.KEY_EVENT_INFO, mCurrentEvent);
            intent.putExtra(Constants.KEY_RECOGNIZE_OFFSET, mOffset);
            startActivityForResult(intent, 0);
            waitView.setVisibility(View.GONE);
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
        if (mOffset == 0 || Math.abs(mCustomFile.play_offset_ms - mOffset) >= 200) {
            mOffset = mCustomFile.play_offset_ms;
        }
        mCurrentEvent = null;
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
            if (mRryRecognize >= 2) {
                mRryRecognize = 0;
                mHandler.removeMessages(CHANGE_EVENT);
            }
            mCustomFile = null;
            return;
        }
        if (mCustomFile == null) {
            mCustomFile = customFile;
            mHandler.sendEmptyMessage(CHANGE_EVENT);
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

    @Override
    protected void openPlayer() {
        isPause = false;
        startEventActivity(PlayerEventActivity.class);
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
                    LogUtils.i("RecognizeReceiver", "mOffset: " + DateUtils.formatTime(weakObj.mOffset));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(CHANGE_EVENT);
        mHandler = null;
        mSoundPool.release();
        mSoundPool = null;
    }
}
