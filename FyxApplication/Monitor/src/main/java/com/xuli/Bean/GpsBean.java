package com.xuli.Bean;

/**
 * Created by Administrator on 12:20.
 */
public class GpsBean {
    private GpsItemBean  data;
    private String plateNumber;
    private String type;

    public GpsItemBean getData() {
        return data;
    }

    public void setData(GpsItemBean data) {
        this.data = data;
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
}
