package com.example.licl.seubdspeed.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.licl.seubdspeed.R;

/**
 * 关于 一级页面
 * Created by 李嘉文 on 2017/2/20.
 */
public class AboutFragment extends android.support.v4.app.Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        return view;
    }
}
