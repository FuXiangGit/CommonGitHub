package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 网点巡检  机具巡检 故障拍照保存类
 */
@DatabaseTable(tableName = "TMR_PHOTO_VO")
public class TmrPhotoVo {
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
	private String operatedtime;

	// 操作人
	@DatabaseField
	private String operator;

	@DatabaseField(unique = true)
	private String phonepath;

	@DatabaseField
	private String remarks;

	@DatabaseField
	private String isUploaded = "N";
	
	//照片是否上传
	@DatabaseField
	private String isphotoUploaded = "N";
	
	//这一故障拍了多少张照片
	@DatabaseField 
	private String typecount;

	//网点 检查 和机具检查 照片 存储 1为 网点   0为机具
	@DatabaseField
	private int  storagetype;

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

	public String getPhonepath() {
		return phonepath;
	}

	public void setPhonepath(String phonepath) {
		this.phonepath = phonepath;
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

	public String getIsphotoUploaded() {
		return isphotoUploaded;
	}

	public void setIsphotoUploaded(String isphotoUploaded) {
		this.isphotoUploaded = isphotoUploaded;
	}

	public String getTypecount() {
		return typecount;
	}

	public void setTypecount(String typecount) {
		this.typecount = typecount;
	}

	public int getStoragetype() {
		return storagetype;
	}

	public void setStoragetype(int storagetype) {
		this.storagetype = storagetype;
	}
}
