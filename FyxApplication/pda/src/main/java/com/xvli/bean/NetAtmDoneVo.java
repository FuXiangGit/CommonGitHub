package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 网点检查项完成 保存
 */
@DatabaseTable(tableName="NET_DONE_INFO")
public class NetAtmDoneVo {

    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;

    //网点id
    @DatabaseField
    private String branchid;

    //网点所属客户
    @DatabaseField
    private String  customerid;

    //网点检查项是否完成
    @DatabaseField
    private String netisdone;

    //机具检查项是否完成
    @DatabaseField
    private String atmisdone;

    @DatabaseField
    private String isUploader = "N";

    //所属客户
    @DatabaseField
    private String atmcustomerid ;
    // 操作时间
    @DatabaseField
    private String operatedtime;

    // 操作人，jobnumber用逗号隔开
    @DatabaseField
    private String operator;
    //网点类型
    @DatabaseField
    private String atmnodetype;


    @DatabaseField
    private String taskid;

    //是否登记  Y 已登记  N 未登记
    @DatabaseField
    private String isRegister ="N" ;
    //上传数据数据时唯一标示符
    @DatabaseField
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIsRegister() {
        return isRegister;
    }

    public void setIsRegister(String isRegister) {
        this.isRegister = isRegister;
    }

    public NetAtmDoneVo() {
    }


    public String getAtmisdone() {
        return atmisdone;
    }

    public void setAtmisdone(String atmisdone) {
        this.atmisdone = atmisdone;
    }

    public String getBranchid() {
        return branchid;
    }

    public void setBranchid(String branchid) {
        this.branchid = branchid;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getIsUploader() {
        return isUploader;
    }

    public void setIsUploader(String isUploader) {
        this.isUploader = isUploader;
    }


    public String getNetisdone() {
        return netisdone;
    }

    public void setNetisdone(String netisdone) {
        this.netisdone = netisdone;
    }

    public String getAtmcustomerid() {
        return atmcustomerid;
    }

    public void setAtmcustomerid(String atmcustomerid) {
        this.atmcustomerid = atmcustomerid;
    }

    public String getAtmnodetype() {
        return atmnodetype;
    }

    public void setAtmnodetype(String atmnodetype) {
        this.atmnodetype = atmnodetype;
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

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }
}
