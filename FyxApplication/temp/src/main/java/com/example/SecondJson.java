package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/29 0029.
 */
public class SecondJson {
    private String id;
    private String barcode;
    private String lineid;
    private int Deleted;
    private List<ThirdJson> data = new ArrayList<ThirdJson>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getLineid() {
        return lineid;
    }

    public void setLineid(String lineid) {
        this.lineid = lineid;
    }

    public int getDeleted() {
        return Deleted;
    }

    public void setDeleted(int deleted) {
        Deleted = deleted;
    }

    public List<ThirdJson> getData() {
        return data;
    }

    public void setData(List<ThirdJson> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SecondJson{" +
                "id='" + id + '\'' +
                ", barcode='" + barcode + '\'' +
                ", lineid='" + lineid + '\'' +
                ", Deleted=" + Deleted +
                ", data=" + data +
                '}';
    }
}
