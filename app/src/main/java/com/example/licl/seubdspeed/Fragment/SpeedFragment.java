package com.example.licl.seubdspeed.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;
import com.example.licl.seubdspeed.BDAPPlication;
import com.example.licl.seubdspeed.Model.SinglePosition;
import com.example.licl.seubdspeed.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备速度表 一级页面
 * Created by 李嘉文 on 2017/2/21.
 */
public class SpeedFragment extends android.support.v4.app.Fragment {

    private MapView mMapView = null;
    private AMap aMap;
    private String nodeID;
    List<LatLng> latLngs = new ArrayList<LatLng>();
    Polyline polyline;
    private TextView tv_speed;
    private Button btn_devices;
    private Button btn_history;

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
        View view = inflater.inflate(R.layout.fragment_speed, container, false);

        for (int i=0;i<BDAPPlication.getInstance().historyDatas.size();i++){
            if (BDAPPlication.getInstance().historyDatas.get(i).getNodeID().equals(nodeID)){
            ArrayList<SinglePosition> positionAndTime=BDAPPlication.getInstance().historyDatas.get(i).getPositionAndTime();
                for (int j=0;j<positionAndTime.size();j++){
                    latLngs.add(positionAndTime.get(j).getPosition());
                }
            }
        }

        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.mapb);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //if (aMap == null) {
            aMap = mMapView.getMap();
        //}

        //initMapPositions();
        polyline=aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));

        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(BDAPPlication.getInstance().newestPosition.latitude,BDAPPlication.getInstance().newestPosition.longitude),18,30,0));
        aMap.moveCamera(mCameraUpdate);

        tv_speed=(TextView)view.findViewById(R.id.tv_speed);
        btn_devices=(Button)view.findViewById(R.id.btn_nodeinfo);
        btn_history=(Button)view.findViewById(R.id.btn_nodehistory);

        return view;
    }

    public void update(int v){
        for (int i=0;i<BDAPPlication.getInstance().historyDatas.size();i++){
            if (BDAPPlication.getInstance().historyDatas.get(i).getNodeID().equals(nodeID)){
                ArrayList<SinglePosition> positionAndTime=BDAPPlication.getInstance().historyDatas.get(i).getPositionAndTime();
                int j=positionAndTime.size();
                latLngs.add(positionAndTime.get(j-1).getPosition());//将最后一组经纬度加入数据集
            }
        }
        int size=latLngs.size();
        List<LatLng> newLine = new ArrayList<LatLng>();//将最新的两个点绘制为线段
        newLine.add(latLngs.get(size-2));
        newLine.add(latLngs.get(size-1));
        //添加一条线段
        aMap.addPolyline(new PolylineOptions().addAll(newLine).width(10).color(Color.argb(255, 1, 1, 1)));
        //更新速度选项
        if (v>1)
            tv_speed.setText(String.valueOf(v));
        else
            tv_speed.setText("0");
        //更新地图camera
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(BDAPPlication.getInstance().newestPosition.latitude,BDAPPlication.getInstance().newestPosition.longitude),18,30,0));
        aMap.moveCamera(mCameraUpdate);
    }


    @Override
    public void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        super.onDestroy();
        if (mMapView!=null)
        mMapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mMapView!=null)
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mMapView!=null)
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView!=null)
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }


}
