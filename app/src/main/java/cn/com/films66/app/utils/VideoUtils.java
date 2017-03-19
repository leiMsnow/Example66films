package cn.com.films66.app.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.shuyu.core.uils.SDCardUtils;

import java.io.File;
import java.net.URLDecoder;

/**
 * Created by Azure on 2017/3/18.
 */

public class VideoUtils {

    public static String getLocalURL(String resources_url) {
        File file = getLocalFile(resources_url);
        return (file != null && file.exists()) ? file.getAbsolutePath() : resources_url;
    }

    public static boolean hasLocalURL(String resources_url) {
        File file = getLocalFile(resources_url);
        return file != null && file.exists();
    }

    @Nullable
    private static File getLocalFile(String resources_url) {
        if (!TextUtils.isEmpty(resources_url)) {
            String localName = createLocalName(resources_url);
            if (!localName.equals("temp")) {
                String localUrl = SDCardUtils.getCachePath(Constants.DOWNLOAD_PATH) + localName;
                return new File(localUrl);
            }
        }
        return null;
    }

    public static String createLocalName(String resources_url) {
        int lastSplit = resources_url.lastIndexOf("/");
        if (lastSplit != -1) {
            return URLDecoder.decode(resources_url.substring(lastSplit + 1));
        }
        return "temp";
    }
}