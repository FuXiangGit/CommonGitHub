package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 16:21.
 */
@DatabaseTable(tableName = "TMR_CYCLEITEMVALUE_VO")
public class DynCycleItemValueVo implements Serializable {

    private static final long serialVersionUID = 1L;

    public DynCycleItemValueVo() {
    }

    @DatabaseField(generatedId = true)
    private int ids;

    //======================================================================
    // 主键ID
    @DatabaseField
    private String id;

    @DatabaseField
    private String clientid ;

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    //检查项名称
    @DatabaseField
    private String name ;

    // 检查项编码
    @DatabaseField
    private String code ;

    //所属客户
    @DatabaseField
    private String atmCustomerId ;
    //顺序
    @DatabaseField
    private int order ;


    //机具条形码
    @DatabaseField
    private String barcode;

    // 是否启用
    @DatabaseField
    private boolean enabled ;

    // 是否启用
    @DatabaseField
    private boolean isneeded;


    //保存用户输入信息Value
    @DatabaseField

    private String Value;



    //======================================================================
    // 任务id号
    @DatabaseField
        private String taskid;

    // atm id号
    @DatabaseField
    private String  atmid;
    //上传数据数据时唯一标示符
    @DatabaseField
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAtmCustomerId() {
        return atmCustomerId;
    }

    public void setAtmCustomerId(String atmCustomerId) {
        this.atmCustomerId = atmCustomerId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isneeded() {
        return isneeded;
    }

    public void setIsneeded(boolean isneeded) {
        this.isneeded = isneeded;
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

    public String getBranchid() {
        return branchid;
    }

    public void setBranchid(String branchid) {
        this.branchid = branchid;
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

    public String getIsDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public String getIsCanUp() {
        return isCanUp;
    }

    public void setIsCanUp(String isCanUp) {
        this.isCanUp = isCanUp;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }


    // 网点 id号
    @DatabaseField

    private String  branchid;

    // 操作时间
    @DatabaseField
    private String operatedtime;

    // 操作人，jobnumber用逗号隔开
    @DatabaseField
    private String operator;


    @DatabaseField
    private String isUploaded = "N";

    //atm 是否完成
    @DatabaseField
    private String isDone = "N";

    //atm 是否可以上传
    @DatabaseField
    private String isCanUp = "N";

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    //余额
    @DatabaseField
    private String balance;

    //批次号
    @DatabaseField
    private String cycleNo;

    public String getCycleNo() {
        return cycleNo;
    }

    public void setCycleNo(String cycleNo) {
        this.cycleNo = cycleNo;
    }

    public String getWithdrawamount() {
        return withdrawamount;
    }

    public void setWithdrawamount(String withdrawamount) {
        this.withdrawamount = withdrawamount;
    }

    public String getDepositamount() {
        return depositamount;
    }

    public void setDepositamount(String depositamount) {
        this.depositamount = depositamount;
    }

    //存款金额
    @DatabaseField
    private String depositamount;

    @DatabaseField
    private String withdrawamount;



}
