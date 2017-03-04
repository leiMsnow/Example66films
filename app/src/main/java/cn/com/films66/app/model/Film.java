package cn.com.films66.app.model;

import java.util.List;

/**
 * Created by zhangleilei on 21/02/2017.
 */

public class Film {

    public int id;
    public String name;
    public String introduction;
    public String runtime;
    public String created_at;
    public String updated_at;
    public String cover_url;
    public String background_image_url;
    public List<FilmEvents> events;
    public List<LocationCards> location_cards;

    public String getRuntime() {
//        return DateUtils.formatTime(runtime * 60 * 1000);
        return runtime;
    }
}
