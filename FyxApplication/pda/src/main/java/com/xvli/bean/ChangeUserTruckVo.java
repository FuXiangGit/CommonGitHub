package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 调度换人换车
 * Created by Administrator on 2015/12/8.
 */
@DatabaseTable(tableName = "CHANGE_VO")
public class ChangeUserTruckVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;
    //调度id
    @DatabaseField
    private String dispatchid;
    // 日期
    @DatabaseField
    private String taskTime;
    //线路
    @DatabaseField
    private String line;

    // 原业务员
    @DatabaseField
    private String workersId;

    // 变更业务员
    @DatabaseField
    private String changeworkersid;

    // 变更业务员详细信息
    @DatabaseField
    private String changeworkersname;

    // 原车辆
    @DatabaseField
    private String truckId;

    // 变更车辆id
    @DatabaseField
    private String changetruckid;

    // 变更车辆车牌
    @DatabaseField
    private String changetruckplatenumber;

    // 变更车辆自编号
    @DatabaseField
    private String changetruckcode;

    // 变更车辆条码
    @DatabaseField
    private String changetruckbarcode;

    /// 变更车辆所属公司
    @DatabaseField
    private String changetruckcompany;

    // 调度说明
    @DatabaseField
    private String dispatchinfo;

    // 调度时间
    @DatabaseField
    private String dispatchtime;

    public ChangeUserTruckVo() {

    }

    public String getChangetruckbarcode() {
        return changetruckbarcode;
    }

    public void setChangetruckbarcode(String changetruckbarcode) {
        this.changetruckbarcode = changetruckbarcode;
    }

    public String getChangetruckcode() {
        return changetruckcode;
    }

    public void setChangetruckcode(String changetruckcode) {
        this.changetruckcode = changetruckcode;
    }

    public String getChangetruckcompany() {
        return changetruckcompany;
    }

    public void setChangetruckcompany(String changetruckcompany) {
        this.changetruckcompany = changetruckcompany;
    }

    public String getChangetruckid() {
        return changetruckid;
    }

    public void setChangetruckid(String changetruckid) {
        this.changetruckid = changetruckid;
    }

    public String getChangetruckplatenumber() {
        return changetruckplatenumber;
    }

    public void setChangetruckplatenumber(String changetruckplatenumber) {
        this.changetruckplatenumber = changetruckplatenumber;
    }

    public String getChangeworkersid() {
        return changeworkersid;
    }

    public void setChangeworkersid(String changeworkersid) {
        this.changeworkersid = changeworkersid;
    }

    public String getChangeworkersname() {
        return changeworkersname;
    }

    public void setChangeworkersname(String changeworkersname) {
        this.changeworkersname = changeworkersname;
    }

    public String getDispatchid() {
        return dispatchid;
    }

    public void setDispatchid(String dispatchid) {
        this.dispatchid = dispatchid;
    }

    public String getDispatchinfo() {
        return dispatchinfo;
    }

    public void setDispatchinfo(String dispatchinfo) {
        this.dispatchinfo = dispatchinfo;
    }

    public String getDispatchtime() {
        return dispatchtime;
    }

    public void setDispatchtime(String dispatchtime) {
        this.dispatchtime = dispatchtime;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }

    public String getWorkersId() {
        return workersId;
    }

    public void setWorkersId(String workersId) {
        this.workersId = workersId;
    }
}
