package cn.com.films66.app.model;

import java.util.List;

/**
 * Created by zhangleilei on 21/02/2017.
 */

public class RecognizeResult {

    public StatusEntity status;
    public MetadataEntity metadata;

    public static class StatusEntity {
        public int code;
    }

    public static class MetadataEntity {
        public List<CustomFile> custom_files;
    }
}
