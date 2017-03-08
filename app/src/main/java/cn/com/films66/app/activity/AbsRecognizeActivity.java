package cn.com.films66.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.model.CustomFile;
import cn.com.films66.app.utils.Constants;

/**
 * Created by Azure on 2017/2/26.
 */

public abstract class AbsRecognizeActivity extends AppBaseActivity {

    private RecognizeReceiver mRecognizeReceiver;

    private class RecognizeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.RECOGNIZE_STATE_ACTION)) {
                boolean processing = intent.getBooleanExtra(Constants.KEY_RECOGNIZE_STATE, false);
                onRecognizeState(processing);

            } else if (intent.getAction().equals(Constants.RECOGNIZE_RESULT_ACTION)) {
                CustomFile customFile = intent.getParcelableExtra(Constants.KEY_RECOGNIZE_RESULT);
                onRecognizeResult(customFile);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    protected abstract void onRecognizeState(boolean state);

    protected abstract void onRecognizeResult(CustomFile customFile);

    private void registerReceiver() {
        if (mRecognizeReceiver == null) {
            mRecognizeReceiver = new RecognizeReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.RECOGNIZE_STATE_ACTION);
            filter.addAction(Constants.RECOGNIZE_RESULT_ACTION);
            registerReceiver(mRecognizeReceiver, filter);
        }
    }

    protected void unRegisterReceiver() {
        if (mRecognizeReceiver != null) {
            unregisterReceiver(mRecognizeReceiver);
            mRecognizeReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }
}
