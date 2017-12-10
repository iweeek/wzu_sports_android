package com.tim.app.server.entry.db;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

/**
 * @创建者 倪军
 * @创建时间 09/12/2017
 * @描述
 */
// @Entity(nameInDb = "Location_point")
public class LocationPoint {
    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "longitude")
    private double longitude;
    @Property(nameInDb = "latitude")
    private double latitude;
    @Property(nameInDb = "radius")
    private int radius;
    @Property(nameInDb = "qualified_cost_time")
    private int qualifiedCostTime;
    @Property(nameInDb = "addr")
    private String addr;
    @Property(nameInDb = "is_enabled")
    private boolean isEnabled;
    @Property(nameInDb = "description")
    private String description;
    @Property(nameInDb = "university_id")
    private int universityId;
    @Convert(converter = DateStringConverter.class, columnType = String.class)
    @Property(nameInDb = "created_at")
    private Date createdAt;
    @Convert(converter = DateStringConverter.class, columnType = String.class)
    @Property(nameInDb = "updated_at")
    private Date updatedAt;
    @Generated(hash = 1607597686)
    public LocationPoint(Long id, String name, double longitude, double latitude,
            int radius, int qualifiedCostTime, String addr, boolean isEnabled,
            String description, int universityId, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.qualifiedCostTime = qualifiedCostTime;
        this.addr = addr;
        this.isEnabled = isEnabled;
        this.description = description;
        this.universityId = universityId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    @Generated(hash = 1965639846)
    public LocationPoint() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public int getQualifiedCostTime() {
        return this.qualifiedCostTime;
    }
    public void setQualifiedCostTime(int qualifiedCostTime) {
        this.qualifiedCostTime = qualifiedCostTime;
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
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getUniversityId() {
        return this.universityId;
    }
    public void setUniversityId(int universityId) {
        this.universityId = universityId;
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
}
