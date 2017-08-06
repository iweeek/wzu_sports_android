package com.tim.app.server.entry;

import java.io.Serializable;

/**
 * @创建者 倪军
 * @创建时间 2017/8/2
 * @描述
 */

public class HistorySportEntry  implements Serializable{

    public static final int RUNNING_TYPE = 1;
    public static final int AREA_TYPE = 2;

    /**
     * 记录的Id
     * @return
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;
    private static final long serialVersionUID = 6187447685293862071L;
    private int sportId;
    private int costTime;
    private int kcalConsumed;
    private boolean qualified;
    private long startTime;
    private long endAt;
    private String sportDate;
    private String sportName;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;//类型：1，跑步运动；2，区域运动。

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    /**
     * 这个运动项目的Id,首页的运动项目
     * @return
     */
    public int getSportId() {
        return sportId;
    }

    public void setSportId(int sportId) {
        this.sportId = sportId;
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

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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
        return "HistorySportEntry{" +
                "sportId=" + sportId +
                ", costTime=" + costTime +
                ", kcalConsumed=" + kcalConsumed +
                ", qualified=" + qualified +
                ", startTime=" + startTime +
                ", endAt=" + endAt +
                ", sportDate='" + sportDate + '\'' +
                ", sportName='" + sportName + '\'' +
                '}';
    }
}
