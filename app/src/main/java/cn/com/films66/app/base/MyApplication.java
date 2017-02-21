package cn.com.films66.app.base;

import android.content.Intent;

import com.shuyu.core.CoreApplication;
import com.shuyu.core.api.CacheInterceptor;
import com.shuyu.core.uils.LogUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.com.films66.app.BuildConfig;
import cn.com.films66.app.service.RecognizeService;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static cn.com.films66.app.BuildConfig.IS_DEBUG;

/**
 * Created by zhangleilei on 8/31/16.
 */
public class MyApplication extends CoreApplication {

    private static MyApplication mApplication;
    private static final int TIMEOUT_READ = 15;
    private static final int TIMEOUT_CONNECTION = 15;
    private static OkHttpClient mOkHttpClient;

    @Override
    public void onCreate() {
        MCrashHandler.getInstance().init();
        super.onCreate();
        mApplication = this;
        LogUtils.isDebug = BuildConfig.IS_DEBUG;

        startService(new Intent(this, RecognizeService.class));
    }

    public OkHttpClient genericClient() {

        if (mOkHttpClient != null)
            return mOkHttpClient;

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        HttpLoggingInterceptor.Level level = IS_DEBUG ?
                HttpLoggingInterceptor.Level.HEADERS :
                HttpLoggingInterceptor.Level.NONE;
        logInterceptor.setLevel(level);

//        Map<String, String> commonParams = CommonUtils.getCommonParams();

//        List<String> headerParams = new ArrayList<>();
//        headerParams.add("Connection:Keep-Alive");
//        headerParams.add("accet:*/*");
//        headerParams.add("Accept-Encoding:gzip");
//        headerParams.add("User-Agent:okhttp/2.5.0");
//        headerParams.add("H-Quality:L");
//        headerParams.add("Pay-Key:" + getPayKey(commonParams));

//        BasicParamsInterceptor basicParamsInterceptor = new BasicParamsInterceptor.Builder()
//                .addHeaderLinesList(headerParams)
//                .addQueryParamsMap(commonParams)
//                .build();

        File cacheFile = new File(CoreApplication.getApplication().getCacheDir(), "retrofit_cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 10);

        return mOkHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .retryOnConnectionFailure(true)
                .addInterceptor(logInterceptor)
                .addNetworkInterceptor(new CacheInterceptor())
//                .addInterceptor(basicParamsInterceptor)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                .build();
    }

    public static MyApplication getApplication() {
        return mApplication;
    }
}
