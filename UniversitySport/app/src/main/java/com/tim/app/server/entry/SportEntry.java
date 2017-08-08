package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 首页的运动对象
 */
public class SportEntry implements Serializable {
    private static final long serialVersionUID = 6187447685293862071L;
    public   static final  int AREA_SPORT = 1;
    public static  final  int RUNNING_SPORT = 2;

    private String sportName;//名称
    private int participantNum;//参加人数
    private int targetDistance;//目标距离
    private String targetSpeed;//目标速度
    private int targetTime;//目标时间
    private int steps;//目标步数
    private String bgUrl;//背景图片地址
    private int bgDrawableId;//背景图片id
    private int type;//运动方式
    private int id = 1;//运动id，默认值为1
    private int acquisitionInterval;

    public int getAcquisitionInterval() {
        return acquisitionInterval;
    }

    public void setAcquisitionInterval(int acquisitionInterval) {
        this.acquisitionInterval = acquisitionInterval;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public int getParticipantNum() {
        return participantNum;
    }

    public void setParticipantNum(int participantNum) {
        this.participantNum = participantNum;
    }
    public int getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(int targetDistance) {
        this.targetDistance = targetDistance;
    }

    public String getTargetSpeed() {
        return targetSpeed;
    }

    public void setTargetSpeed(String targetSpeed) {
        this.targetSpeed = targetSpeed;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
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
                "sportName='" + sportName + '\'' +
                ", participantNum=" + participantNum +
                ", targetDistance=" + targetDistance +
                ", targetSpeed='" + targetSpeed + '\'' +
                ", targetTime=" + targetTime +
                ", steps=" + steps +
                ", bgUrl='" + bgUrl + '\'' +
                ", bgDrawableId=" + bgDrawableId +
                ", type=" + type +
                ", id=" + id +
                ", acquisitionInterval=" + acquisitionInterval +
                '}';
    }
}
