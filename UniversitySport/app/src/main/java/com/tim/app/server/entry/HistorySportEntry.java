package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 运动数据
 */
public class HistorySportEntry implements Serializable {
    private static final long serialVersionUID = 6187447685293862071L;
    private String sportName;//运动描述
    private long startTime;
    private String speed;//速度
    private int completeCount;//完成段数
    private int minDistance;//最短距离
    private int sportTime;
    private int costEnergy;
    private int sportDistance;
    private boolean qualified;
    private  int activityId;

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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
