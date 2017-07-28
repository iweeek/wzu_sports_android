package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 运动区域
 */
public class SportArea implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;
    private  Integer id;
    private String areaName;//地点描述
    private String address;//地址
    private double latitude; //纬度
    private double longitude;//经度
    private int targetTime;//达标时间
    private double radius;//半径
    private boolean isSelected;//是否选中

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
