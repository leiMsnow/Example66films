package cn.com.films66.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.SDCardUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.com.films66.app.api.SimpleFileDownloadListener;
import cn.com.films66.app.model.EventBusModel;
import cn.com.films66.app.utils.Constants;
import cn.com.films66.app.utils.VideoUtils;

public class DownloadService extends Service {

    private List<String> eventsUrl;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (eventsUrl == null) {
            if (intent != null) {
                eventsUrl = intent.getStringArrayListExtra(Constants.KEY_EVENTS_LIST);
                if (eventsUrl != null) {
                    startDownload(eventsUrl);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadFirst(String url) {
        String path = SDCardUtils.getCachePath(Constants.DOWNLOAD_PATH)
                + VideoUtils.createLocalName(url);
        FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(new SimpleFileDownloadListener() {
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtils.d("DownloadProgress", "soFarBytes: " + soFarBytes);
                        LogUtils.d("DownloadProgress", "totalBytes: " + totalBytes);
                        EventBus.getDefault().post(new EventBusModel.DownloadProgress(soFarBytes, totalBytes));
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        EventBus.getDefault().post(new EventBusModel.DownLoad());
                    }
                }).start();
    }

    //第一个视频单独下载，以便于拿到监听
    private void startDownload(List<String> url) {
        downloadFirst(url.get(0));
        downloadOther(url);
    }

    private void downloadOther(List<String> url) {
        FileDownloadQueueSet queueSet = new FileDownloadQueueSet(new SimpleFileDownloadListener());
        List<BaseDownloadTask> tasks = new ArrayList<>();
        for (int i = 1; i < url.size(); i++) {
            String currentUrl = url.get(i);
            String path = SDCardUtils.getCachePath(Constants.DOWNLOAD_PATH)
                    + VideoUtils.createLocalName(currentUrl);
            tasks.add(FileDownloader.getImpl().create(currentUrl)
                    .setPath(path).setTag(i + 1));
        }
        queueSet.disableCallbackProgressTimes();
        queueSet.setAutoRetryTimes(1);
        queueSet.downloadTogether(tasks);
        queueSet.start();
        queueSet.addTaskFinishListener(new BaseDownloadTask.FinishListener() {
            @Override
            public void over(BaseDownloadTask task) {
                stopSelf();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
