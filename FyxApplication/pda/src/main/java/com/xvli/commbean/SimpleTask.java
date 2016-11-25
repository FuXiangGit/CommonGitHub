package com.xvli.commbean;

/**
 * Created by Administrator on 2016/3/9.
 */
public class SimpleTask {
    private String netName;//网点名称
    private String normalOrOther;//正常任务还是其他任务
    private String lineNumber;//线路
    private String okNumberPercent;//完成数量
    private int isAllDone;//是否都完成，为完成为0，完成为1
    private String branchID;
    private String taskid;

    public SimpleTask() {
    }

    public SimpleTask( String netName, String normalOrOther,String lineNumber,  String okNumber,int isAllDone,String branchId) {
        this.isAllDone = isAllDone;
        this.lineNumber = lineNumber;
        this.netName = netName;
        this.normalOrOther = normalOrOther;
        this.okNumberPercent = okNumber;
        this.branchID = branchId;
    }

    public SimpleTask( String netName, String normalOrOther,String lineNumber,int isAllDone,String okNumberPercent,String taskid) {
        this.netName = netName;
        this.normalOrOther = normalOrOther;
        this.lineNumber = lineNumber;
        this.isAllDone = isAllDone;
        this.okNumberPercent = okNumberPercent;
        this.taskid = taskid;

    }

    public String getBranchID() {
        return branchID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    public int getIsAllDone() {
        return isAllDone;
    }

    public void setIsAllDone(int isAllDone) {
        this.isAllDone = isAllDone;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public String getNormalOrOther() {
        return normalOrOther;
    }

    public void setNormalOrOther(String normalOrOther) {
        this.normalOrOther = normalOrOther;
    }

    public String getOkNumberPercent() {
        return okNumberPercent;
    }

    public void setOkNumberPercent(String okNumberPercent) {
        this.okNumberPercent = okNumberPercent;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }
}
