package cn.com.films66.app.model;

/**
 * Created by Azure on 2017/9/16.
 */

public class EventBusModel {

    public static class DownloadProgress {

        public int soFarBytes;
        public int totalBytes;

        public DownloadProgress(int soFarBytes, int totalBytes) {
            this.soFarBytes = soFarBytes;
            this.totalBytes = totalBytes;
        }

    }
}
