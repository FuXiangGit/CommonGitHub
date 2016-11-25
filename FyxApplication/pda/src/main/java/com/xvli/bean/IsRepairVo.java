package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 是否维修
 */
@DatabaseTable(tableName = "IS_REPAIR_VO")
public class IsRepairVo {
	@DatabaseField(generatedId = true)
	private int ids;

	@DatabaseField
	private String id;

	@DatabaseField
	private String clientid;

	@DatabaseField
	private String faultmessages;

	// 客户id号
	@DatabaseField
	private String atmcustomerid;

	// 是否异常 ON   为正常 Y   OFF  为异常N
	@DatabaseField
	private String operonoff = "Y";
	// 操作的备注，填写文本
	@DatabaseField
	private String opercontent="";
	// 计划外的任务ID 为0
	@DatabaseField
	private String taskid;

	// 网点id
	@DatabaseField
	private String branchid;

	// 网点id
	@DatabaseField
	private String branchname;

	// 机具号
	@DatabaseField
	private String atmnumber;

	// 机具id
	@DatabaseField
	private String atmid;

	@DatabaseField
	private String name;

	@DatabaseField
	private String code;

	@DatabaseField
	private String order;

	@DatabaseField
	private String operator;

	// 故障时间
	@DatabaseField
	private String faulttime;


	// 其他
	@DatabaseField
	private String otherremark;

	// 是否上传
	@DatabaseField
	private String isupload = "N";
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

	public IsRepairVo() {
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}


	public String getAtmnumber() {
		return atmnumber;
	}

	public void setAtmnumber(String atmnumber) {
		this.atmnumber = atmnumber;
	}



	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getFaulttime() {
		return faulttime;
	}

	public void setFaulttime(String faulttime) {
		this.faulttime = faulttime;
	}


	public String getOtherremark() {
		return otherremark;
	}

	public void setOtherremark(String otherremark) {
		this.otherremark = otherremark;
	}


	public String getIsupload() {
		return isupload;
	}

	public void setIsupload(String isupload) {
		this.isupload = isupload;
	}

	public String getAtmid() {
		return atmid;
	}

	public void setAtmid(String atmid) {
		this.atmid = atmid;
	}

	public String getFaultmessages() {
		return faultmessages;
	}

	public void setFaultmessages(String faultmessages) {
		this.faultmessages = faultmessages;
	}

	public String getBranchid() {
		return branchid;
	}

	public void setBranchid(String branchid) {
		this.branchid = branchid;
	}

	public String getAtmcustomerid() {
		return atmcustomerid;
	}

	public void setAtmcustomerid(String atmcustomerid) {
		this.atmcustomerid = atmcustomerid;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getOpercontent() {
		return opercontent;
	}

	public void setOpercontent(String opercontent) {
		this.opercontent = opercontent;
	}

	public String getOperonoff() {
		return operonoff;
	}

	public void setOperonoff(String operonoff) {
		this.operonoff = operonoff;
	}
}
