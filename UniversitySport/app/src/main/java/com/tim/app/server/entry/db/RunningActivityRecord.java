package com.tim.app.server.entry.db;

/**
 * Created by nimon on 2017/6/17.
 * 数据库记录
 */

public class RunningActivityRecord {

    public long id;
    public long runningSportId;
    public long endRunningSportId;
    public long studentId;
    public int distance;
    public int stepCount;
    public int costTime;
    public long speed;
    public long stepPerSecond;
    public long distancePerStep;
    public int targetFinishedTime;
    public int startTime;
    public int kcalConsumed;
    public int qualified;
    public int isValid;
    public int isVerified;
    public int qualifiedDistance;
    public int qualifiedCostTime;
    public int minCostTime;
    public int createdAt;
    public int updatedAt;
    public int endedAt;
    public int endedBy;

    public RunningActivityRecord() {
    }

    @Override
    public String toString() {
        return "RunningActivityRecord{" +
                "id=" + id +
                ", runningSportId=" + runningSportId +
                ", endRunningSportId=" + endRunningSportId +
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
                ", isValid=" + isValid +
                ", isVerified=" + isVerified +
                ", qualifiedDistance=" + qualifiedDistance +
                ", qualifiedCostTime=" + qualifiedCostTime +
                ", minCostTime=" + minCostTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", endedAt=" + endedAt +
                ", endedBy=" + endedBy +
                '}';
    }

    public RunningActivityRecord(long id, long runningSportId, long endRunningSportId, long studentId, int distance, int stepCount, int costTime, long speed, long stepPerSecond, long distancePerStep, int targetFinishedTime, int startTime, int kcalConsumed, int qualified, int isValid, int isVerified, int qualifiedDistance, int qualifiedCostTime, int minCostTime, int createdAt, int updatedAt, int endedAt, int endedBy) {
        this.id = id;
        this.runningSportId = runningSportId;
        this.endRunningSportId = endRunningSportId;
        this.studentId = studentId;
        this.distance = distance;
        this.stepCount = stepCount;
        this.costTime = costTime;
        this.speed = speed;
        this.stepPerSecond = stepPerSecond;
        this.distancePerStep = distancePerStep;
        this.targetFinishedTime = targetFinishedTime;
        this.startTime = startTime;
        this.kcalConsumed = kcalConsumed;
        this.qualified = qualified;
        this.isValid = isValid;
        this.isVerified = isVerified;
        this.qualifiedDistance = qualifiedDistance;
        this.qualifiedCostTime = qualifiedCostTime;
        this.minCostTime = minCostTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.endedAt = endedAt;
        this.endedBy = endedBy;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRunningSportId() {
        return runningSportId;
    }

    public void setRunningSportId(Long runningSportId) {
        this.runningSportId = runningSportId;
    }

    public Long getEndRunningSportId() {
        return endRunningSportId;
    }

    public void setEndRunningSportId(Long endRunningSportId) {
        this.endRunningSportId = endRunningSportId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
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

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public Long getStepPerSecond() {
        return stepPerSecond;
    }

    public void setStepPerSecond(Long stepPerSecond) {
        this.stepPerSecond = stepPerSecond;
    }

    public Long getDistancePerStep() {
        return distancePerStep;
    }

    public void setDistancePerStep(Long distancePerStep) {
        this.distancePerStep = distancePerStep;
    }

    public int getTargetFinishedTime() {
        return targetFinishedTime;
    }

    public void setTargetFinishedTime(int targetFinishedTime) {
        this.targetFinishedTime = targetFinishedTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getKcalConsumed() {
        return kcalConsumed;
    }

    public void setKcalConsumed(int kcalConsumed) {
        this.kcalConsumed = kcalConsumed;
    }

    public int getQualified() {
        return qualified;
    }

    public void setQualified(int qualified) {
        this.qualified = qualified;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public int getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
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

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(int endedAt) {
        this.endedAt = endedAt;
    }

    public int getEndedBy() {
        return endedBy;
    }

    public void setEndedBy(int endedBy) {
        this.endedBy = endedBy;
    }
}
