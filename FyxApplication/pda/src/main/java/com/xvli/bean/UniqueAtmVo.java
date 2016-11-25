package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Atm机具唯一信息表
 */
@DatabaseTable(tableName="UNIQUE_ATM")
public class UniqueAtmVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;
    //Atm id号
    @DatabaseField
    private String atmid;

    @DatabaseField
    private String clientid;

    //网点代码
    @DatabaseField
    private String branchbacode;

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

    //对应任务操作类型  加钞（日常作业，清机加钞，加钞，清机，撤钞）
    @DatabaseField
    private int operationtype;


    //网点名称
    @DatabaseField
    private String branchname;

    //客户ID号
    @DatabaseField
    private String customerid;

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
    //该机具对应的检查项是否完成
    @DatabaseField
    private String isroutdone = "N";
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

    @DatabaseField
    private boolean isfixed;

    //是否有吞没卡
    @DatabaseField
    private String isbankcard = "N";   //0 是  1 否

    //是否进行现场维修维修
    @DatabaseField
    private String isrepair = "N";//0 是  1 否

    @DatabaseField
    private String gisx;
    @DatabaseField
    private String gisy;
    @DatabaseField
    private String operationname;

    @DatabaseField
    private String  customername;

    @DatabaseField
    private String isUploaded = "N";
    //对应atm是否完成
    @DatabaseField
    private String isatmdone = "N";//Y 为已完成 N 为未完成  R为撤销(Revoke)    C为变更(change)   A 为新增（add）

    //是否登记  Y 已登记  N 未登记
    @DatabaseField
    private String isRegister ="N" ;

    //检查项登记 Y 已登记  N 未登记
    @DatabaseField
    private String isRegisterCheck ="N" ;


    //机具所属线路名称
    @DatabaseField
    private String linenumber;
    //机具的网点所属线路名字
    @DatabaseField
    private String branchlinenumber;

    //是否是撤销任务  如果atm 下的所有任务都撤销 则该 atm也不用显示 Y为是取消时任   N 为没取消需要显示
    @DatabaseField
    private String iscancel ="N";

    @DatabaseField
    private String moneyBag ; //钞包编码

    //泰国项目需求拉链包  用于存放回收的零钱
    @DatabaseField
    private String  zipperbag;

    //泰国项目 线路id  上下级机具数据上传时用到
    @DatabaseField
    private String linenchid;

    @DatabaseField
    private String boxcoderecycle;

    public String getBoxcoderecycle() {
        return boxcoderecycle;
    }

    public void setBoxcoderecycle(String boxcoderecycle) {
        this.boxcoderecycle = boxcoderecycle;
    }

    public String getLinenchid() {
        return linenchid;
    }

    public void setLinenchid(String linenchid) {
        this.linenchid = linenchid;
    }

    public String getZipperbag() {
        return zipperbag;
    }

    public void setZipperbag(String zipperbag) {
        this.zipperbag = zipperbag;
    }

    public String getBranchlinenumber() {
        return branchlinenumber;
    }

    public void setBranchlinenumber(String branchlinenumber) {
        this.branchlinenumber = branchlinenumber;
    }

    public String getMoneyBag() {
        return moneyBag;
    }

    public void setMoneyBag(String moneyBag) {
        this.moneyBag = moneyBag;
    }

    public String getIsRegisterCheck() {
        return isRegisterCheck;
    }

    public void setIsRegisterCheck(String isRegisterCheck) {
        this.isRegisterCheck = isRegisterCheck;
    }

    // 操作时间
    @DatabaseField
    private String operatedtime;

    // 操作人，jobnumber用逗号隔开
    @DatabaseField
    private String operator;

    // 是否是基础表里获取出来的机具 N 为不是  Y为是
    @DatabaseField
    private String isbase = "N";
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

    public UniqueAtmVo() {
    }

    public String getIsbankcard() {
        return isbankcard;
    }

    public void setIsbankcard(String isbankcard) {
        this.isbankcard = isbankcard;
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



    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
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

    public String getIsrepair() {
        return isrepair;
    }

    public void setIsrepair(String isrepair) {
        this.isrepair = isrepair;
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

    public String getOperationname() {
        return operationname;
    }

    public void setOperationname(String operationname) {
        this.operationname = operationname;
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

    public String getIsroutdone() {
        return isroutdone;
    }

    public void setIsroutdone(String isroutdone) {
        this.isroutdone = isroutdone;
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

    public String getIsbase() {
        return isbase;
    }

    public void setIsbase(String isbase) {
        this.isbase = isbase;
    }

    public String getLinenumber() {
        return linenumber;
    }

    public void setLinenumber(String linenumber) {
        this.linenumber = linenumber;
    }

    public String getIscancel() {
        return iscancel;
    }

    public void setIscancel(String iscancle) {
        this.iscancel = iscancle;
    }
}