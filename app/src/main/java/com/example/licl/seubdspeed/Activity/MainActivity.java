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
import com.example.licl.seubdspeed.R;
import com.example.licl.seubdspeed.Util.TCPClient;

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


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
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
                            System.out.println("Return Message from Socket::::: >>>>> "+message);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
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

    private void toAbout() {
        if (aboutFrag==null){
            aboutFrag=new AboutFragment();
            fragmentTrs.add(R.id.fl_content,aboutFrag);
        }else {
            fragmentTrs.show(aboutFrag);
        }
    }

    private void toDevices() {
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
}
