package cn.com.films66.app.model;

import java.util.List;

/**
 * Created by zhangleilei on 21/02/2017.
 */

public class RecognizeEntity {

    public StatusEntity status;
    public MetadataEntity metadata;
    public int result_type;

    public static class StatusEntity {
        public String msg;
        public int code;
        public String version;
    }

    public static class MetadataEntity {
        public String timestamp_utc;
        public List<MusicEntity> music;
    }
}
