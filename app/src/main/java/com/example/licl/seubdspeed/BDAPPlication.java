package com.example.licl.seubdspeed;

import android.app.Application;

import com.amap.api.maps.model.LatLng;
import com.example.licl.seubdspeed.Model.NodeHistory;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by licl on 2017/2/21.
 */

public class BDAPPlication extends Application {//初始的地图坐标原点

    private static BDAPPlication instance;
    public LatLng newestPosition=new LatLng(118,36);
    public ArrayList<NodeHistory> historyDatas=new ArrayList<>();

    public static BDAPPlication getInstance(){
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
}
