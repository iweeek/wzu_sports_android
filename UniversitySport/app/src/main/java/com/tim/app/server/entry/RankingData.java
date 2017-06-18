package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 排行榜数据
 */
public class RankingData implements Serializable {


    private static final long serialVersionUID = 6187447685293862071L;
    private String userName;//速度
    private String avatar;//速度
    private int costValue;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getCostValue() {
        return costValue;
    }

    public void setCostValue(int costValue) {
        this.costValue = costValue;
    }
}
