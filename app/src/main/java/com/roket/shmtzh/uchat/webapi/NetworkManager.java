package com.roket.shmtzh.uchat.webapi;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.net.CookieManager;
import java.net.CookiePolicy;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.roket.shmtzh.uchat.api.MessageApi;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by shmtzh on 7/5/16.
 */
public class NetworkManager {


    private static final String TAG = "WebApiManager";

    private static NetworkManager sInstance;
    private Picasso mPicasso;
    private CookieManager cookieManager;
    private TokenInterceptor mTokenInterceptor;
    private final Context mContext;
    private OkHttpClient okHttpClient;
    private Handler mHandler;
    private MessageApi              mMessageApi;


    private NetworkManager(Context context) {
        mTokenInterceptor = new TokenInterceptor();
        mContext = context;
        initPicasso(context);
        initApi();
    }

    public static void create(Context context) {
        if (sInstance != null) {
            throw new IllegalStateException("Already created!");
        }
        sInstance = new NetworkManager(context);
    }

    public static synchronized NetworkManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Call create() first");
        }
        return sInstance;
    }

    private void initApi() {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor interceptor = chain -> {
            Request request = chain.request();
            Response response = chain.proceed(request);

            boolean showToast = (response.code() == 400);
            if (showToast) {
                String txt = "intercept: error in request" + request.method() + " " + request.url(); //+ " response: " + response.body().string();
                Log.e(TAG, txt);
                Message message = mHandler.obtainMessage(0, txt);
                message.sendToTarget();
            }
            return response;
        };
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        okHttpClient = new OkHttpClient().newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(logging)
                .addInterceptor(interceptor)
                .addNetworkInterceptor(mTokenInterceptor)
                .build();


        Retrofit apiRestAdapter = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_END_POINT)
                .client(okHttpClient)
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

//        mUserApi = apiRestAdapter.create(UserApi.class);


        initLooper();
    }

    private void initLooper() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Log.d(TAG, "handleMessage: ");
                Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void initPicasso(Context context) {
        Picasso.Builder mBuilder = new Picasso.Builder(context);
        LruCache picassoCache = new LruCache(context);
        mBuilder.memoryCache(picassoCache);

        if (BuildConfig.DEBUG) {
            mBuilder.indicatorsEnabled(true);
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient picassoClient = new OkHttpClient.Builder()
                .addInterceptor(new TokenInterceptor())
                .addInterceptor(logging)
                .addNetworkInterceptor(chain -> {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder().header("Cache-Control", "max-age=" + (60 * 60 * 24)).build();
                }).build();
        mBuilder.downloader(new OkHttp3Downloader(picassoClient));

        mPicasso = mBuilder.build();
        mBuilder.listener((picasso, uri, exception) -> exception.printStackTrace());
    }

    public Picasso getPicasso() {
        return mPicasso;
    }


    public MessageApi getMessageApi() {
        return mMessageApi;
    }


}
