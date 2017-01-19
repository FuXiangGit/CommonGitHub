package com.example;

/**
 * Created by Administrator on 2016/12/29 0029.
 */
public class FourthJson {
    private String id;
    private String barcode;
    private String lineid;
    private int Deleted;

    public FourthJson(String id, String barcode, String lineid, int deleted) {
        this.id = id;
        this.barcode = barcode;
        this.lineid = lineid;
        Deleted = deleted;
    }

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

    @Override
    public String toString() {
        return "FourthJson{" +
                "id='" + id + '\'' +
                ", barcode='" + barcode + '\'' +
                ", lineid='" + lineid + '\'' +
                ", Deleted=" + Deleted +
                '}';
    }
}
