package com.tim.app.server.entry;

/**
 * Created by nimon on 2017/6/17.
 */

public class RunningSportsRecord extends SportsRecord {

    private int id;
    private Integer currentDistance;
    private Integer elapseTime;
    private Integer startTime;
    private Integer steps;
    private Integer date;


    public RunningSportsRecord() {
    }

    public RunningSportsRecord(int id, Integer projectId, Integer studentId,
                               Integer currentDistance, Integer elapseTime,
                               Integer startTime, Integer steps, Integer date) {
        this.id = id;
        this.projectId = projectId;
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

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RunningSportsRecord{" +
                "projectId=" + projectId +
                ", id=" + id +
                ", studentId=" + studentId +
                ", currentDistance=" + currentDistance +
                ", elapseTime=" + elapseTime +
                ", startTime=" + startTime +
                ", steps=" + steps +
                ", date=" + date +
                '}';
    }
}
