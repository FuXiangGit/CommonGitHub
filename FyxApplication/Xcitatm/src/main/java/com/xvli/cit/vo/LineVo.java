package com.xvli.cit.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 线路表
 */
@DatabaseTable(tableName = "LINE_VO")
public class LineVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;

    @DatabaseField
    private String lineid;//线路id

    @DatabaseField
    private String linename; //线路名字

    @DatabaseField
    private String linedata;//线路日期

    @DatabaseField
    private String customername; //客户名称

    @DatabaseField
    private boolean ischeck;//是否选中

    @DatabaseField
    private String operators;//操作人

    @DatabaseField
    private String isUploaded = "N";

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

    public String getLineid() {
        return lineid;
    }

    public void setLineid(String lineid) {
        this.lineid = lineid;
    }

    public String getLinename() {
        return linename;
    }

    public void setLinename(String linename) {
        this.linename = linename;
    }

    public String getLinedata() {
        return linedata;
    }

    public void setLinedata(String linedata) {
        this.linedata = linedata;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getOperators() {
        return operators;
    }

    public void setOperators(String operators) {
        this.operators = operators;
    }

    public String getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        this.isUploaded = isUploaded;
    }
}
