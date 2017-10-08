package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 运动区域列表项
 */
public class FixLocationOutdoorSportPoint implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;
    private  Integer id;
    private String areaName;//地点描述
    private boolean isEnabled;
    private String address;//地址
    private double latitude; //纬度
    private double longitude;//经度
    private int qualifiedCostTime;//达标时间
    private double radius;//半径
    private int universityId;


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

    public int getQualifiedCostTime() {
        return qualifiedCostTime;
    }

    public void setQualifiedCostTime(int qualifiedCostTime) {
        this.qualifiedCostTime = qualifiedCostTime;
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

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }


    @Override
    public String toString() {
        return "FixLocationOutdoorSportPoint{" +
                "id=" + id +
                ", name='" + areaName + '\'' +
                ", isEnabled=" + isEnabled +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", qualifiedCostTime=" + qualifiedCostTime +
                ", radius=" + radius +
                ", universityId=" + universityId +
                '}';
    }
}
