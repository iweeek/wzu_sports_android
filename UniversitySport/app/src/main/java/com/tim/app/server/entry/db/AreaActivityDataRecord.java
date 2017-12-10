package com.tim.app.server.entry.db;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

/**
 * @创建者 倪军
 * @创建时间 04/12/2017
 * @描述
 */
@Entity(nameInDb = "area_activity_data")
public class AreaActivityDataRecord {

    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    public Long id;

    @Property(nameInDb = "activity_id")
    public long activityId;
    @Property(nameInDb = "acquisition_time")

    @Convert(converter = DateStringConverter.class, columnType = String.class)
    public Date acquisitionTime;

    @Property(nameInDb = "is_normal")
    public boolean isNormal;
    @Property(nameInDb = "longitude")
    public double longitude;
    @Property(nameInDb = "latitude")
    public double latitude;
    @Property(nameInDb = "location_type")
    public int locationType;

    @Convert(converter = DateStringConverter.class, columnType = String.class)
    @Property(nameInDb = "created_at")
    public Date createdAt;

    @Convert(converter = DateStringConverter.class, columnType = String.class)
    @Property(nameInDb = "updated_at")
    public Date updatedAt;

    @Generated(hash = 882222915)
    public AreaActivityDataRecord(Long id, long activityId, Date acquisitionTime,
            boolean isNormal, double longitude, double latitude, int locationType,
            Date createdAt, Date updatedAt) {
        this.id = id;
        this.activityId = activityId;
        this.acquisitionTime = acquisitionTime;
        this.isNormal = isNormal;
        this.longitude = longitude;
        this.latitude = latitude;
        this.locationType = locationType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Generated(hash = 542785037)
    public AreaActivityDataRecord() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getActivityId() {
        return this.activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public Date getAcquisitionTime() {
        return this.acquisitionTime;
    }

    public void setAcquisitionTime(Date acquisitionTime) {
        this.acquisitionTime = acquisitionTime;
    }

    public boolean getIsNormal() {
        return this.isNormal;
    }

    public void setIsNormal(boolean isNormal) {
        this.isNormal = isNormal;
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

    public int getLocationType() {
        return this.locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
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
