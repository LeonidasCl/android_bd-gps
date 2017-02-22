package com.example.licl.seubdspeed.Model;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by licl on 2017/2/23.
 */

public class NodeHistory {
    private ArrayList<SinglePosition> positionAndTime;
    private String nodeID;
    private ArrayList<Integer> speed=new ArrayList<>();

    public NodeHistory(ArrayList<SinglePosition> positionAndTime,String nodeID){
        this.positionAndTime=positionAndTime;
        this.nodeID=nodeID;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public ArrayList<SinglePosition> getPositionAndTime() {
        return positionAndTime;
    }

    public void setPositionAndTime(ArrayList<SinglePosition> positionAndTime) {
        this.positionAndTime = positionAndTime;
    }

    public ArrayList<Integer> getSpeed() {
        return speed;
    }

    public void setSpeed(ArrayList<Integer> speed) {
        this.speed = speed;
    }
}
