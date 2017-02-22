package com.example.licl.seubdspeed.Model;

/**
 * Created by ekansh on 19/10/15.
 */
public class Node {

    private String name;
    private String desc;
    private String id;
    private boolean isOnline;

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
