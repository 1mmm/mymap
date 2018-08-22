package com.example.wanglei.mymap;

/**
 * Created by 2mmm on 2018/1/27.
 */

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

public class makerInfo implements Serializable{


    private double lng,lat;
    private String content;
    private String id,tid;
    private LatLng latlng;
    public makerInfo()
    {

    }
    public makerInfo(double lng, double lat, String content,String id,String tid) {
        this.lng = lng;
        this.lat = lat;
        this.content = content;
        this.id=id;
        this.tid=tid;
        latlng=new LatLng(lat, lng);
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public String getid(){return id;}
    public String getContent() {
        return content;
    }
    public String gettid() {
        return tid;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LatLng getLatlng() {
        return latlng;
    }
    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

}