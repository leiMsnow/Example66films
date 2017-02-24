package cn.com.films66.app.api;


import java.util.List;

import cn.com.films66.app.model.FilmEntity;
import retrofit2.http.GET;
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
    @GET("films/1")
    Observable<FilmEntity> getFilmDetail();

    /**
     * 获取电影互动内容
     */
//    @GET("films/1/events")
//    Observable<> getFilmEvents();

}
