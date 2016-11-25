package com.xuli.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/09/28.
 * 车辆轨迹数据  ：用于车辆轨迹回放
 */
@DatabaseTable(tableName = "TRUCK_GPS_VO")
public class TruckGpsVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;

    @DatabaseField
    private double   latitue;

    @DatabaseField
    private double   longitude;


    @DatabaseField
    private String mileage;

    @DatabaseField
    private String speed;

    @DatabaseField
    private String time;

    @DatabaseField
    private String impulseSpeed;

    @DatabaseField
    private int course;  //角度

    @DatabaseField
    private String altitude;

    @DatabaseField
    private String plateNumber;//车牌号

    @DatabaseField
    private String type;//接口数据类型（GPS_INFO）

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public double getLatitue() {
        return latitue;
    }

    public void setLatitue(double latitue) {
        this.latitue = latitue;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImpulseSpeed() {
        return impulseSpeed;
    }

    public void setImpulseSpeed(String impulseSpeed) {
        this.impulseSpeed = impulseSpeed;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




    //构造方法
    public TruckGpsVo() {
    }


}
