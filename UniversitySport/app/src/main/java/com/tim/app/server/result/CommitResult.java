package com.tim.app.server.result;

/**
 * 提交运动数据返回数据mode
 */

public class CommitResult {

    private int status;
    private int code;
    private String message;
    private String developerMessage;
    private int id;
    private int projectId;
    private int studentId;
    private int distance;
    private int costTime;
    private int targetTime;
    private int qualified;
    private int startTime;
    private long createdAt;
    private long updatedAt;
    private int costCalorie;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public int getQualified() {
        return qualified;
    }

    public void setQualified(int qualified) {
        this.qualified = qualified;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
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

    public int getCostCalorie() {
        return costCalorie;
    }

    public void setCostCalorie(int costCalorie) {
        this.costCalorie = costCalorie;
    }

    @Override
    public String toString() {
        return "CommitResult{" +
                "status=" + status +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", developerMessage='" + developerMessage + '\'' +
                ", id=" + id +
                ", projectId=" + projectId +
                ", studentId=" + studentId +
                ", distance=" + distance +
                ", costTime=" + costTime +
                ", targetTime=" + targetTime +
                ", qualified=" + qualified +
                ", startTime=" + startTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", costCalorie=" + costCalorie +
                '}';
    }
}
