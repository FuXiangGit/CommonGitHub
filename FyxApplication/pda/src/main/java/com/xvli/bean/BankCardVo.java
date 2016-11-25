package com.xvli.bean;

import java.io.Serializable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 保存银行吞没卡信息
 */
@DatabaseTable(tableName = "BANK_CARD_VO")
public class BankCardVo implements Serializable
{

    private static final long serialVersionUID = 1L;

    @DatabaseField(generatedId = true)
    private int id;
    
    //网点名称
    @DatabaseField
    private String  branchid;
    //atm编号
    @DatabaseField
    private String  atmid;
    //银行卡号
    @DatabaseField
    private String cardno;
    //是否本行
    @DatabaseField
    private String isown;
    //操作时间
    @DatabaseField
    private String operatetime;
    //操作人
    @DatabaseField
    private String operator;
    //备注
    @DatabaseField
    private String remark;
    //图片
    @DatabaseField
    private String photo;

    //任务id号
    @DatabaseField
    private String taskid;

    @DatabaseField
    private String clientid;
    
  //是否已经上传过
    @DatabaseField
    private String isUploaded="N";
    
    //是否有效
    @DatabaseField
    private String isYouXiao="Y";
    //上传数据数据时唯一标示符
    @DatabaseField
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
    public String getClientid()
    {
        return clientid;
    }
    public void setClientid(String clientid)
    {
        this.clientid = clientid;
    }
    


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getCardno() {
      return this.cardno;
    }
    public String getIsown() {
      return this.isown;
    }
    public String getOperator() {
      return this.operator;
    }
    public String getRemark() {
      return this.remark;
    }
    public String getPhoto() {
      return this.photo;
    }
    
    public void setCardno(String cardno) {
      this.cardno = cardno;
    }
    public void setIsown(String isown) {
      this.isown = isown;
    }
    public void setOperator(String operator) {
      this.operator = operator;
    }
    public void setRemark(String remark) {
      this.remark = remark;
    }
    public void setPhoto(String photo) {
      this.photo = photo;
    }
    public String getOperatetime()
    {
        return operatetime;
    }
    public void setOperatetime(String operatetime)
    {
        this.operatetime = operatetime;
    }
	public String getIsYouXiao() {
		return isYouXiao;
	}
	public void setIsYouXiao(String isYouXiao) {
		this.isYouXiao = isYouXiao;
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
}
