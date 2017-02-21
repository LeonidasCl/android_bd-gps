package com.example.licl.seubdspeed;

import android.app.Application;

/**
 * Created by licl on 2017/2/21.
 */

public class BDAPPlication extends Application {

    private static BDAPPlication instance;

    public static BDAPPlication getInstance(){
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
}
