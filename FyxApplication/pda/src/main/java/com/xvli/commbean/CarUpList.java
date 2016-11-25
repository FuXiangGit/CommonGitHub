package com.xvli.commbean;

/**
 * Created by Administrator on 13:43.
 */
public class CarUpList {

    private String braCode; //二维码
    private String itemtype ; //类型 0为钞箱，1为钞袋    2 卡钞  3 废钞
    private String status; // "Y" 已扫描  "N" 未扫描
    private int    reasonType; // 加机清钞装上物品 1           2 卸下物品   3
    private String atmid;

    public String getAtmid() {
        return atmid;
    }

    public void setAtmid(String atmid) {
        this.atmid = atmid;
    }

    public CarUpList() {

    }

    public String getBraCode() {
        return braCode;
    }

    public void setBraCode(String braCode) {
        this.braCode = braCode;
    }

    public String getItemtype() {
        return itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
