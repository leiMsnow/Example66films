package cn.com.films66.app.api;

import com.shuyu.core.CoreApplication;
import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.NetUtils;
import com.shuyu.core.uils.SPUtils;
import com.shuyu.core.uils.ToastUtils;

import cn.com.films66.app.base.MyApplication;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Azure on 2016/9/5.
 */
public class BaseApi {

    public static final String KEY_BASE_URL = "KEY_BASE_URL";
    private static final String LOCAL_SERVER_URL = "http://www.duodongyule.com";
    public static final String BASE_URL = LOCAL_SERVER_URL + "/api/";

    public static <T> T createApi(Class<T> service) {
        final String url = SPUtils.get(CoreApplication.getApplication()
                , KEY_BASE_URL, BASE_URL).toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(NobodyConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(MyApplication.getApplication().genericClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
                .subscribe(new Observer<T>() {

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   LogUtils.d("onError", e.getMessage());
                                   if (listener != null) {
                                       listener.onFail();
                                   }
                               }

                               @Override
                               public void onComplete() {

                               }

                               @Override
                               public void onSubscribe(Disposable disposable) {

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
