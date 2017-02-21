package cn.com.films66.app.api;

import com.shuyu.core.CoreApplication;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.NetUtils;
import com.shuyu.core.uils.SPUtils;
import com.shuyu.core.uils.ToastUtils;

import cn.com.films66.app.base.MyApplication;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Azure on 2016/9/5.
 */
public class BaseApi {

    public static final String KEY_BASE_URL = "KEY_BASE_URL";
    private static final String LOCAL_SERVER_URL = "http://film.timepill.net";
    public static final String BASE_URL = LOCAL_SERVER_URL + "/api/";

    public static <T> T createApi(Class<T> service) {
        final String url = SPUtils.get(CoreApplication.getApplication()
                , KEY_BASE_URL, BASE_URL).toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(MyApplication.getApplication().genericClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(service);
    }

    public static <T> void request(Observable<T> observable,
                                   final IResponseListener<T> listener) {

        if (!NetUtils.isConnected(MyApplication.getApplication())) {
            ToastUtils.getInstance().showToast("网络不可用,请检查网络");
            if (listener != null) {
                listener.onFail();
            }
            return;
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<T>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   LogUtils.d("onError", e.getMessage());
                                   if (listener != null) {
                                       listener.onFail();
                                   }
                               }

                               @Override
                               public void onNext(T data) {
                                   if (listener != null) {
                                       listener.onSuccess(data);
                                   }
                               }
                           }
                );
    }

    public interface IResponseListener<T> {

        void onSuccess(T data);

        void onFail();
    }

}
