package cn.com.films66.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.SDCardUtils;

import cn.com.films66.app.api.SimpleFileDownloadListener;
import cn.com.films66.app.model.FilmEvents;
import cn.com.films66.app.utils.Constants;
import cn.com.films66.app.utils.VideoUtils;

public class DownloadService extends Service {

    private String mDownloadUrl;
    private FilmEvents events;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            events = intent.getParcelableExtra(Constants.KEY_EVENT_INFO);
            if (events==null||TextUtils.isEmpty(events.resources_url)){
                stopSelf();
            }else {
                startDownload(events.resources_url);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload(String url) {
        if (TextUtils.isEmpty(mDownloadUrl)||!mDownloadUrl.equals(url)){
            mDownloadUrl = url;
            String path = SDCardUtils.getCachePath(Constants.DOWNLOAD_PATH)
                    + VideoUtils.createLocalName(url);
            LogUtils.d(DownloadService.class.getName(), "downloadPath: " + path);
            LogUtils.d(DownloadService.class.getName(), "mDownloadUrl: " + mDownloadUrl);
            FileDownloader.getImpl().create(mDownloadUrl)
                    .setPath(path)
                    .setListener(new SimpleFileDownloadListener() {
                        @Override
                        protected void completed(BaseDownloadTask task) {
                            super.completed(task);
                            Intent intent = new Intent();
                            intent.setAction(Constants.DOWNLOAD_STATE_ACTION);
                            sendBroadcast(intent);
                            stopSelf();
                        }
                    }).start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
