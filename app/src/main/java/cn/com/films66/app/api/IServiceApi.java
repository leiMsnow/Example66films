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
     * 首页
     */
    @GET("films")
    Observable<List<FilmEntity>> getFilms();
}
