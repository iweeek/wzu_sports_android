package com.tim.app.server.entry;

import java.io.Serializable;

/**
 * @创建者 倪军
 * @创建时间 2017/8/2
 * @描述
 */

public class HistorySportEntry implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;

    private long id;
    private int sportId;
    private int studentId;
    private int costTime;
    private long startTime;
    private int kcalConsumed;
    private int distance;
    private boolean qualified;
    private int qualifiedCostTime;
    private long createdAt;
    private long updatedAt;
    private long endedAt;
    private String sportDate;
    private String sportName;
    private String areaName;
    private boolean endedBy;
    private int type;//类型：1，跑步运动；2，区域运动。


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSportId() {
        return sportId;
    }

    public void setSportId(int sportId) {
        this.sportId = sportId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }

    public int getQualifiedCostTime() {
        return qualifiedCostTime;
    }

    public void setQualifiedCostTime(int qualifiedCostTime) {
        this.qualifiedCostTime = qualifiedCostTime;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(long endedAt) {
        this.endedAt = endedAt;
    }

    public String getSportDate() {
        return sportDate;
    }

    public void setSportDate(String sportDate) {
        this.sportDate = sportDate;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public boolean isEndedBy() {
        return endedBy;
    }

    public void setEndedBy(boolean endedBy) {
        this.endedBy = endedBy;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "HistorySportEntry{" +
                "id=" + id +
                ", sportId=" + sportId +
                ", studentId=" + studentId +
                ", costTime=" + costTime +
                ", startTime=" + startTime +
                ", kcalConsumed=" + kcalConsumed +
                ", distance=" + distance +
                ", qualified=" + qualified +
                ", qualifiedCostTime=" + qualifiedCostTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", endedAt=" + endedAt +
                ", sportDate='" + sportDate + '\'' +
                ", sportName='" + sportName + '\'' +
                ", areaName='" + areaName + '\'' +
                ", endedBy=" + endedBy +
                ", type=" + type +
                '}';
    }
}
