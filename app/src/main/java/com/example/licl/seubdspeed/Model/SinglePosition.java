package com.example.licl.seubdspeed.Model;

import com.amap.api.maps.model.LatLng;

/**
 * Created by licl on 2017/2/23.
 */
public class SinglePosition {
    private LatLng position;
    private String time;

    public SinglePosition(LatLng latlng, String t) {
        position=latlng;
        time=t;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
