package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * WIFI信息
 */
public class WifiInfo implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;
    private String wifiName;//wifi名称
    private String ssid;

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
}
