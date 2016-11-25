package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 泰国维修更换Seal表
 */
@DatabaseTable(tableName = "TAI_CHANGE_SEAL")
public class TaiRepairSealVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;
    //Atm id号
    @DatabaseField
    private String atmid;

    @DatabaseField
    private String clientid;

    //对应任务id
    @DatabaseField
    private String taskid;

    //对应任务时间
    @DatabaseField
    private String tasktime;

    //客户ID号
    @DatabaseField
    private String atmcustomerid;

    //维修Seal条形码  新编码属于表中的数据
    @DatabaseField
    private String newbarcode;

    //维修Seal条形码
    @DatabaseField
    private String oldbarcode;

    //操作类型 1为巡检    0为作业任务(加钞任务)   2维修
    @DatabaseField
    private int tasktype;

    //编号
    @DatabaseField
    private String atmno;


    //备注
    @DatabaseField
    private String remarks;

    // 操作时间
    @DatabaseField
    private String operatedtime;

    // 操作人，jobnumber用逗号隔开
    @DatabaseField
    private String operator;


    @DatabaseField
    private String isUploaded = "N";


    //泰国项目 线路id  上下级机具数据上传时用到
    @DatabaseField
    private String linenchid;

    @DatabaseField
    private String boxcoderecycle;

    public String getOldbarcode() {
        return oldbarcode;
    }

    public void setOldbarcode(String oldbarcode) {
        this.oldbarcode = oldbarcode;
    }

    public String getLinenchid() {
        return linenchid;
    }

    public void setLinenchid(String linenchid) {
        this.linenchid = linenchid;
    }

    public String getBoxcoderecycle() {
        return boxcoderecycle;
    }

    public void setBoxcoderecycle(String boxcoderecycle) {
        this.boxcoderecycle = boxcoderecycle;
    }

    public TaiRepairSealVo() {
    }

    public String getAtmid() {
        return atmid;
    }

    public void setAtmid(String atmid) {
        this.atmid = atmid;
    }

    public String getAtmno() {
        return atmno;
    }

    public void setAtmno(String atmno) {
        this.atmno = atmno;
    }


    public String getNewbarcode() {
        return newbarcode;
    }

    public void setNewbarcode(String newbarcode) {
        this.newbarcode = newbarcode;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getCustomerid() {
        return atmcustomerid;
    }

    public void setCustomerid(String customerid) {
        this.atmcustomerid = customerid;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTasktime() {
        return tasktime;
    }

    public void setTasktime(String tasktime) {
        this.tasktime = tasktime;
    }


    public int getTasktype() {
        return tasktype;
    }

    public void setTasktype(int tasktype) {
        this.tasktype = tasktype;
    }


    public String getOperatedtime() {
        return operatedtime;
    }

    public void setOperatedtime(String operatedtime) {
        this.operatedtime = operatedtime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }


    public String getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        this.isUploaded = isUploaded;
    }


    public String getAtmcustomerid() {
        return atmcustomerid;
    }

    public void setAtmcustomerid(String atmcustomerid) {
        this.atmcustomerid = atmcustomerid;
    }
}
