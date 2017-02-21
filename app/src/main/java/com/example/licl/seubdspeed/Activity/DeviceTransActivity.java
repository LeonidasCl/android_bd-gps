package com.example.licl.seubdspeed.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.licl.seubdspeed.Adapters.DeviceTransAdapter;
import com.example.licl.seubdspeed.R;
import com.example.licl.seubdspeed.Util.TCPClient;

import java.util.ArrayList;


@SuppressLint("NewApi")
public class DeviceTransActivity extends Activity
{
    private ListView mList;
    private ArrayList<String> arrayList;
    private DeviceTransAdapter mAdapter;
    private TCPClient mTcpClient = null;
    private connectTask conctTask = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        arrayList = new ArrayList<String>();
 
        final EditText editText = (EditText) findViewById(R.id.editText);
        Button send = (Button)findViewById(R.id.send_button);
        editText.setText("{\"M\":\"checkin\",\"ID\":\"1442\",\"K\":\"20b14bbaf\"}\n");
        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.list);
        mAdapter = new DeviceTransAdapter(this, arrayList);
        mList.setAdapter(mAdapter);
 
        mTcpClient = null;
        // connect to the server
        conctTask = new connectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                //add the text in the arrayList
                arrayList.add("客户端: " + message);
                //sends the message to the server
                if (mTcpClient != null) 
                {
                    mTcpClient.sendMessage(message);
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
            /*mTcpClient = new TCPClient(new TCPClient.OnMessageReceived()
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
            mTcpClient.run();
            if(mTcpClient!=null)
            {
            	mTcpClient.sendMessage("Initial Message when connected with Socket Server");
            }
            return null;*/
            mTcpClient=TCPClient.getInstance();
            mTcpClient.setHearBeatCountDownTimer(new CountDownTimer(Long.MAX_VALUE,10000) {
                @Override
                public void onTick(long l){
                    Log.i("logmsg","excited::2::tick");
                    if (mTcpClient!=null)
                        mTcpClient.sendCheckin("1442","20b14bbaf");
                }

                @Override
                public void onFinish() {
                    Log.i("logmsg","excited::2::tick finish");
                }
            });
            mTcpClient.setListener(new TCPClient.OnMessageReceived(){
                @Override
                public void messageReceived(String message){
                    Log.i("logmsg","excited::2");
                }
            });
            return null;
        }
 
        @Override
        protected void onProgressUpdate(String... values){
            super.onProgressUpdate(values);
 
            //in the arrayList we add the messaged received from server
            arrayList.add(values[0]);
            
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
}