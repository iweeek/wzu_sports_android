package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 首页的运动对象
 */
public class Sport implements Serializable {

    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    public static final int TYPE_THREE = 2;
    public static final int TYPE_FOUR = 3;

    private static final long serialVersionUID = 6187447685293862071L;
    private String title;//名称
    private int joinNumber;//参加人数
    private int targetDistance;//目标距离
    private String targetSpeed;//目标速度
    private int targetTime;//目标时间
    private int steps;//目标步数
    private String bgUrl;//背景图片地址
    private int bgDrawableId;//背景图片id
    private int type;//运动方式

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getJoinNumber() {
        return joinNumber;
    }

    public void setJoinNumber(int joinNumber) {
        this.joinNumber = joinNumber;
    }

    public int getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(int targetDistance) {
        this.targetDistance = targetDistance;
    }

    public String getTargetSpeed() {
        return targetSpeed;
    }

    public void setTargetSpeed(String targetSpeed) {
        this.targetSpeed = targetSpeed;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public int getBgDrawableId() {
        return bgDrawableId;
    }

    public void setBgDrawableId(int bgDrawableId) {
        this.bgDrawableId = bgDrawableId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
