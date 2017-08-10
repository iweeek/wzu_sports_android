package com.tim.app.server.entry.db;

/**
 * Created by nimon on 2017/6/17.
 * 数据库记录
 */

public class RunningSportsRecordOld extends SportsRecord {

    private int id;
    private Integer currentDistance;
    private Integer elapseTime;
    private Long startTime;
    private Integer steps;
    private Long date;
    private Integer acitivityId;


    public RunningSportsRecordOld() {
    }

    public RunningSportsRecordOld(int id, Integer runningSportId, Integer activityId, Integer studentId,
                                  Integer currentDistance, Integer elapseTime,
                                  Long startTime, Integer steps, Long date) {
        this.id = id;
        this.runningSportId = runningSportId;
        this.acitivityId = activityId;
        this.studentId = studentId;
        this.currentDistance = currentDistance;
        this.elapseTime = elapseTime;
        this.startTime = startTime;
        this.steps = steps;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(Integer currentDistance) {
        this.currentDistance = currentDistance;
    }

    public Integer getElapseTime() {
        return elapseTime;
    }

    public void setElapseTime(Integer elapseTime) {
        this.elapseTime = elapseTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "RunningSportsRecordOld{" +
                "runningSportId=" + runningSportId +
                ", id=" + id +
                ", studentId=" + studentId +
                ", currentDistance=" + currentDistance +
                ", elapseTime=" + elapseTime +
                ", startTime=" + startTime +
                ", steps=" + steps +
                ", date=" + date +
                '}';
    }

    public Integer getAcitivityId() {
        return acitivityId;
    }

    public void setAcitivityId(Integer acitivityId) {
        this.acitivityId = acitivityId;
    }
}
