package com.xuli.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * 车辆一级菜单信息
 */
@DatabaseTable(tableName = "TRUCK_GROUP_VO")
public class TruckGroupVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;

   /* "id": "432ab132-be1e-4c48-b8ba-aa96e14d6574",// 车辆部门id
            "name": "办公室",
            "vehicount": 5,
            "vehionline": 0,
            "pid": "f5ee0793-f674-49ef-a350-898cab2827e9",
            "code": "100102",
            "children": []*/
    //车辆部门
    @DatabaseField
    private String id;

    //车辆部门id
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
    private List<TruckChildVo> childList;


    public TruckGroupVo() {
    }

    public List<TruckChildVo> getChildList() {
        return childList;
    }

    public void setChildList(List<TruckChildVo> childList) {
        this.childList = childList;
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
