package com.roket.shmtzh.uchat;

import android.app.Application;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.roket.shmtzh.uchat.webapi.NetworkManager;

/**
 * Created by shmtzh on 7/5/16.
 */
public class UChat extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        NetworkManager.create(getApplicationContext());

        initPreferences();
    }


    private void initPreferences() {
        HawkBuilder hawk = Hawk.init(this);
        if (BuildConfig.DEBUG) {
            hawk.setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION);
            hawk.setLogLevel(LogLevel.FULL);
        } else {
            hawk.setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM);
            hawk.setLogLevel(LogLevel.NONE);
        }
        hawk.setStorage(HawkBuilder.newSharedPrefStorage(this))
                .build();
    }
}
