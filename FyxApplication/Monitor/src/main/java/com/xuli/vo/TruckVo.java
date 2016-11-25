package com.xuli.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 所有车辆信息
 */
@DatabaseTable(tableName = "TRUCK_VO")
public class TruckVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;
    //车辆id
    @DatabaseField
    private String id;

    //是否选中
    @DatabaseField
    private boolean ischeck;

    //车辆部门id
    @DatabaseField
    private String depid;

    //车辆部门
    @DatabaseField
    private String platenumber;

    //车辆部门名称
    @DatabaseField
    private String depname;

    //车辆状态 是否在线
    @DatabaseField
    private boolean online;

    //车辆状态 是否正在监控  监控才可弹出选择小功能
    @DatabaseField
    private boolean ismonitor;

    @DatabaseField
    private String team;
    @DatabaseField
    private String crew;
    @DatabaseField
    private String lastlocation;
    @DatabaseField
    private String lastlocationtime;
    @DatabaseField
    private String lastmessage;
    @DatabaseField
    private String lastmessagetime;
    @DatabaseField
    private String code;
    @DatabaseField
    private String bodynum;

    //车辆类型
    @DatabaseField
    private String cartype;

    @DatabaseField
    private String carbrandtyp;
    @DatabaseField
    private String recognitioncode;
    @DatabaseField
    private String recognitionid;
    @DatabaseField
    private String carperson;

    @DatabaseField
    private String address;
    @DatabaseField
    private String usenature;
    @DatabaseField
    private String carstatus;
    @DatabaseField
    private String radio;

    //车辆是否在线
    @DatabaseField
    private boolean show;

    public TruckVo() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBodynum() {
        return bodynum;
    }

    public void setBodynum(String bodynum) {
        this.bodynum = bodynum;
    }

    public String getCarbrandtyp() {
        return carbrandtyp;
    }

    public void setCarbrandtyp(String carbrandtyp) {
        this.carbrandtyp = carbrandtyp;
    }

    public String getCarperson() {
        return carperson;
    }

    public void setCarperson(String carperson) {
        this.carperson = carperson;
    }

    public String getCarstatus() {
        return carstatus;
    }

    public void setCarstatus(String carstatus) {
        this.carstatus = carstatus;
    }

    public String getCartype() {
        return cartype;
    }

    public void setCartype(String cartype) {
        this.cartype = cartype;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCrew() {
        return crew;
    }

    public void setCrew(String crew) {
        this.crew = crew;
    }

    public String getDepid() {
        return depid;
    }

    public void setDepid(String depid) {
        this.depid = depid;
    }

    public String getDepname() {
        return depname;
    }

    public void setDepname(String depname) {
        this.depname = depname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getLastlocation() {
        return lastlocation;
    }

    public void setLastlocation(String lastlocation) {
        this.lastlocation = lastlocation;
    }

    public String getLastlocationtime() {
        return lastlocationtime;
    }

    public void setLastlocationtime(String lastlocationtime) {
        this.lastlocationtime = lastlocationtime;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getLastmessagetime() {
        return lastmessagetime;
    }

    public void setLastmessagetime(String lastmessagetime) {
        this.lastmessagetime = lastmessagetime;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getPlatenumber() {
        return platenumber;
    }

    public void setPlatenumber(String platenumber) {
        this.platenumber = platenumber;
    }

    public String getRadio() {
        return radio;
    }

    public void setRadio(String radio) {
        this.radio = radio;
    }

    public String getRecognitioncode() {
        return recognitioncode;
    }

    public void setRecognitioncode(String recognitioncode) {
        this.recognitioncode = recognitioncode;
    }

    public String getRecognitionid() {
        return recognitionid;
    }

    public void setRecognitionid(String recognitionid) {
        this.recognitionid = recognitionid;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getUsenature() {
        return usenature;
    }

    public void setUsenature(String usenature) {
        this.usenature = usenature;
    }

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public boolean ismonitor() {
        return ismonitor;
    }

    public void setIsmonitor(boolean ismonitor) {
        this.ismonitor = ismonitor;
    }
}
