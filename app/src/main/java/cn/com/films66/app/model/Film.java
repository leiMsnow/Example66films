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
    public String type;
    public String releaseDate;
    public String cover_url;
    public List<FilmEvents> events;
    public List<LocationCards> location_cards;

    public String getRuntime() {
        return runtime;
    }
}
