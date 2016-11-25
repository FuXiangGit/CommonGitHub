package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Atm机具信息
 */
@DatabaseTable(tableName="T_ATM_VO")
public class AtmVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;
    //Atm id号
    @DatabaseField
    private String atmid;

    @DatabaseField
    private String clientid;

    //网点代码
    @DatabaseField
    private String netcode;

    //网点id
    @DatabaseField
    private String branchid;

    //atm操作类型"atmtype": "4cd6e444-dfdf-493f-a433-bd390b569fe4",
    @DatabaseField
    private String atmtype;

    //  0:存款机  1:取款机    2：存取一体机  3：存取循环机    4：其他机器
    @DatabaseField
    private String atmjobtype;

    //对应任务id
    @DatabaseField
    private String taskid;

    //对应任务时间
    @DatabaseField
    private String tasktime;


    //对应任务类型 0：全天任务，1：上午任务，2：下午任务
    @DatabaseField
    private int tasktimetypes;

    //对应任务操作类型  加钞（日常作业：清机加钞，加钞，清机，撤钞）
    @DatabaseField
    private int operationtype;


    //网点名称
    @DatabaseField
    private String branchname;

    //网点code
    @DatabaseField
    private String branchbacode;


    //客户ID号
    @DatabaseField
    private String atmcustomerid;

    //机具条形码
    @DatabaseField
    private String barcode;
    //操作类型 1为巡检    0为作业任务(加钞任务)   2维修
    @DatabaseField
    private int tasktype;

    //编号
    @DatabaseField
    private String atmno;

    //地址
    @DatabaseField
    private String address;

    //备注
    @DatabaseField
    private String remarks;


    //报修时间
    @DatabaseField
    private String reporttime;

    //报修信息
    @DatabaseField
    private String reportcontent;

    //故障时间
    @DatabaseField
    private String errortime;

    //是否维修
    @DatabaseField
    private boolean isfixed;


    @DatabaseField
    private String gisx;
    @DatabaseField
    private String gisy;
    // 操作时间
    @DatabaseField
    private String operatedtime;

    // 操作人，jobnumber用逗号隔开
    @DatabaseField
    private String operator;
    //对应atm是否完成
    @DatabaseField
    private String isatmdone = "N"; //Y 为已完成 N 为未完成  R为撤销(Revoke)    C为变更(change)   A 为新增（add）  G未去（not Go)


    //
    @DatabaseField
    private String isUploaded = "N";


    @DatabaseField
    private int errorlevel;

    @DatabaseField
    private String operationname;

    @DatabaseField
    private String  customername;

    //是否登记  Y 已登记  N 未登记
    @DatabaseField
    private String isRegister ="N" ;

    //是否是撤销任务 N否  　Ｙ是
    @DatabaseField
    private String iscancel ="N" ;

    //机具所属线路名字
    @DatabaseField
    private String linenumber;


    //机具的网点所属线路名字
    @DatabaseField
    private String branchlinenumber;

    // 不等于0的  都需要提示
    @DatabaseField
    private String boxtag;

    @DatabaseField
    private String moneyBag ; //钞包编码 ，泰国项目出库扎带编码


    //泰国项目 线路id  上下级机具数据上传时用到
    @DatabaseField
    private String linenchid;

    @DatabaseField
    private String boxcoderecycle;
    @DatabaseField
    private String cardlocaton ; //卡钞存放位置

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

    public String getBranchlinenumber() {
        return branchlinenumber;
    }

    public void setBranchlinenumber(String branchlinenumber) {
        this.branchlinenumber = branchlinenumber;
    }

    public String getCardlocaton() {
        return cardlocaton;
    }

    public void setCardlocaton(String cardlocaton) {
        this.cardlocaton = cardlocaton;
    }

    public String getMoneyBag() {
        return moneyBag;
    }

    public void setMoneyBag(String moneyBag) {
        this.moneyBag = moneyBag;
    }

    public String getIsRegister() {
        return isRegister;
    }

    public void setIsRegister(String isRegister) {
        this.isRegister = isRegister;
    }


    public String getBoxtag() {
        return boxtag;
    }

    public void setBoxtag(String boxtag) {
        this.boxtag = boxtag;
    }

    public AtmVo() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getAtmtype() {
        return atmtype;
    }

    public void setAtmtype(String atmtype) {
        this.atmtype = atmtype;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getNetcode() {
        return netcode;
    }

    public void setNetcode(String netcode) {
        this.netcode = netcode;
    }

    public String getCustomerid() {
        return atmcustomerid;
    }

    public void setCustomerid(String customerid) {
        this.atmcustomerid = customerid;
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

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getIsatmdone() {
        return isatmdone;
    }

    public void setIsatmdone(String isatmdone) {
        this.isatmdone = isatmdone;
    }

    public int getOperationtype() {
        return operationtype;
    }

    public void setOperationtype(int operationtype) {
        this.operationtype = operationtype;
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

    public int getTasktimetypes() {
        return tasktimetypes;
    }

    public void setTasktimetypes(int tasktimetypes) {
        this.tasktimetypes = tasktimetypes;
    }

    public String getAtmjobtype() {
        return atmjobtype;
    }

    public void setAtmjobtype(String atmjobtype) {
        this.atmjobtype = atmjobtype;
    }

    public String getBranchid() {
        return branchid;
    }

    public void setBranchid(String branchid) {
        this.branchid = branchid;
    }

    public int getTasktype() {
        return tasktype;
    }

    public void setTasktype(int tasktype) {
        this.tasktype = tasktype;
    }

    public String getErrortime() {
        return errortime;
    }

    public void setErrortime(String errortime) {
        this.errortime = errortime;
    }

    public boolean isfixed() {
        return isfixed;
    }

    public void setIsfixed(boolean isfixed) {
        this.isfixed = isfixed;
    }

    public String getReportcontent() {
        return reportcontent;
    }

    public void setReportcontent(String reportcontent) {
        this.reportcontent = reportcontent;
    }

    public String getReporttime() {
        return reporttime;
    }

    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
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

    public int getErrorlevel() {
        return errorlevel;
    }

    public void setErrorlevel(int errorlevel) {
        this.errorlevel = errorlevel;
    }

    public String getOperationname() {
        return operationname;
    }

    public void setOperationname(String operationname) {
        this.operationname = operationname;
    }

    public String getAtmcustomerid() {
        return atmcustomerid;
    }

    public void setAtmcustomerid(String atmcustomerid) {
        this.atmcustomerid = atmcustomerid;
    }

    public String getBranchbacode() {
        return branchbacode;
    }

    public void setBranchbacode(String branchbacode) {
        this.branchbacode = branchbacode;
    }


    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getIscancel() {
        return iscancel;
    }

    public void setIscancel(String iscancel) {
        this.iscancel = iscancel;
    }

    public String getLinenumber() {
        return linenumber;
    }

    public void setLinenumber(String linenumber) {
        this.linenumber = linenumber;
    }
}