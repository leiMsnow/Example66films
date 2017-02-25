package cn.com.films66.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.shuyu.core.BaseFragment;

import java.lang.ref.WeakReference;

import cn.com.films66.app.model.FilmEventsEntity;
import cn.com.films66.app.utils.Constants;

/**
 * Created by zhangleilei on 2017/2/25.
 */

public abstract class EventFragment extends BaseFragment {

    protected FilmEventsEntity mEvents;
    private MyHandler myHandler;

    private static class MyHandler extends Handler {

        private WeakReference<EventFragment> weakReference;

        public MyHandler(EventFragment weakObj) {
            weakReference = new WeakReference<>(weakObj);
        }

        @Override
        public void handleMessage(Message msg) {
            EventFragment weakObj = weakReference.get();
            if (weakObj != null) {
                weakObj.getActivity().finish();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvents = getArguments().getParcelable(Constants.KEY_EVENT_INFO);
        myHandler = new MyHandler(this);
        if (mEvents != null) {
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    myHandler.sendEmptyMessage(0);
                }
            }, mEvents.getEndTime() - mEvents.getStartTime());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler = null;
    }
}
