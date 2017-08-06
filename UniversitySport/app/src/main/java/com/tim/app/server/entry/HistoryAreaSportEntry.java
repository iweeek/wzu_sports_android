package com.tim.app.server.entry;

/**
 * @创建者 倪军
 * @创建时间 2017/8/1
 * @描述
 */

public class HistoryAreaSportEntry extends HistorySportEntry{

    private static final long serialVersionUID = 6187447685293862071L;
    private String areaName;
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /*多态*/
    @Override
    public String getSportName() {
        return getAreaName();
    }
}
