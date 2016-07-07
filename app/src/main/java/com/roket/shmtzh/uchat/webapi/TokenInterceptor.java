package com.roket.shmtzh.uchat.webapi;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by shmtzh on 7/5/16.
 */
public class TokenInterceptor implements Interceptor {

    public static String mToken = null;

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
//        if (mToken == null && Hawk.contains(PrefConstants.USER)) {
//            User mUser = Hawk.get(PrefConstants.USER);
//
//            if (mUser.getToken() != null && mUser.getToken().getAccessToken() != null && mUser.getToken().getAccessToken().length() > 0) {
//                mToken = mUser.getToken().getAccessToken();
//            }
//        }

        Request originalRequest = chain.request();
        if (mToken == null || !originalRequest.url().host().contains("rolr.net")) {
            return chain.proceed(originalRequest);
        } else {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + mToken)
                    .build();

            Log.w("TokenInterceptor", "Calling: " + newRequest.method() + " :: " + newRequest.url().url().toString());
            return chain.proceed(newRequest);
        }
    }

    public void clear() {
        mToken = null;
    }
}
