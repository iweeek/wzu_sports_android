package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 运动数据
 */
public class HistoryData implements Serializable {


    private static final long serialVersionUID = 6187447685293862071L;
    private String sportDesc;//运动描述
    private long time;
    private String speed;//速度
    private int completeCount;//完成段数
    private int minDistance;//最短距离
    private int sportTime;
    private int costEnergy;
    private int sportDistance;
    private boolean qualified;

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }

    public String getSportDesc() {
        return sportDesc;
    }

    public void setSportDesc(String sportDesc) {
        this.sportDesc = sportDesc;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    public int getSportTime() {
        return sportTime;
    }

    public void setSportTime(int sportTime) {
        this.sportTime = sportTime;
    }

    public int getCostEnergy() {
        return costEnergy;
    }

    public void setCostEnergy(int costEnergy) {
        this.costEnergy = costEnergy;
    }

    public int getSportDistance() {
        return sportDistance;
    }

    public void setSportDistance(int distance) {
        sportDistance = distance;
    }
}
