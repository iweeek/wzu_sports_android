package com.tim.app.server.entry.db;

/**
 * @创建者 倪军
 * @创建时间 04/12/2017
 * @描述
 */

public class RunningActivityDataRecord {

    public long id;
    public long activityId;
    public int acquisitionTime;
    public int stepCount;
    public int stepCountCal;
    public int distance;
    public long distancePerStep;
    public long stepPerSecond;
    public long longitude;
    public long latitude;
    public int locationType;
    public int isNormal;
    public int createdAt;
    public int updatedAt;

    @Override
    public String toString() {
        return "RunningActivityDataRecord{" +
                "id=" + id +
                ", activityId=" + activityId +
                ", acquisitionTime=" + acquisitionTime +
                ", stepCount=" + stepCount +
                ", stepCountCal=" + stepCountCal +
                ", distance=" + distance +
                ", distancePerStep=" + distancePerStep +
                ", stepPerSecond=" + stepPerSecond +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", locationType=" + locationType +
                ", isNormal=" + isNormal +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public RunningActivityDataRecord() {
    }

    public RunningActivityDataRecord(long id, long activityId, int acquisitionTime, int stepCount, int stepCountCal, int distance, long distancePerStep, long stepPerSecond, long longitude, long latitude, int locationType, int isNormal, int createdAt, int updatedAt) {
        this.id = id;
        this.activityId = activityId;
        this.acquisitionTime = acquisitionTime;
        this.stepCount = stepCount;
        this.stepCountCal = stepCountCal;
        this.distance = distance;
        this.distancePerStep = distancePerStep;
        this.stepPerSecond = stepPerSecond;
        this.longitude = longitude;
        this.latitude = latitude;
        this.locationType = locationType;
        this.isNormal = isNormal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public int getAcquisitionTime() {
        return acquisitionTime;
    }

    public void setAcquisitionTime(int acquisitionTime) {
        this.acquisitionTime = acquisitionTime;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public int getStepCountCal() {
        return stepCountCal;
    }

    public void setStepCountCal(int stepCountCal) {
        this.stepCountCal = stepCountCal;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public long getDistancePerStep() {
        return distancePerStep;
    }

    public void setDistancePerStep(long distancePerStep) {
        this.distancePerStep = distancePerStep;
    }

    public long getStepPerSecond() {
        return stepPerSecond;
    }

    public void setStepPerSecond(long stepPerSecond) {
        this.stepPerSecond = stepPerSecond;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public int getIsNormal() {
        return isNormal;
    }

    public void setIsNormal(int isNormal) {
        this.isNormal = isNormal;
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
}
