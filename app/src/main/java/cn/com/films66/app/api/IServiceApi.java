package cn.com.films66.app.api;


import java.util.List;

import cn.com.films66.app.model.FilmEntity;
import cn.com.films66.app.model.FilmEventsEntity;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 接口
 * Created by zhangleilei on 9/5/16.
 */

public interface IServiceApi {
    /**
     * 获取电影列表
     */
    @GET("films")
    Observable<List<FilmEntity>> getFilms();

    /**
     * 获取单个电影
     */
    @GET("films/{id}")
    Observable<FilmEntity> getFilmDetail(@Path("id") int id);

    /**
     * 获取电影互动内容
     */
    @GET("films/{id}/events")
    Observable<List<FilmEventsEntity>> getFilmEvents();

}
