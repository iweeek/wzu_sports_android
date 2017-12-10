package com.tim.app.server.entry.db;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by nimon on 2017/6/17.
 * 数据库记录
 */
@Entity(nameInDb = "area_activity")
public class AreaActivityRecord {

    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    public Long id;

    @Property(nameInDb = "area_sport_id")
    public long areaSportId;
    @Property(nameInDb = "area_sport_name")
    private String areaSportName;
    // @Property(nameInDb = "location_id")
    // public long locationId;

    @Property(nameInDb = "student_id")
    public long studentId;
    @Property(nameInDb = "cost_time")
    public int costTime;

    @Convert(converter = DateStringConverter.class, columnType = String.class)
    @Property(nameInDb = "start_time")
    public Date startTime;

    @Property(nameInDb = "kcal_consumed")
    public int kcalConsumed;
    @Property(nameInDb = "qualified")
    public boolean qualified;
    @Property(nameInDb = "is_valid")
    public boolean isValid;
    @Property(nameInDb = "is_verified")
    public boolean isVerified;
    @Property(nameInDb = "qualified_cost_time")
    public int qualifiedCostTime;

    @Convert(converter = DateStringConverter.class, columnType = String.class)
    @Property(nameInDb = "created_at")
    public Date createdAt;

    @Convert(converter = DateStringConverter.class, columnType = String.class)
    @Property(nameInDb = "updated_at")
    public Date updatedAt;

    @Convert(converter = DateStringConverter.class, columnType = String.class)
    @Property(nameInDb = "ended_at")
    public Date endedAt;

    @Property(nameInDb = "ended_by")
    public int endedBy;

    @Property(nameInDb = "sport_date")
    private String sportDate;

    // Location
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "longitude")
    private double longitude;
    @Property(nameInDb = "latitude")
    private double latitude;
    @Property(nameInDb = "radius")
    private int radius;
    @Property(nameInDb = "addr")
    private String addr;
    @Property(nameInDb = "is_enabled")
    private boolean isEnabled;
    @Generated(hash = 2039695789)
    public AreaActivityRecord(Long id, long areaSportId, String areaSportName,
            long studentId, int costTime, Date startTime, int kcalConsumed,
            boolean qualified, boolean isValid, boolean isVerified,
            int qualifiedCostTime, Date createdAt, Date updatedAt, Date endedAt,
            int endedBy, String sportDate, String name, double longitude,
            double latitude, int radius, String addr, boolean isEnabled) {
        this.id = id;
        this.areaSportId = areaSportId;
        this.areaSportName = areaSportName;
        this.studentId = studentId;
        this.costTime = costTime;
        this.startTime = startTime;
        this.kcalConsumed = kcalConsumed;
        this.qualified = qualified;
        this.isValid = isValid;
        this.isVerified = isVerified;
        this.qualifiedCostTime = qualifiedCostTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.endedAt = endedAt;
        this.endedBy = endedBy;
        this.sportDate = sportDate;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.addr = addr;
        this.isEnabled = isEnabled;
    }
    @Generated(hash = 564245555)
    public AreaActivityRecord() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getAreaSportId() {
        return this.areaSportId;
    }
    public void setAreaSportId(long areaSportId) {
        this.areaSportId = areaSportId;
    }
    public String getAreaSportName() {
        return this.areaSportName;
    }
    public void setAreaSportName(String areaSportName) {
        this.areaSportName = areaSportName;
    }
    public long getStudentId() {
        return this.studentId;
    }
    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }
    public int getCostTime() {
        return this.costTime;
    }
    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }
    public Date getStartTime() {
        return this.startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public int getKcalConsumed() {
        return this.kcalConsumed;
    }
    public void setKcalConsumed(int kcalConsumed) {
        this.kcalConsumed = kcalConsumed;
    }
    public boolean getQualified() {
        return this.qualified;
    }
    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }
    public boolean getIsValid() {
        return this.isValid;
    }
    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }
    public boolean getIsVerified() {
        return this.isVerified;
    }
    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
    public int getQualifiedCostTime() {
        return this.qualifiedCostTime;
    }
    public void setQualifiedCostTime(int qualifiedCostTime) {
        this.qualifiedCostTime = qualifiedCostTime;
    }
    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Date getEndedAt() {
        return this.endedAt;
    }
    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }
    public int getEndedBy() {
        return this.endedBy;
    }
    public void setEndedBy(int endedBy) {
        this.endedBy = endedBy;
    }
    public String getSportDate() {
        return this.sportDate;
    }
    public void setSportDate(String sportDate) {
        this.sportDate = sportDate;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public int getRadius() {
        return this.radius;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }
    public String getAddr() {
        return this.addr;
    }
    public void setAddr(String addr) {
        this.addr = addr;
    }
    public boolean getIsEnabled() {
        return this.isEnabled;
    }
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    

}