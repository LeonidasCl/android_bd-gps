package com.example.licl.seubdspeed.Model;

/**
 * Created by ekansh on 19/10/15.
 */
public class Node {

    private String name;
    private String desc;
    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
