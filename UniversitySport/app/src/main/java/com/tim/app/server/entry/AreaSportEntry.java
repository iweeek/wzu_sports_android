package com.tim.app.server.entry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @创建者 倪军
 * @创建时间 2017/7/31
 * @描述 区域定点运动运动项目实体类
 */


public class AreaSportEntry implements Parcelable {
    private static final long serialVersionUID = 6187447685293862071L;
    private int id;//运动项目Id
    private String name;//名称
    private boolean isEnable;//项目是否启用
    private int qualifiedCostTime;//合格时间
    private int acquisitionInterval;//采集运动数据的时间间隔
    private int universityId;//大学ID
    private  int participantNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public int getQualifiedCostTime() {
        return qualifiedCostTime;
    }

    public void setQualifiedCostTime(int qualifiedCostTime) {
        this.qualifiedCostTime = qualifiedCostTime;
    }

    public int getAcquisitionInterval() {
        return acquisitionInterval;
    }

    public void setAcquisitionInterval(int acquisitionInterval) {
        this.acquisitionInterval = acquisitionInterval;
    }

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public int getParticipantNum() {
        return participantNum;
    }

    public void setParticipantNum(int participantNum) {
        this.participantNum = participantNum;
    }

    @Override
    public String toString() {
        return "AreaSportEntry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isEnable=" + isEnable +
                ", qualifiedCostTime=" + qualifiedCostTime +
                ", acquisitionInterval=" + acquisitionInterval +
                ", universityId=" + universityId +
                ", participantNum=" + participantNum +
                '}';
    }

    public AreaSportEntry() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeByte(this.isEnable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.qualifiedCostTime);
        dest.writeInt(this.acquisitionInterval);
        dest.writeInt(this.universityId);
    }

    protected AreaSportEntry(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.isEnable = in.readByte() != 0;
        this.qualifiedCostTime = in.readInt();
        this.acquisitionInterval = in.readInt();
        this.universityId = in.readInt();
    }

    public static final Parcelable.Creator<AreaSportEntry> CREATOR = new Parcelable.Creator<AreaSportEntry>() {
        @Override
        public AreaSportEntry createFromParcel(Parcel source) {
            return new AreaSportEntry(source);
        }

        @Override
        public AreaSportEntry[] newArray(int size) {
            return new AreaSportEntry[size];
        }
    };
}
