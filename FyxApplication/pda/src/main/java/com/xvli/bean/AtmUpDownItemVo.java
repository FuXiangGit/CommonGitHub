package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 物品上下atm机具记录
 * 
 * @author guxuehua
 * 
 *         下午3:41:55
 */
@DatabaseTable
public class AtmUpDownItemVo implements Serializable
{
    @DatabaseField(generatedId = true)
    private int    id;

    // 网点名称
    @DatabaseField
    private String  branchid;

    //
    @DatabaseField
    private String  atmid;

    // 物品类型，卡钞，废钞，钞箱，钞袋（-1为任务外的，也不在基础数据中的）0为钞箱，1为钞袋    2 卡钞  3 废钞
    @DatabaseField
    private String itemtype;

    // 二维码
    @DatabaseField
    private String barcode;

    // 操作类型 UP/DOWN
    @DatabaseField
    private String operatetype;

    // 操作人
    @DatabaseField
    private String operator;

    // 操作时间
    @DatabaseField
    private String operatetime;

    // 备注
    @DatabaseField
    private String remark;

    // 位置
    @DatabaseField
    private String location;

    // 任务id号
    @DatabaseField
    private String taskinfoid;

    @DatabaseField
    private String clientid;

    // 金额
    @DatabaseField
    private long   moneyamount = 0;

    // 是否已经上传过
    @DatabaseField
    private String isUploaded  = "N";
    
    //选择的时间
    @DatabaseField
    private String stucktime;
    
    //atm机具上下钞箱是否有效
    @DatabaseField
    private String isYouXiao="Y";

    @DatabaseField
    private String moneyBag ; //钞包编码

    @DatabaseField
    private String boxcoderecycle;//扎袋编码

    public String getBoxcoderecycle() {
        return boxcoderecycle;
    }

    public void setBoxcoderecycle(String boxcoderecycle) {
        this.boxcoderecycle = boxcoderecycle;
    }

    @DatabaseField
    private String lineid;//线路id

    public String getLineid() {
        return lineid;
    }

    public void setLineid(String lineid) {
        this.lineid = lineid;
    }

    public String getMoneyBag() {
        return moneyBag;
    }

    public void setMoneyBag(String moneyBag) {
        this.moneyBag = moneyBag;
    }

    //钞箱抄袋对应的运送和回收状态（运送状态0，回收状态1）
    @DatabaseField
    private int sendOrRecycle;
    //上传数据数据时唯一标示符
    @DatabaseField
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getSendOrRecycle() {
        return sendOrRecycle;
    }

    public void setSendOrRecycle(int sendOrRecycle) {
        this.sendOrRecycle = sendOrRecycle;
    }

    //卸下和装上物品不匹配原因
    @DatabaseField
    private String reasion;

    public AtmUpDownItemVo() {
    }

    public String getStucktime() {
		return stucktime;
	}

	public void setStucktime(String stucktime) {
		this.stucktime = stucktime;
	}

	public String getIsUploaded()
    {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded)
    {
        this.isUploaded = isUploaded;
    }

    public String getClientid()
    {
        return clientid;
    }

    public long getMoneyamount()
    {
        return moneyamount;
    }

    public void setMoneyamount(long moneyamount)
    {
        this.moneyamount = moneyamount;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getItemtype()
    {
        return itemtype;
    }

    public void setItemtype(String itemtype)
    {
        this.itemtype = itemtype;
    }

    public void setClientid(String clientid)
    {
        this.clientid = clientid;
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

    public String getTaskinfoid()
    {
        return taskinfoid;
    }

    public void setTaskinfoid(String taskinfoid)
    {
        this.taskinfoid = taskinfoid;
    }

    // public long getMoneyamount()
    // {
    // return moneyamount;
    // }
    // public void setMoneyamount(long moneyamount)
    // {
    // this.moneyamount = moneyamount;
    // }
    public String getBarcode()
    {
        return barcode;
    }

    public void setBarcode(String barcode)
    {
        this.barcode = barcode;
    }

    public String getOperatetype()
    {
        return operatetype;
    }

    public void setOperatetype(String operatetype)
    {
        this.operatetype = operatetype;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getOperatetime()
    {
        return operatetime;
    }

    public void setOperatetime(String operatetime)
    {
        this.operatetime = operatetime;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

	public String getReasion() {
		return reasion;
	}

	public void setReasion(String reasion) {
		this.reasion = reasion;
	}

	public String getIsYouXiao() {
		return isYouXiao;
	}

	public void setIsYouXiao(String isYouXiao) {
		this.isYouXiao = isYouXiao;
	}


    //网点名称
    @DatabaseField
    private String branchname;

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }
}