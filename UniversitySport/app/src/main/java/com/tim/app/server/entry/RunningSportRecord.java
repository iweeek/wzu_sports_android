package com.tim.app.server.entry;

import java.io.Serializable;

/**
 * @创建者 倪军
 * @创建时间 2017/8/10
 * @描述
 */

public class RunningSportRecord implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;


    /**
     * id : 274
     * runningSportId : 1
     * studentId : 1
     * distance : 24
     * stepCount : 0
     * costTime : 2
     * speed : 12
     * stepPerSecond : 0
     * distancePerStep : 0
     * targetFinishedTime : 0
     * startTime : 1502331628000
     * kcalConsumed : 0
     * qualified : false
     * qualifiedDistance : 2000
     * qualifiedCostTime : 2400
     * minCostTime : 0
     * createdAt : null
     * updatedAt : null
     * endedAt : 1502331629632
     * endedBy : null
     */

    private int id;
    private int runningSportId;
    private int studentId;
    private int distance;
    private int stepCount;
    private int costTime;
    private int speed;
    private int stepPerSecond;
    private int distancePerStep;
    private int targetFinishedTime;
    private long startTime;
    private int kcalConsumed;
    private boolean qualified;
    private int qualifiedDistance;
    private int qualifiedCostTime;
    private int minCostTime;
    private long createdAt;
    private long updatedAt;
    private long endedAt;
    private boolean endedBy;

    @Override
    public String toString() {
        return "RunningSportRecord{" +
                "id=" + id +
                ", runningSportId=" + runningSportId +
                ", studentId=" + studentId +
                ", distance=" + distance +
                ", stepCount=" + stepCount +
                ", costTime=" + costTime +
                ", speed=" + speed +
                ", stepPerSecond=" + stepPerSecond +
                ", distancePerStep=" + distancePerStep +
                ", targetFinishedTime=" + targetFinishedTime +
                ", startTime=" + startTime +
                ", kcalConsumed=" + kcalConsumed +
                ", qualified=" + qualified +
                ", qualifiedDistance=" + qualifiedDistance +
                ", qualifiedCostTime=" + qualifiedCostTime +
                ", minCostTime=" + minCostTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", endedAt=" + endedAt +
                ", endedBy=" + endedBy +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRunningSportId() {
        return runningSportId;
    }

    public void setRunningSportId(int runningSportId) {
        this.runningSportId = runningSportId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getStepPerSecond() {
        return stepPerSecond;
    }

    public void setStepPerSecond(int stepPerSecond) {
        this.stepPerSecond = stepPerSecond;
    }

    public int getDistancePerStep() {
        return distancePerStep;
    }

    public void setDistancePerStep(int distancePerStep) {
        this.distancePerStep = distancePerStep;
    }

    public int getTargetFinishedTime() {
        return targetFinishedTime;
    }

    public void setTargetFinishedTime(int targetFinishedTime) {
        this.targetFinishedTime = targetFinishedTime;
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

    public int getQualifiedDistance() {
        return qualifiedDistance;
    }

    public void setQualifiedDistance(int qualifiedDistance) {
        this.qualifiedDistance = qualifiedDistance;
    }

    public int getQualifiedCostTime() {
        return qualifiedCostTime;
    }

    public void setQualifiedCostTime(int qualifiedCostTime) {
        this.qualifiedCostTime = qualifiedCostTime;
    }

    public int getMinCostTime() {
        return minCostTime;
    }

    public void setMinCostTime(int minCostTime) {
        this.minCostTime = minCostTime;
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
