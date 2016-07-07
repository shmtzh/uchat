package com.roket.shmtzh.uchat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.roket.shmtzh.uchat.R;
import com.roket.shmtzh.uchat.webapi.NetworkManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;

/**
 * Created by shmtzh on 7/5/16.
 */
public class PicassoUtils {

    private static final String TAG = PicassoUtils.class.getSimpleName();

    public static void loadImageCacheFirst(@Nullable final Context context, @Nullable final String imgUrl, @Nullable final ImageView imageView) {
        loadImageCacheFirst(context, imgUrl, imageView, 0, 0);
    }

    public static void loadImageCacheFirst(@Nullable final Context context, @Nullable final String imgUrl, @Nullable final ImageView imageView, int width, int height) {
        if (ifParamsCorrect(context, imgUrl, imageView)) {
            final Picasso picasso = NetworkManager.getInstance().getPicasso();//new Picasso.Builder(context).downloader(okHttpDownloader).build();
            picasso.setIndicatorsEnabled(false);
            RequestCreator rc = picasso.load(imgUrl)
                    .noPlaceholder()
                    .networkPolicy(NetworkPolicy.OFFLINE);
            if (width != 0 && height != 0) {
                rc.resize(width, height);
            }
            rc.into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    picasso.load(imgUrl)
                            .noPlaceholder()
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError() {
                                    Log.v("Picasso", "Could not fetch image");
                                }
                            });
                }
            });
            ;
        }
    }


    private static boolean ifParamsCorrect(@Nullable final Context context, @Nullable final String imgUrl, @Nullable final ImageView imageView) {
        if (context == null) {
            Log.e(TAG, "Context is null");
            return false;
        }
        if (TextUtils.isEmpty(imgUrl)) {
            Log.e(TAG, "Image url is empty");
            return false;
        }
        if (imageView == null) {
            Log.e(TAG, "Image holder is empty");
            return false;
        }
        return true;
    }

    private static boolean ifParamsCorrect(@Nullable final String imgUrl, @Nullable final View view) {
        if (TextUtils.isEmpty(imgUrl)) {
            Log.e(TAG, "Image url is empty");
            return false;
        }
        if (view == null) {
            Log.e(TAG, "View holder is empty");
            return false;
        }
        return true;
    }


    public static void loadImageCacheFirst(String imgUrl, ImageView imageView) {
        loadImageCacheFirst(imageView.getContext(), imgUrl, imageView);
    }



    private static Picasso getPicasso(String imgUrl, Context context) {
        final Picasso picasso;
            picasso = NetworkManager.getInstance().getPicasso();
        return picasso;
    }

}
