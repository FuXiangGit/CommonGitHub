package com.xvli.cit.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 任务表
 */
@DatabaseTable(tableName = "TASK_VO")
public class TaskVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;

    @DatabaseField
    private String taskid;//任务ID号

    @DatabaseField
    private String branchname; //网点名称

    @DatabaseField
    private String branchcode;//网点编码


    @DatabaseField
    private String customername; //客户名称


    @DatabaseField
    private String departmentname; //车辆所属部门

    @DatabaseField
    private int tasktype;//任务类型  1 派件 2 收件 3 送零

    @DatabaseField
    private String tasktime; //时间

    @DatabaseField
    private String dispatchstate = "Z"; //调度状态  Z 正常  A(add)新增  I(in)转入   O(out)转出  C(cancle)取消

    @DatabaseField
    private String taskstate = "N"; //任务状态 N 未完成  Y 已完成  I(in)待接收   O(out)待转出

    @DatabaseField
    private String fromdetial;//from详情 网点名称和地址

    @DatabaseField
    private String todetial;//to 详情 网点名称和地址

    @DatabaseField
    private boolean ispasscar;//是否需要过车子

    @DatabaseField
    private String businesstype;//业务类型

    @DatabaseField
    private String operatetime;//操作时间

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

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }

    public String getBranchcode() {
        return branchcode;
    }

    public void setBranchcode(String branchcode) {
        this.branchcode = branchcode;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public int getTasktype() {
        return tasktype;
    }

    public void setTasktype(int tasktype) {
        this.tasktype = tasktype;
    }

    public String getTasktime() {
        return tasktime;
    }

    public void setTasktime(String tasktime) {
        this.tasktime = tasktime;
    }

    public String getDispatchstate() {
        return dispatchstate;
    }

    public void setDispatchstate(String dispatchstate) {
        this.dispatchstate = dispatchstate;
    }

    public String getTaskstate() {
        return taskstate;
    }

    public void setTaskstate(String taskstate) {
        this.taskstate = taskstate;
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
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

    public String getFromdetial() {
        return fromdetial;
    }

    public void setFromdetial(String fromdetial) {
        this.fromdetial = fromdetial;
    }

    public String getTodetial() {
        return todetial;
    }

    public void setTodetial(String todetial) {
        this.todetial = todetial;
    }

    public boolean ispasscar() {
        return ispasscar;
    }

    public void setIspasscar(boolean ispasscar) {
        this.ispasscar = ispasscar;
    }

    public String getBusinesstype() {
        return businesstype;
    }

    public void setBusinesstype(String businesstype) {
        this.businesstype = businesstype;
    }
}
