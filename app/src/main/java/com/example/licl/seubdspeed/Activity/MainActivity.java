package com.example.licl.seubdspeed.Activity;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.example.licl.seubdspeed.BDAPPlication;
import com.example.licl.seubdspeed.Fragment.AboutFragment;
import com.example.licl.seubdspeed.Fragment.DevicesFragment;
import com.example.licl.seubdspeed.Fragment.MainFragment;
import com.example.licl.seubdspeed.Fragment.SpeedFragment;
import com.example.licl.seubdspeed.Model.NodeHistory;
import com.example.licl.seubdspeed.Model.SinglePosition;
import com.example.licl.seubdspeed.Model.Transmition;
import com.example.licl.seubdspeed.R;
import com.example.licl.seubdspeed.Util.Status;
import com.example.licl.seubdspeed.Util.TCPClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton main;
    private ImageButton devices;
    private ImageButton about;
    private FragmentManager fragmentMgr;
    private FragmentTransaction fragmentTrs;
    private Fragment mainFrag;
    private Fragment devicesFrag;
    private Fragment aboutFrag;
    private TCPClient mTcpClient = null;
    private connectTask conctTask = null;
    private SpeedFragment speedFrag;
    private boolean hasSpeedFrag=false;


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (msg.what==0){
                mTcpClient.setHearBeatCountDownTimer(new CountDownTimer(Long.MAX_VALUE,30000){
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
            }
        }
    };
    public class connectTask extends AsyncTask<String,String,TCPClient>{
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
                                        if (hasSpeedFrag&&speedFrag.getNodeID().equals(data.getID())){//如果此时显示的是测速仪表盘，更新速度和路径线
                                            speedFrag.update(v);
                                        }
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
                mTcpClient.sendMessage("Initial Message when connected with Socket Server");
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main=(ImageButton)findViewById(R.id.button_main);
        devices=(ImageButton)findViewById(R.id.button_devices);
        about=(ImageButton)findViewById(R.id.button_about);
        main.setOnClickListener(this);
        devices.setOnClickListener(this);
        about.setOnClickListener(this);
        fragmentMgr=getSupportFragmentManager();
        main.performClick();

        mTcpClient = null;
        // connect to the server
        conctTask = new connectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        setTitle(R.string.app_name);
        fragmentTrs=fragmentMgr.beginTransaction();
        setSelected();
        main.setSelected(true);
        toMain();
        fragmentTrs.commit();
    }

    @Override
    public void onClick(View view) {

        fragmentTrs=fragmentMgr.beginTransaction();
        setSelected();
        switch (view.getId()) {
            case R.id.button_main:
                main.setSelected(true);
                toMain();
                break;
            case R.id.button_devices:
                devices.setSelected(true);
                toDevices();
                break;
            case R.id.button_about:
                about.setSelected(true);
                toAbout();
                break;
        }
        fragmentTrs.commit();

    }

    public void toSpeed(String nodeID){//切换到速度仪表盘
        fragmentTrs=fragmentMgr.beginTransaction();
        if(mainFrag != null){
            fragmentTrs.hide(mainFrag);
        }
        if(devicesFrag != null){
            fragmentTrs.hide(devicesFrag);
        }
        if(aboutFrag != null){
            fragmentTrs.hide(aboutFrag);
        }
        speedFrag = new SpeedFragment();
        speedFrag.setNodeID(nodeID);
        fragmentTrs.add(R.id.fl_content,speedFrag);
        fragmentTrs.show(speedFrag);
        fragmentTrs.commit();
        hasSpeedFrag=true;
    }

    private void toAbout(){
        if (aboutFrag==null){
            aboutFrag=new AboutFragment();
            fragmentTrs.add(R.id.fl_content,aboutFrag);
        }else{
            fragmentTrs.show(aboutFrag);
        }
    }

    private void toDevices(){
        if (devicesFrag==null){
            devicesFrag=new DevicesFragment();
            fragmentTrs.add(R.id.fl_content,devicesFrag);
        }else {
            fragmentTrs.show(devicesFrag);
        }
    }

    private void toMain() {
        //BDAPPlication.getInstance().getmTcpClient().sendCheckin("1442","20b14bbaf");
        if(mainFrag == null){
            mainFrag = new MainFragment();
            fragmentTrs.add(R.id.fl_content,mainFrag);
        }else{
            fragmentTrs.show(mainFrag);
        }
    }

    private void setSelected(){
        main.setSelected(false);
        devices.setSelected(false);
        about.setSelected(false);
        if(mainFrag != null){
            fragmentTrs.hide(mainFrag);
        }
        if(devicesFrag != null){
            fragmentTrs.hide(devicesFrag);
        }
        if(aboutFrag != null){
            fragmentTrs.hide(aboutFrag);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mTcpClient = null;
        // connect to the server
        conctTask = new connectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onStop(){
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
    protected void onDestroy()
    {
        try
        {
            System.out.println("onDestroy.");
            mTcpClient.sendMessage("exit");
            mTcpClient.stopClient();
            conctTask.cancel(true);
            conctTask = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if (hasSpeedFrag){
            //TODO 如果有测速面板 按返回键只是关掉测速面板 如果没有测速面板 按返回键两次会退出应用
        }
    }
}
