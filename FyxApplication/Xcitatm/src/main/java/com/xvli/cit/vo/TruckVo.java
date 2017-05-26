package com.xvli.cit.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "TRUCK_VO")
public class TruckVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;
    @DatabaseField
    private String clientid;

    @DatabaseField
    private String truckId;//车辆ID号

    @DatabaseField
    private String barcode; //车辆二维码

    @DatabaseField
    private String platenumber;//车牌号


    @DatabaseField
    private String departmentid; //车辆部门id号


    @DatabaseField
    private String departmentname; //车辆所属部门

    @DatabaseField
    private int operateType = 2;//是否绑定 1绑定  2未绑定  3已解绑

    @DatabaseField
    private String kilometre; //公里数


    @DatabaseField
    private String operatetime;//操作时间


    @DatabaseField
    private String operators;//操作人

    @DatabaseField
    private String isUploaded = "N";


    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }


    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
    }

    public String getOperators() {
        return operators;
    }

    public void setOperators(String operators) {
        this.operators = operators;
    }


    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPlatenumber() {
        return platenumber;
    }

    public void setPlatenumber(String platenumber) {
        this.platenumber = platenumber;
    }

    public String getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(String departmentid) {
        this.departmentid = departmentid;
    }

    public String getKilometre() {
        return kilometre;
    }

    public void setKilometre(String kilometre) {
        this.kilometre = kilometre;
    }

    public String getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        this.isUploaded = isUploaded;
    }


    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }
}
