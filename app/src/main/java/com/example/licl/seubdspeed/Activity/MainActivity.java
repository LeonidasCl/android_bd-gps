package com.example.licl.seubdspeed.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.example.licl.seubdspeed.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton main;
    private ImageButton devices;
    private ImageButton about;
    private FragmentManager fragmentMgr;
    private FragmentTransaction fragmentTrs;
    private Fragment mainFrag;
    private Fragment devicesFrag;
    private Fragment aboutFrag;

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
        toMain();
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
}
