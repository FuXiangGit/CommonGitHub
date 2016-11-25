package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
@DatabaseTable(tableName = "TRUCK_VO")
public class TruckVo implements Serializable
{
    @DatabaseField(generatedId = true)
    private int tableid;


    @DatabaseField
    private long id;
    //车辆二维码
    @DatabaseField
    private String code;
    //车牌号
    @DatabaseField
    private String platenumber;

    //车辆ID号
    @DatabaseField
    private String truckId;

    //车辆状态
    @DatabaseField
    private String state;

    //车辆部门id号
    @DatabaseField
    private String departmentid;

    //车辆所属部门
    @DatabaseField
    private String departmentname;
    
    @DatabaseField
    private String clientId;

    //计划内 Y  计划外  N
    @DatabaseField
    private String isPlan = "Y";

    //是否绑定 1绑定  2未绑定
    @DatabaseField
    private int operateType = 2;

    @DatabaseField
    private String type; //1 车辆 2 车辆钥匙  3 车辆侧门钥匙

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    @DatabaseField
    private String first; //泰国  Y   有绑定过车辆  需要扫描 车辆钥匙 和车辆侧门钥匙

    //车辆线路id

    public String getLineid() {
        return lineid;
    }

    public void setLineid(String lineid) {
        this.lineid = lineid;
    }

    @DatabaseField
    private String lineid;

    //操作时间
    @DatabaseField
    private String operateTime;

    //操作人
    @DatabaseField
    private String operators;

    @DatabaseField
    private String gisx;

    @DatabaseField
    private String gisy;

    public String getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        this.isUploaded = isUploaded;
    }

    @DatabaseField

    private String gisz;

    @DatabaseField
    private String isUploaded="N";


    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperators() {
        return operators;
    }

    public void setOperators(String operators) {
        this.operators = operators;
    }

    public String getGisx() {
        return gisx;
    }

    public void setGisx(String gisx) {
        this.gisx = gisx;
    }

    public String getGisy() {
        return gisy;
    }

    public void setGisy(String gisy) {
        this.gisy = gisy;
    }

    public String getGisz() {
        return gisz;
    }

    public void setGisz(String gisz) {
        this.gisz = gisz;
    }




    public String getIsPlan() {
        return isPlan;
    }

    public void setIsPlan(String isPlan) {
        this.isPlan = isPlan;
    }

    
    public int getTableid()
    {
        return tableid;
    }
    public void setTableid(int tableid)
    {
        this.tableid = tableid;
    }

    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public String getCode()
    {
        return code;
    }
    public void setCode(String code)
    {
        this.code = code;
    }
    public String getPlatenumber()
    {
        return platenumber;
    }
    public void setPlatenumber(String platenumber)
    {
        this.platenumber = platenumber;
    }
    public String getState()
    {
        return state;
    }
    public void setState(String state)
    {
        this.state = state;
    }
    public String getDepartmentid()
    {
        return departmentid;
    }
    public void setDepartmentid(String departmentid)
    {
        this.departmentid = departmentid;
    }


}
