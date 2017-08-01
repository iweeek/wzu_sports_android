package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 运动数据
 */
public class HistoryRunningSportEntry implements Serializable {
    private static final long serialVersionUID = 6187447685293862071L;
    private int runningSportId;
    private int costTime;
    private int distance;
    private int kcalConsumed;
    private boolean qualified;
    private long startTime;
    private long endAt;
    private String sportDate;
    private String speed;//速度
    private String sportName;//运动名字  快走、随机慢跑

    public int getRunningSportId() {
        return runningSportId;
    }

    public void setRunningSportId(int runningSportId) {
        this.runningSportId = runningSportId;
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

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public int getKcalConsumed() {
        return kcalConsumed;
    }

    public void setKcalConsumed(int kcalConsumed) {
        this.kcalConsumed = kcalConsumed;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public long getEndAt() {
        return endAt;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
    }

    public String getSportDate() {
        return sportDate;
    }

    public void setSportDate(String sportDate) {
        this.sportDate = sportDate;
    }

    @Override
    public String toString() {
        return "HistoryRunningSportEntry{" +
                "runningSportId=" + runningSportId +
                ", costTime=" + costTime +
                ", distance=" + distance +
                ", kcalConsumed=" + kcalConsumed +
                ", qualified=" + qualified +
                ", startTime=" + startTime +
                ", endAt=" + endAt +
                ", sportDate='" + sportDate + '\'' +
                ", speed='" + speed + '\'' +
                ", sportName='" + sportName + '\'' +
                '}';
    }
}
