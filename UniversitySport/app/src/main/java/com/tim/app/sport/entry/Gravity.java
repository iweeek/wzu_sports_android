package com.tim.app.sport.entry;


import java.io.Serializable;

/**
 * @创建者 倪军
 * @创建时间 27/09/2017
 * @描述
 */
public class Gravity implements Serializable{

    private int id;

    private float x;
    private float y;
    private float z;
    private float average;

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

    @Override
    public String toString() {
        return "Gravity" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", average=" + average;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
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
