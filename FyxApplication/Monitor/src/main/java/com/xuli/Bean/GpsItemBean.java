package com.xuli.Bean;

/**
 * Created by Administrator on 12:22.
 */
public class GpsItemBean {
    private String altitude;
    private int course;
    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    private String impulseSpeed;
    private double   latitue;
    private double   longitude;
    private String mileage;
    private String speed;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }





    public String getImpulseSpeed() {
        return impulseSpeed;
    }

    public void setImpulseSpeed(String impulseSpeed) {
        this.impulseSpeed = impulseSpeed;
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
}
