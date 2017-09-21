package cn.com.films66.app.model;

import cn.com.films66.app.utils.TimeUtils;

/**
 * Created by zhangleilei on 2017/2/25.
 */

public class LocationCards {

    public int id;
    public String start_time;
    public String card_url;

    public long getStartTime() {
        return TimeUtils.stringToTime(start_time);
    }
}
