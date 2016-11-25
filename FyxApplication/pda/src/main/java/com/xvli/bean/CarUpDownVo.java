package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 物品上下车记录
 */
@DatabaseTable
public class CarUpDownVo implements Serializable
{
    @DatabaseField(generatedId = true)
    private int id;
    
    //物品类型，钞箱("0")，钞袋("1")，卡钞("2")，废钞("3") ，如果是计划之外的物品也不在基础数据中则为-1   5 扎带   7 TEBAG(拉链包)
    @DatabaseField
    private String itemtype;
    
    //二维码编号
    @DatabaseField
    private String barCode;
    
    //操作类型， ON/OFF
    @DatabaseField
    private String operatetype;
    
    //操作人，可谓空
    @DatabaseField
    private String operator;
    
    //操作时间
    @DatabaseField
    private String operatetime;
    
    //车辆的GIS位置
    @DatabaseField
    private String gisx;
    @DatabaseField
    private String gisy;
    @DatabaseField
    private String gisz;
    
    //任务id号
    @DatabaseField
    private String taskinfoid;

    @DatabaseField
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @DatabaseField
    private String clientid;
    
  //atmId信息，如果是计划之外的则为-1
    @DatabaseField
    private String atmid;

    @DatabaseField
    private String atmName;

    public String getAtmName() {
        return atmName;
    }

    public void setAtmName(String atmName) {
        this.atmName = atmName;
    }

    //branchid信息，如果是计划之外的则为-1
    @DatabaseField
    private String branchid;

    @DatabaseField
    private String moneyBag ; //钞包编码

    public String getMoneyBag() {
        return moneyBag;
    }

    public void setMoneyBag(String moneyBag) {
        this.moneyBag = moneyBag;
    }

    //网点名称
    @DatabaseField
    private  String branchname;

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }

    //是否已经上传过
    @DatabaseField
    private String isUploaded="N";
    
  //是否已经点击上传按钮
    @DatabaseField
    private String isdoneok="N";

    @DatabaseField
    private String isScan = "N";//是否扫描

    public String getIsScan() {
        return isScan;
    }

    public void setIsScan(String isScan) {
        this.isScan = isScan;
    }

    //下车上车默认为0，上车完成后全部置为1
    @DatabaseField
    private String isonoffok="0";

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    //是否有效  N失效 Y有效
    @DatabaseField
    private String enabled = "Y";

    public CarUpDownVo() {
    }
    //上传数据数据时唯一标示符
    @DatabaseField
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
	public String getGisz() {
		return gisz;
	}
	public void setGisz(String gisz) {
		this.gisz = gisz;
	}
	public String getIsUploaded()
    {
        return isUploaded;
    }
    public void setIsUploaded(String isUploaded)
    {
        this.isUploaded = isUploaded;
    }
    public String getAtmid() {
		return atmid;
	}
	public void setAtmid(String atmid) {
		this.atmid = atmid;
	}
	public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public String getClientid()
    {
        return clientid;
    }
    public void setClientid(String clientid)
    {
        this.clientid = clientid;
    }
    public String getTaskinfoid()
    {
        return taskinfoid;
    }
    public void setTaskinfoid(String taskinfoid)
    {
        this.taskinfoid = taskinfoid;
    }
    public String getItemtype()
    {
        return itemtype;
    }
    public void setItemtype(String itemtype)
    {
        this.itemtype = itemtype;
    }
    public String getBarCode()
    {
        return barCode;
    }
    public void setBarCode(String barCode)
    {
        this.barCode = barCode;
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
	public String getBranchid() {
		return branchid;
	}
	public void setBranchid(String branchid) {
		this.branchid = branchid;
	}
	public String getIsdoneok() {
		return isdoneok;
	}
	public void setIsdoneok(String isdoneok) {
		this.isdoneok = isdoneok;
	}
	public String getIsonoffok() {
		return isonoffok;
	}
	public void setIsonoffok(String isonoffok) {
		this.isonoffok = isonoffok;
	}
    
	
    
}
