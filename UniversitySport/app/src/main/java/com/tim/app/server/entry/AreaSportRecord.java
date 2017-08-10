package com.tim.app.server.entry;

import java.io.Serializable;

/**
 * @创建者 倪军
 * @创建时间 2017/8/10
 * @描述
 */

public class AreaSportRecord implements Serializable{

    private static final long serialVersionUID = 6187447685293862071L;

    /**
     * id : 256
     * areaSportId : 5
     * studentId : 4942
     * costTime : 1
     * startTime : 1502330084000
     * kcalConsumed : 0
     * qualified : false
     * qualifiedCostTime : 3600
     * createdAt : 1502330084000
     * updatedAt : 1502330084000
     * endedAt : 1502330085995
     * endedBy : false
     */

    private int id;
    private int areaSportId;
    private int studentId;
    private int costTime;
    private long startTime;
    private int kcalConsumed;
    private boolean qualified;
    private int qualifiedCostTime;
    private long createdAt;
    private long updatedAt;
    private long endedAt;
    private boolean endedBy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAreaSportId() {
        return areaSportId;
    }

    public void setAreaSportId(int areaSportId) {
        this.areaSportId = areaSportId;
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

    public boolean isEndedBy() {
        return endedBy;
    }

    public void setEndedBy(boolean endedBy) {
        this.endedBy = endedBy;
    }
}
