package com.tim.app.server.entry;

/**
 * @创建者 倪军
 * @创建时间 2017/8/1
 * @描述
 */

public class HistoryAreaSportEntry {

    private static final long serialVersionUID = 6187447685293862071L;
    private  int areaSportId;  //运动区域的Id
    private int costTime;
    private int kcalConsumed;
    private String areaName;//运动描述
    private long startTime;
    private boolean qualified;


    public int getAreaSportId() {
        return areaSportId;
    }

    public void setAreaSportId(int areaSportId) {
        this.areaSportId = areaSportId;
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

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }

    @Override
    public String toString() {
        return "HistoryAreaSportEntry{" +
                "areaSportId=" + areaSportId +
                ", costTime=" + costTime +
                ", kcalConsumed=" + kcalConsumed +
                ", areaName='" + areaName + '\'' +
                ", startTime=" + startTime +
                ", qualified=" + qualified +
                '}';
    }
}
