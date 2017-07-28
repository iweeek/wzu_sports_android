package com.tim.app.server.entry;

/**
 * Created by nimon on 2017/6/17.
 */

public class SportsRecord {

    protected Integer runningSportId;
    protected Integer studentId;


    public Integer getProjectId() {
        return runningSportId;
    }

    public void setRunningSportId(Integer runningSportId) {
        this.runningSportId = runningSportId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
}
