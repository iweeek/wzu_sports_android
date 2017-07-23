package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 运动区域
 */
public class SportArea implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;
    private String desc;//运动场馆
    private String address;//运动地点
    private int targetTime;//达标时间
    private boolean isSelected;//是否选中

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
