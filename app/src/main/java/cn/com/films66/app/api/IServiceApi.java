package cn.com.films66.app.api;


import java.util.List;

import cn.com.films66.app.model.Film;
import cn.com.films66.app.model.HelpInfo;
import cn.com.films66.app.model.NoBodyEntity;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
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
    Observable<List<Film>> getFilms();

    /**
     * 获取单个电影
     */
    @GET("films/{id}")
    Observable<Film> getFilmDetail(@Path("id") int id);

    /**
     * 获取帮助信息
     */
    @GET("help_info")
    Observable<List<HelpInfo>> getHelpInfo();

    @POST("feedback")
    Observable<NoBodyEntity> sendFeedback(@Query("content") String context,
                                          @Query("platform") int platform);

}
