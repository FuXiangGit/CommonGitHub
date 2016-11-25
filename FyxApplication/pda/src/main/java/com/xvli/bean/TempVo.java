package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 泰国 项目 机具完成时 绑定拉链包
 */
@DatabaseTable(tableName = "T_TEMP_VO")
public class TempVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String atmid ;

    @DatabaseField
    private String clientid ;

    @DatabaseField
    private int itemtype ;

    @DatabaseField
    private String lineid ;

    @DatabaseField
    private String operatetime ;

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
    }

    @DatabaseField
    private String barcode;

    //是否已经上传过
    @DatabaseField
    private String isUploaded="N";

    public String getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        this.isUploaded = isUploaded;
    }

    public String getAtmid() {
        return atmid;
    }

    public void setAtmid(String atmid) {
        this.atmid = atmid;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemtype() {
        return itemtype;
    }

    public void setItemtype(int itemtype) {
        this.itemtype = itemtype;
    }

    public String getLineid() {
        return lineid;
    }

    public void setLineid(String lineid) {
        this.lineid = lineid;
    }


}
