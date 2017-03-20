package cn.com.films66.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.SDCardUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.films66.app.api.SimpleFileDownloadListener;
import cn.com.films66.app.utils.Constants;
import cn.com.films66.app.utils.VideoUtils;

public class DownloadService extends Service {

    //    private String mDownloadUrl;
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

    private void startDownload(List<String> url) {

        FileDownloadQueueSet queueSet = new FileDownloadQueueSet(queueTarget);

        List<BaseDownloadTask> tasks = new ArrayList<>();
        for (int i = 0; i < url.size(); i++) {

            String currentUrl = url.get(i);
            String path = SDCardUtils.getCachePath(Constants.DOWNLOAD_PATH)
                    + VideoUtils.createLocalName(currentUrl);
            LogUtils.d(DownloadService.class.getName(), "downloadPath: " + path);
            LogUtils.d(DownloadService.class.getName(), "currentUrl: " + currentUrl);

            tasks.add(FileDownloader.getImpl().create(currentUrl)
                    .setPath(path).setTag(i + 1));
        }
        queueSet.disableCallbackProgressTimes();
        queueSet.setAutoRetryTimes(1);
        queueSet.downloadSequentially(tasks);

//            FileDownloader.getImpl().create(currentUrl)
//                    .setPath(path)
//                    .setCallbackProgressTimes(0)
//                    .setListener(queueTarget)
//                    .asInQueueTask()
//                    .enqueue();
//        FileDownloader.getImpl().start(queueTarget, true);

    }

    private SimpleFileDownloadListener queueTarget = new SimpleFileDownloadListener() {
        @Override
        protected void completed(BaseDownloadTask task) {
            super.completed(task);
            if (Integer.parseInt(task.getTag().toString()) == 1) {
                LogUtils.d(DownloadService.class.getName(), "completed-taskID: " + task.getTag());
                Intent intent = new Intent();
                intent.setAction(Constants.DOWNLOAD_STATE_ACTION);
                sendBroadcast(intent);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
