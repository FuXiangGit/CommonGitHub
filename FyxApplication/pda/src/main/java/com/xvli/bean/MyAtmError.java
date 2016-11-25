package com.xvli.bean;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * 保存卡钞废钞
 *
 */
@DatabaseTable(tableName = "ATM_ERROR_VO")
public class MyAtmError implements Serializable
{

    private static final long serialVersionUID = 1L;
    
    
    @DatabaseField(generatedId = true)
    private int id;
    
    /**
     * 二维码
     */
    @DatabaseField
    private String code;
    
    /**
     * 金额
     */
    @DatabaseField
    private long moneyamount;
    
  //网点
    @DatabaseField
    private String branchid;

    //网点
    @DatabaseField
    private String branchname;
    
    //atmid
    @DatabaseField
    private String atmid;
    
  //atm编号
    @DatabaseField
    private String atmno;
    
    //物品类型，卡钞(2)，废钞(3)
    @DatabaseField
    private String itemtype;
    
    //位置
    @DatabaseField
    private String location;
    
    //任务id号

    @DatabaseField
    private String taskid;
    
    @DatabaseField
    private String clientid;
    
    //操作时间
    @DatabaseField
    private String operatetime;
    
    @DatabaseField
    private String isScan="N";
    
    //是否已经上传过
    @DatabaseField
    private String isUploaded="N";

    
    //卡钞废钞选择的时间
    @DatabaseField
    private String stucktime;
    
    //卡钞废钞是否有效
    @DatabaseField
    private String isYouXiao = "Y";
    
    //车辆的GIS位置
    @DatabaseField
    private String gisx;
    @DatabaseField
    private String gisy;
    @DatabaseField
    private String gisz;

    @DatabaseField
    private String isback;//针对于迪堡卡钞是否带回 Y为带回  N为不带回  默认是带回的

    @DatabaseField
    private boolean isgeton = false; // 是否上车   false 为未上车
    //上传数据数据时唯一标示符
    @DatabaseField
    private String uuid;
    //泰国项目 回库时用到的扎带
    @DatabaseField
    private String boxcoderecycle;

    //钞包编码   针对于迪堡钞包存放钞箱和废钞问题
    @DatabaseField
    private String moneyBag ;

    @DatabaseField
    private String lineid ;//泰国项目上传是需要线路id

    public String getLineid() {
        return lineid;
    }

    public void setLineid(String lineid) {
        this.lineid = lineid;
    }

    public String getBoxcoderecycle() {
        return boxcoderecycle;
    }

    public void setBoxcoderecycle(String boxcoderecycle) {
        this.boxcoderecycle = boxcoderecycle;
    }

    public String getMoneyBag() {
        return moneyBag;
    }

    public void setMoneyBag(String moneyBag) {
        this.moneyBag = moneyBag;
    }

    public String getIsback() {
        return isback;
    }

    public void setIsback(String isback) {
        this.isback = isback;
    }

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

	public String getStucktime() {
		return stucktime;
	}

	public void setStucktime(String stucktime) {
		this.stucktime = stucktime;
	}

	public String getIsScan() {
		return isScan;
	}

	public void setIsScan(String isScan) {
		this.isScan = isScan;
	}

	public String getIsUploaded() {
		return isUploaded;
	}

	public void setIsUploaded(String isUploaded) {
		this.isUploaded = isUploaded;
	}

	public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getAtmno()
    {
        return atmno;
    }

    public void setAtmno(String atmno)
    {
        this.atmno = atmno;
    }

    public String getOperatetime()
    {
        return operatetime;
    }

    public void setOperatetime(String operatetime)
    {
        this.operatetime = operatetime;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }


    public long getMoneyamount()
    {
        return moneyamount;
    }

    public void setMoneyamount(long moneyamount)
    {
        this.moneyamount = moneyamount;
    }

    public String getBranchid()
    {
        return branchid;
    }

    public void setBranchid(String branchid)
    {
        this.branchid = branchid;
    }

    public String getAtmid()
    {
        return atmid;
    }

    public void setAtmid(String atmid)
    {
        this.atmid = atmid;
    }


    public String getItemtype()
    {
        return itemtype;
    }

    public void setItemtype(String itemtype)
    {
        this.itemtype = itemtype;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getClientid()
    {
        return clientid;
    }

    public void setClientid(String clientid)
    {
        this.clientid = clientid;
    }

	public String getIsYouXiao() {
		return isYouXiao;
	}

	public void setIsYouXiao(String isYouXiao) {
		this.isYouXiao = isYouXiao;
	}


    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }

    public boolean isgeton() {
        return isgeton;
    }

    public void setIsgeton(boolean isgeton) {
        this.isgeton = isgeton;
    }
}
