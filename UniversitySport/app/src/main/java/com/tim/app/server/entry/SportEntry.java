package com.tim.app.server.entry;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * 首页的运动对象
 */
public class SportEntry implements Parcelable {
    private static final long serialVersionUID = 6187447685293862071L;
    public static final int AREA_SPORT = 1;
    public static final int RUNNING_SPORT = 2;

    private int id = 1;//运动id，默认值为1
    private String name;//名称
    private int qualifiedCostTime;//目标时间
    private int acquisitionInterval;
    private int participantNum;//参加人数
    private int qualifiedDistance;//目标距离
    private float targetSpeed;//目标速度
    private String imgUrl;//背景图片地址
    private int bgDrawableId;//背景图片id
    private int type;//运动方式

    public int getAcquisitionInterval() {
        return acquisitionInterval;
    }

    public void setAcquisitionInterval(int acquisitionInterval) {
        this.acquisitionInterval = acquisitionInterval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParticipantNum() {
        return participantNum;
    }

    public void setParticipantNum(int participantNum) {
        this.participantNum = participantNum;
    }

    public int getQualifiedDistance() {
        return qualifiedDistance;
    }

    public void setQualifiedDistance(int qualifiedDistance) {
        this.qualifiedDistance = qualifiedDistance;
    }

    public float getTargetSpeed() {
        return targetSpeed;
    }

    public void setTargetSpeed(float targetSpeed) {
        this.targetSpeed = targetSpeed;
    }

    public int getTargetTime() {
        return qualifiedCostTime;
    }

    public void setTargetTime(int targetTime) {
        this.qualifiedCostTime = targetTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getBgDrawableId() {
        return bgDrawableId;
    }

    public void setBgDrawableId(int bgDrawableId) {
        this.bgDrawableId = bgDrawableId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SportEntry{" +
                "name='" + name + '\'' +
                ", participantNum=" + participantNum +
                ", qualifiedDistance=" + qualifiedDistance +
                ", targetSpeed='" + targetSpeed + '\'' +
                ", qualifiedCostTime=" + qualifiedCostTime +
                ", imgUrl='" + imgUrl + '\'' +
                ", bgDrawableId=" + bgDrawableId +
                ", type=" + type +
                ", id=" + id +
                ", acquisitionInterval=" + acquisitionInterval +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.qualifiedCostTime);
        dest.writeInt(this.acquisitionInterval);
        dest.writeInt(this.participantNum);
        dest.writeInt(this.qualifiedDistance);
        dest.writeFloat(this.targetSpeed);
        dest.writeString(this.imgUrl);
        dest.writeInt(this.bgDrawableId);
        dest.writeInt(this.type);
    }

    public SportEntry() {
    }

    protected SportEntry(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.qualifiedCostTime = in.readInt();
        this.acquisitionInterval = in.readInt();
        this.participantNum = in.readInt();
        this.qualifiedDistance = in.readInt();
        this.targetSpeed = in.readFloat();
        this.imgUrl = in.readString();
        this.bgDrawableId = in.readInt();
        this.type = in.readInt();
    }

    public static final Creator<SportEntry> CREATOR = new Creator<SportEntry>() {
        @Override
        public SportEntry createFromParcel(Parcel source) {
            return new SportEntry(source);
        }

        @Override
        public SportEntry[] newArray(int size) {
            return new SportEntry[size];
        }
    };
}
