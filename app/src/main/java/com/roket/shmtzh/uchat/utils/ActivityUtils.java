package com.roket.shmtzh.uchat.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.roket.shmtzh.uchat.R;

/**
 * Created by shmtzh on 7/1/16.
 */
public class ActivityUtils {
    private ActivityUtils() {/* prevent instantiation */}

    public static void start(Activity source, Intent what) {
        source.startActivity(what);
    }

    public static void startAsPopupForResult(Activity source, Intent what, int requestCode) {
        source.startActivityForResult(what, requestCode);
    }

    public static void startAsPopupForResult(Fragment fragment, Intent what, int requestCode) {
        fragment.startActivityForResult(what, requestCode);
    }

    public static void navigate(Activity source, Intent where) {
        source.startActivity(where.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        source.finish();
    }

    public static void finish(Activity source) {
        source.finish();
    }


}
