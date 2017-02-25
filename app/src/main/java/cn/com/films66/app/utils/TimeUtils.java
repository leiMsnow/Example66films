package cn.com.films66.app.utils;

/**
 * Created by zhangleilei on 2017/2/25.
 */

public class TimeUtils {

    public static int stringToTime(String str) {
        String[] strTime = str.split(":");
        if (strTime.length == 3) {
            int hour = Integer.parseInt(strTime[0]);
            int minute = Integer.parseInt(strTime[1]);
            int second = Integer.parseInt(strTime[2]);
            hour = hour * 60 * 60;
            minute = minute * 60;

            return (hour + minute + second) * 1000;
        }
        return -1;
    }
}
