package com.xuli.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 车辆二级菜单信息
 */
@DatabaseTable(tableName = "TRUCK_CHILD_VO")
public class TruckChildVo implements Serializable {

    @DatabaseField(generatedId = true)
    private int ids;


    //车辆部门id
    @DatabaseField
    private String id;

    //车辆部门所在的父 部门 id
    @DatabaseField
    private String pid;

    //车辆部门名称
    @DatabaseField
    private String name;


    @DatabaseField
    private String code;


    //车辆总数据
    @DatabaseField
    private int vehicount;


    //车辆在线数据
    @DatabaseField
    private int vehionline;


    public TruckChildVo() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVehicount() {
        return vehicount;
    }

    public void setVehicount(int vehicount) {
        this.vehicount = vehicount;
    }

    public int getVehionline() {
        return vehionline;
    }

    public void setVehionline(int vehionline) {
        this.vehionline = vehionline;
    }
}
