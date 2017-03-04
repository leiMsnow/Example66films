package cn.com.films66.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.IACRCloudListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.ToastUtils;

import java.io.File;

import cn.com.films66.app.model.CustomFile;
import cn.com.films66.app.model.RecognizeResult;
import cn.com.films66.app.utils.Constants;

public class RecognizeService extends Service {

    private ACRCloudClient mClient;

    private boolean mProcessing = false;
    private boolean initState = false;
    private long startTime = 0;
    private boolean isLoop = false;
//    private MyHandler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
//        mHandler = new MyHandler(this);
        initACRCloud();
    }

    private void initACRCloud() {
        String path = Environment.getExternalStorageDirectory().toString()
                + "/acrcloud";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        ACRCloudConfig mConfig = new ACRCloudConfig();
        mConfig.context = this;
        mConfig.host = "cn-north-1.api.acrcloud.com";
        // offline db path, you can change it with other path which this app can access.
        mConfig.dbPath = path;
        mConfig.accessKey = "bb04322f38c38b18a931320d00b2eb8b";
        mConfig.accessSecret = "arJFm2S5MIuY9PBMkRJmrdzQrD64P1stIYCjCXCK";
        mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
        mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
        //mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH;
        mConfig.acrcloudListener = acrCloudListener;
        mClient = new ACRCloudClient();
        // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
        // the function initWithConfig is used to load offline db, and it may cost long time.
        initState = mClient.initWithConfig(mConfig);
        LogUtils.d(RecognizeService.class.getName(), "initState=" + initState);
        if (initState) {
            //start prerecord, you can call "mClient.stopPreRecord()" to stop prerecord.
            mClient.startPreRecord(3000);
        }
    }

    private IACRCloudListener acrCloudListener = new IACRCloudListener() {

        @Override
        public void onResult(String s) {
            cancelRecognize();
            loopRecognize();

            long time = (System.currentTimeMillis() - startTime) / 1000;
//            LogUtils.d(RecognizeService.class.getName(), "识别结束，用时：" + time + "s 结果：" + s);
            RecognizeResult recognizeEntity = new Gson().fromJson(s
                    , new TypeToken<RecognizeResult>() {
                    }.getType());
            if (recognizeEntity != null && recognizeEntity.status.code == 0) {
                if (recognizeEntity.metadata.custom_files != null) {
                    sendRecognizeState(recognizeEntity.metadata.custom_files.get(0));
                }
            }
        }

        @Override
        public void onVolumeChanged(double v) {

        }
    };

    private void loopRecognize() {
        if (isLoop) {
//            mHandler.sendEmptyMessageDelayed(0, 1000);
            startRecognize();
        }
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            isLoop = intent.getBooleanExtra(Constants.KEY_RECOGNIZE_LOOP, false);
            loopRecognize();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startRecognize() {
        if (!initState) {
            ToastUtils.getInstance().showToast("暂时无法识别,请稍后重试");
            initACRCloud();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
            if (mClient == null || !mClient.startRecognize()) {
                mProcessing = false;
                LogUtils.d(RecognizeService.class.getName(), "start error!");
            }
            startTime = System.currentTimeMillis();
            sendRecognizeState();
        }
    }

//    private static class MyHandler extends Handler {
//
//        private WeakReference<RecognizeService> weakReference;
//
//        public MyHandler(RecognizeService weakObj) {
//            weakReference = new WeakReference<>(weakObj);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            RecognizeService weakObj = weakReference.get();
//            if (weakObj != null) {
//                if (msg.what == 0)
//                    weakObj.startRecognize();
//            }
//        }
//    }


    public void cancelRecognize() {
        if (mProcessing && mClient != null) {
            mProcessing = false;
            mClient.cancel();
            sendRecognizeState();
        }
    }

    // 发送识别状态
    private void sendRecognizeState() {
        Intent intent = new Intent();
        intent.setAction(Constants.RECOGNIZE_STATE_ACTION);
        intent.putExtra(Constants.KEY_RECOGNIZE_STATE, mProcessing);
        sendBroadcast(intent);
    }

    // 发送识别结果
    private void sendRecognizeState(CustomFile customFile) {
        Intent intent = new Intent();
        intent.setAction(Constants.RECOGNIZE_RESULT_ACTION);
        intent.putExtra(Constants.KEY_RECOGNIZE_RESULT, customFile);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new RecognizeBinder();
    }

    public class RecognizeBinder extends Binder {
        public RecognizeService getService() {
            return RecognizeService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mClient != null) {
            cancelRecognize();
            mClient.release();
            initState = false;
            mClient = null;
//            mHandler.removeMessages(0);
//            mHandler = null;
        }
    }
}
