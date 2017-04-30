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
    private boolean isLoop = false;
    private int mRecognizeCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
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
        mConfig.accessKey = "e3c58e23b70a881d960cfa963d0f1965";
        mConfig.accessSecret = "wP2o4851sOditOCixl8s8ru2iTf9pdQ7f10xofxr";
        mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
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
//            LogUtils.d(RecognizeService.class.getName(), "原始识别结果： " + s);
            RecognizeResult recognizeEntity = new Gson().fromJson(s
                    , new TypeToken<RecognizeResult>() {
                    }.getType());
            if (recognizeEntity != null && recognizeEntity.status.code == 0) {
                if (recognizeEntity.metadata.custom_files != null) {
                    sendRecognizeResult(recognizeEntity.metadata.custom_files.get(0));
                } else {
                    sendRecognizeResult(null);
                }
            } else {
                sendRecognizeResult(null);
            }
        }

        @Override
        public void onVolumeChanged(double v) {

        }
    };

    private void loopRecognize() {
        if (isLoop) {
            startRecognize();
        }
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
        if (!isLoop) {
            LogUtils.d(RecognizeService.class.getName(), "cancelRecognize");
            cancelRecognize();
        }
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

        if (mRecognizeCount >= 5) {
            mRecognizeCount = 0;
            return;
        }
        if (!mProcessing) {
            mProcessing = true;
            if (mClient == null || !mClient.startRecognize()) {
                mProcessing = false;
                LogUtils.d(RecognizeService.class.getName(), "启动识别失败");
            }
            sendRecognizeState();
        }
    }

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
    private void sendRecognizeResult(CustomFile customFile) {
        if (customFile == null) {
            mRecognizeCount++;
        } else {
            mRecognizeCount = 0;
        }
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
            LogUtils.d(RecognizeService.class.getName(), "cancelRecognize");
            cancelRecognize();
            mClient.release();
            initState = false;
            mClient = null;
        }
    }
}
