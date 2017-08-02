package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 运动数据
 */
public class HistoryRunningSportEntry  extends HistorySportEntry implements Serializable {
    private static final long serialVersionUID = 6187447685293862071L;
    private int distance;
    private String speed;//速度

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }


    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}
