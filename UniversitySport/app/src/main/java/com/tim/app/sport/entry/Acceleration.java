package com.tim.app.sport.entry;

import java.io.Serializable;

/**
 * @创建者 倪军
 * @创建时间 26/09/2017
 * @描述
 */
public class Acceleration implements Serializable{
    private int id;
    private float x;
    private float y;
    private float z;
    private float average;
    private long timestamp;

    @Override
    public String toString() {
        return "Acceleration  " + "average=" + average;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

}
