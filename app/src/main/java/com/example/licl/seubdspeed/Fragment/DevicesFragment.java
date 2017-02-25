package com.example.licl.seubdspeed.Fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;
import com.example.licl.seubdspeed.BDAPPlication;
import com.example.licl.seubdspeed.R;

/**
 * 设备地图表 一级页面
 * Created by 李嘉文 on 2017/2/21.
 */
public class DevicesFragment extends android.support.v4.app.Fragment {

    MapView mMapView = null;
    AMap aMap;
    CountDownTimer updateTimer;
    Marker marker1;
    Marker marker2;
    Marker marker3;

    public MapView getaMap(){
        return mMapView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.mapa);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);


        aMap = mMapView.getMap();


        initMapPositions();

        updateTimer=new CountDownTimer(Long.MAX_VALUE,5000){
            @Override
            public void onTick(long l){
                initMapPositions();
            }

            @Override
            public void onFinish(){
                //永不回调的方法
            }
        };
        updateTimer.start();

        return view;
    }

    private void initMapPositions() {
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(BDAPPlication.getInstance().newestPosition.latitude,BDAPPlication.getInstance().newestPosition.longitude),18,30,0));
        aMap.moveCamera(mCameraUpdate);
        LatLng latLng1 = new LatLng(BDAPPlication.getInstance().newestPosition.latitude,BDAPPlication.getInstance().newestPosition.longitude);
        LatLng latLng2 = new LatLng(BDAPPlication.getInstance().newestPosition.latitude+0.000253,BDAPPlication.getInstance().newestPosition.longitude+0.000441);
        LatLng latLng3 = new LatLng(BDAPPlication.getInstance().newestPosition.latitude+0.000596,BDAPPlication.getInstance().newestPosition.longitude+0.000171);
        if (marker1==null||marker2==null||marker3==null){
        marker1 = aMap.addMarker(new MarkerOptions().position(latLng1).title("节点1").snippet("北斗+GPS测速模块"));
        marker2 = aMap.addMarker(new MarkerOptions().position(latLng2).title("节点2").snippet("北斗+GPS测速模块"));
        marker3 = aMap.addMarker(new MarkerOptions().position(latLng3).title("节点3").snippet("北斗+GPS测速模块"));
        }else {
            Animation animation = new RotateAnimation(marker1.getRotateAngle(),marker1.getRotateAngle()+180,0,0,0);
            long duration = 2000L;
            animation.setDuration(duration);
            animation.setInterpolator(new LinearInterpolator());

            marker1.setPosition(latLng1);
            marker2.setPosition(latLng2);
            marker3.setPosition(latLng3);

            marker1.setAnimation(animation);
            marker1.startAnimation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView!=null)
        mMapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //不用恢复重新绘制加载地图，直接全部重绘
        if (mMapView!=null)
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        if (mMapView!=null)
        mMapView.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        if (mMapView!=null)
        mMapView.onSaveInstanceState(outState);
    }
}
