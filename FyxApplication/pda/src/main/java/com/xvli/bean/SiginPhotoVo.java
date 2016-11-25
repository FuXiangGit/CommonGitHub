package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 机具完成时上传该表的所有数据
 * 各种页面的签名照片   网点检查项  机具检查项  机具维修项  机具故障项
 */
@DatabaseTable(tableName = "SIGIN_PHOTO_VO")
public class SiginPhotoVo {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;
    // 任务id号
    @DatabaseField
    private String taskid;

    // atm id号
    @DatabaseField
    private String atmid;

    // 操作时间
    @DatabaseField
    private String branchid;

    @DatabaseField
    private String branchidname;

    @DatabaseField
    private String operatedtime;

    // 操作人
    @DatabaseField
    private String operator;

    @DatabaseField(unique = true)
    private String siginpath;

    @DatabaseField
    private String remarks;

    @DatabaseField
    private String isUploaded = "N";


    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getAtmid() {
        return atmid;
    }

    public void setAtmid(String atmid) {
        this.atmid = atmid;
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

    public String getSiginpath() {
        return siginpath;
    }

    public void setSiginpath(String siginpath) {
        this.siginpath = siginpath;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        this.isUploaded = isUploaded;
    }

    public String getBranchid() {
        return branchid;
    }

    public void setBranchid(String branchid) {
        this.branchid = branchid;
    }

    public String getBranchidname() {
        return branchidname;
    }

    public void setBranchidname(String branchidname) {
        this.branchidname = branchidname;
    }
}
