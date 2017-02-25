package com.example.licl.seubdspeed.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.example.licl.seubdspeed.Adapters.DeviceTransAdapter;
import com.example.licl.seubdspeed.BDAPPlication;
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


@SuppressLint("NewApi")
public class DeviceTransActivity extends Activity
{
    private ListView mList;
    private ArrayList<String> arrayList;
    private DeviceTransAdapter mAdapter;
    private TCPClient mTcpClient = null;
    private connectTask conctTask = null;
    private String deviceID;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (msg.what==0){
                if (mTcpClient==null)
                    return;
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
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        arrayList = new ArrayList<String>();
 
        final EditText editText = (EditText) findViewById(R.id.editText);
        Button send = (Button)findViewById(R.id.send_button);
        //editText.setText("{\"M\":\"checkin\",\"ID\":\"1442\",\"K\":\"20b14bbaf\"}\n");
        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.list);
        mAdapter = new DeviceTransAdapter(this, arrayList);
        mList.setAdapter(mAdapter);
 
        mTcpClient = null;
        // connect to the server
        conctTask = new connectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        deviceID=getIntent().getStringExtra("id");
        
        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String message = editText.getText().toString();
                //add the text in the arrayList
                arrayList.add("发送: " + message);
                //sends the message to the server
                if (mTcpClient != null) 
                {
                    mTcpClient.sendMessage("{\"M\":\"say\",\"ID\":\""+deviceID+"\",\"C\":\""+message+"\",\"SIGN\":\"chat\"}\\n");
                }
                //refresh the list
                mAdapter.notifyDataSetChanged();
                //editText.setText("");
            }
        });
    }
    
    /**
     * receive the message from server with asyncTask  
     * */
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
                			System.out.println("Return Message from Socket::::: >>>>> "+message);
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
 
        @Override
        protected void onProgressUpdate(String... values){
            super.onProgressUpdate(values);

            Gson gson=new Gson();
            Transmition data=gson.fromJson(values[0],Transmition.class);
            String latlng0=data.getC();
            String[] arr = new String[0];
            if (latlng0!=null)
                arr=latlng0.split("-");
 
            if (data.getM().equals(com.example.licl.seubdspeed.Util.Status.SAY)){
                if (arr.length>1)
                    arrayList.add("收到位置: 经度"+arr[1]+"  纬度"+arr[0]);
                else
                    arrayList.add("收到信息: "+latlng0);
                LatLng latlng=new LatLng(Double.valueOf(arr[0]),Double.valueOf(arr[1]));
                ArrayList<NodeHistory> historyDatas= BDAPPlication.getInstance().historyDatas;
                if (historyDatas.size()==0){//没有历史数据，说明肯定是第一次收到，直接添加一个节点
                    SinglePosition position=new SinglePosition(latlng,data.getT());
                    ArrayList<SinglePosition> arry=new ArrayList<>();
                    arry.add(position);
                    historyDatas.add(new NodeHistory(arry,data.getID()));
                    return;
                }
                for (int i=0;i<historyDatas.size();i++){//有历史数据，从历史数据中找到对应节点来添加
                    if (historyDatas.get(i).getNodeID().equals(data.getID())) {//如果ID相等说明是这个节点的数据
                        SinglePosition position=new SinglePosition(latlng,data.getT());
                        historyDatas.get(i).getPositionAndTime().add(position);//按顺序加入
                        int size=historyDatas.get(i).getPositionAndTime().size();
                        SinglePosition oldposition= historyDatas.get(i).getPositionAndTime().get(size-2);
                        SinglePosition newposition= historyDatas.get(i).getPositionAndTime().get(size-1);
                        int timetime=Integer.valueOf(newposition.getTime())-Integer.valueOf(oldposition.getTime());
                        float distance = AMapUtils.calculateLineDistance(oldposition.getPosition(),newposition.getPosition());
                        int v= (int) (distance/timetime);
                        historyDatas.get(i).getSpeed().add(v);
                    }
                }

            }
            else if (data.getM().equals(com.example.licl.seubdspeed.Util.Status.CHECKIN))
                return;
            else if (data.getM().equals(com.example.licl.seubdspeed.Util.Status.WELCOME))
                arrayList.add("链接已经建立，您现在可以与设备对话");
            
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy()
    {
    	try
		{
    		System.out.println("onDestroy.");
			mTcpClient.sendMessage("bye");
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
    protected void onResume() {
        super.onResume();
        mTcpClient = null;
        // connect to the server
        conctTask = new DeviceTransActivity.connectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    public void onBackPressed() {//返回的时候直接销毁，不保存在后台
        this.onStop();
        this.onDestroy();
        super.onBackPressed();
    }
}