package com.tim.app.server.entry;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * 运动区域列表项
 */
public class FixLocationOutdoorSportPoint implements  Parcelable {

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
                ", areaName='" + areaName + '\'' +
                ", isEnabled=" + isEnabled +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", qualifiedCostTime=" + qualifiedCostTime +
                ", radius=" + radius +
                ", universityId=" + universityId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.areaName);
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.qualifiedCostTime);
        dest.writeDouble(this.radius);
        dest.writeInt(this.universityId);
    }

    public FixLocationOutdoorSportPoint() {
    }

    protected FixLocationOutdoorSportPoint(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.areaName = in.readString();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.qualifiedCostTime = in.readInt();
        this.radius = in.readDouble();
        this.universityId = in.readInt();
    }

    public static final Parcelable.Creator<FixLocationOutdoorSportPoint> CREATOR = new Parcelable.Creator<FixLocationOutdoorSportPoint>() {
        @Override
        public FixLocationOutdoorSportPoint createFromParcel(Parcel source) {
            return new FixLocationOutdoorSportPoint(source);
        }

        @Override
        public FixLocationOutdoorSportPoint[] newArray(int size) {
            return new FixLocationOutdoorSportPoint[size];
        }
    };
}
