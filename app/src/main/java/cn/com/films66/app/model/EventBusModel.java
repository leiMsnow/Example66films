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

    public static class ControlRecognize{
       public boolean isRecognize = false;
        public ControlRecognize(boolean isRecognize) {
            this.isRecognize = isRecognize;
        }
    }

    public static class ControlRecognizeLoop{
       public boolean isLoop = false;
        public ControlRecognizeLoop(boolean isLoop) {
            this.isLoop = isLoop;
        }
    }

    public static class RecognizeState{
       public boolean recognizeState = false;
        public RecognizeState(boolean recognizeState) {
            this.recognizeState = recognizeState;
        }
    }

    public static class DownLoad {
    }
}
