package com.example.licl.seubdspeed.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.example.licl.seubdspeed.BDAPPlication;
import com.example.licl.seubdspeed.Model.NodeHistory;
import com.example.licl.seubdspeed.Model.SinglePosition;
import com.example.licl.seubdspeed.Model.Transmition;
import com.example.licl.seubdspeed.R;
import com.example.licl.seubdspeed.Util.TCPClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SpeedActivity extends AppCompatActivity {


    MapView mMapView = null;
    private TCPClient mTcpClient = null;
    private SpeedActivity.connectTask conctTask = null;
    private Button btn_devices;
    private Button btn_history;
    private AMap aMap;
    private String nodeID;
    List<LatLng> latLngs = new ArrayList<LatLng>();
    Polyline polyline;
    private TextView tv_speed;
    private boolean firstCall=true;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (msg.what==0){
                mTcpClient.setHearBeatCountDownTimer(new CountDownTimer(Long.MAX_VALUE,20000){
                    @Override
                    public void onTick(long l){
                        Log.i("logmsg","excited::2::tick");
                        if (mTcpClient!=null)
                            mTcpClient.sendMessage("{\"M\":\"checkin\",\"ID\":\"1442\",\"K\":\"20b14bbaf\"}\n");
                    }

                    @Override
                    public void onFinish() {
                        Log.i("logmsg","excited::2::tick finish");
                    }
                });
            }else if (msg.what==1){
                int v=(int)msg.obj;
                if (v==0){
                    tv_speed.setText("0");
                    return;}
                tv_speed.setText(String.valueOf(v));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        conctTask = new SpeedActivity.connectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        setTitle(R.string.app_name);


        for (int i=0;i<BDAPPlication.getInstance().historyDatas.size();i++){
            if (BDAPPlication.getInstance().historyDatas.get(i).getNodeID().equals(nodeID)){
                ArrayList<SinglePosition> positionAndTime=BDAPPlication.getInstance().historyDatas.get(i).getPositionAndTime();
                for (int j=0;j<positionAndTime.size();j++){
                    latLngs.add(positionAndTime.get(j).getPosition());
                }
            }
        }

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.speedmap);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //if (aMap == null) {
        aMap = mMapView.getMap();
        //}

        //initMapPositions();
        polyline=aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));

        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(BDAPPlication.getInstance().newestPosition.latitude,BDAPPlication.getInstance().newestPosition.longitude),18,30,0));
        aMap.moveCamera(mCameraUpdate);

        tv_speed=(TextView)findViewById(R.id.tvss_speed);
        btn_devices=(Button)findViewById(R.id.btn_nodeinfo);
        btn_history=(Button)findViewById(R.id.btn_nodehistory);

        nodeID=getIntent().getStringExtra("nid");

        tv_speed.setText("...");

    }

    public void update(int v){
        for (int i=0;i<BDAPPlication.getInstance().historyDatas.size();i++){
            if (BDAPPlication.getInstance().historyDatas.get(i).getNodeID().equals(nodeID)){//如果是自己
                ArrayList<SinglePosition> positionAndTime=BDAPPlication.getInstance().historyDatas.get(i).getPositionAndTime();
                int j=positionAndTime.size();
                if (firstCall){//首次调用的时候多加两个点，以画出第一条线
                    if (j>2)
                    {
                        latLngs.add(positionAndTime.get(j-3).getPosition());
                        latLngs.add(positionAndTime.get(j-2).getPosition());
                    }
                    firstCall=false;
                }
                latLngs.add(positionAndTime.get(j-1).getPosition());//将最后一组经纬度加入数据集
            }
        }
        int size=latLngs.size();
        if (size<2)
            return;
        List<LatLng> newLine = new ArrayList<LatLng>();//将最新的两个点绘制为线段
        newLine.add(latLngs.get(size-2));
        newLine.add(latLngs.get(size-1));
        //添加一条线段
        aMap.addPolyline(new PolylineOptions().addAll(newLine).width(10).color(Color.argb(255, 1, 1, 1)));
        //更新速度选项
        int updatev=0;
        if (v>1)
            updatev=v;
        else
            updatev=0;
        Message msg=new Message();
        msg.obj=updatev;
        msg.what=1;
        handler.sendMessage(msg);
        //更新地图camera
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(BDAPPlication.getInstance().newestPosition.latitude,BDAPPlication.getInstance().newestPosition.longitude),18,30,0));
        aMap.moveCamera(mCameraUpdate);
    }

    @Override
    protected void onStop() {
        try
        {
            mTcpClient.sendMessage("{\"M\":\"checkout\",\"ID\":\"1442\",\"K\":\"20b14bbaf\"}\\n");
            mTcpClient.stopClient();
            conctTask.cancel(true);
            conctTask = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {
        @Override
        protected TCPClient doInBackground(String... message)
        {
            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived()
            {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message)
                {
                    try
                    {
                        //this method calls the onProgressUpdate
                        publishProgress(message);
                        if(message!=null)
                        {
                            System.out.println("Recieve from Socket >>>>>>>>>>>>>> "+message);
                            Gson gson=new Gson();
                            Transmition data=gson.fromJson(message,Transmition.class);
                            String latlng0=data.getC();
                            if (data.getM().equals(com.example.licl.seubdspeed.Util.Status.SAY))
                            {
                                String[] arr=latlng0.split("-");
                                Double lat=Double.valueOf(arr[1]);
                                Double lng=Double.valueOf(arr[0]);
                                LatLng latlng=new LatLng(lat,lng);
                                BDAPPlication.getInstance().newestPosition =latlng;
                                ArrayList<NodeHistory> historyDatas=BDAPPlication.getInstance().historyDatas;
                                if (historyDatas.size()==0){//没有历史数据，说明肯定是第一次收到，直接添加一个节点
                                    SinglePosition position=new SinglePosition(latlng,data.getT());
                                    ArrayList<SinglePosition> arry=new ArrayList<>();
                                    arry.add(position);
                                    historyDatas.add(new NodeHistory(arry,data.getID()));
                                    return;
                                }
                                for (int i=0;i<historyDatas.size();i++){//有历史数据，从历史数据中找到对应节点来添加
                                    if (historyDatas.get(i).getNodeID().equals(data.getID())){//如果ID相等说明是这个节点的数据
                                        SinglePosition position=new SinglePosition(latlng,data.getT());
                                        historyDatas.get(i).getPositionAndTime().add(position);//按顺序加入
                                        int size=historyDatas.get(i).getPositionAndTime().size();
                                        SinglePosition oldposition= historyDatas.get(i).getPositionAndTime().get(size-2);
                                        SinglePosition newposition= historyDatas.get(i).getPositionAndTime().get(size-1);
                                        int timetime=Integer.valueOf(newposition.getTime())-Integer.valueOf(oldposition.getTime());
                                        float distance = AMapUtils.calculateLineDistance(oldposition.getPosition(),newposition.getPosition());
                                        int v= (int) (distance/timetime);
                                        historyDatas.get(i).getSpeed().add(v);
                                        update(v);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            //链接建立后通知主线程设置心跳包，之后client会开始发送心跳包
            handler.sendEmptyMessage(0);
            mTcpClient.run();
            if(mTcpClient!=null)
            {
                mTcpClient.sendMessage("{\"M\":\"checkin\",\"ID\":\"1442\",\"K\":\"20b14bbaf\"}\n");
                update(0);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed(){//返回的时候直接销毁，不保存在后台，并直接新建Main
        Intent intnt=new Intent(SpeedActivity.this,MainActivity.class);
        startActivity(intnt);
        finish();
    }

}
