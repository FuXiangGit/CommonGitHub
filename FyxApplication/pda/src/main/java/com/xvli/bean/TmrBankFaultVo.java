package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 故障登记信息
 */
@DatabaseTable(tableName = "ATM_BANKFAULT_VO")
public class TmrBankFaultVo {

	@DatabaseField(generatedId = true)
	private int ids;

	// 任务id号
	@DatabaseField
	private String taskid;

	// atm id号
	@DatabaseField
	private String atmid;
	// 网点id
	@DatabaseField
	private String branchid;
	// 操作时间
	@DatabaseField
	private String operatedtime;

	// 操作人，jobnumber用逗号隔开
	@DatabaseField
	private String operator;

	//故障时间
	@DatabaseField
	private String faulttime;

	// 故障等级
	@DatabaseField
	private int faultlevel;
	// 故障详细信息
	@DatabaseField
	private String faultmessages;


	// 到场时间
	@DatabaseField
	private String arrivaltime;

	// 工程师到场时间
	@DatabaseField
	private String engineersarrivetime;

	// 修复时间
	@DatabaseField
	private String timetorepair;
	// 故障原因
	@DatabaseField
	private String failurecause;

	// 修复措施
	@DatabaseField
	private String repairmeasures;

	// 未修复原因
	@DatabaseField
	private String notrepaircause;
	// 工程师签名照
	@DatabaseField
	private String enginephoto;

	// 备注
	@DatabaseField
	private String remarks;

	// 工程师预约时间
	@DatabaseField
	private String orderedtime ;

	@DatabaseField
	private String clientid;
	//是否修复
	@DatabaseField
	private Boolean isrepaired = false;

	@DatabaseField
	private String isUploaded = "N";

	@DatabaseField
	private String otherremark;

	// 故障没有修复 则不需要上传维修相关数据 N为不需要上传 Y为要上传
	@DatabaseField
	private String noneed = "N";


	//是否登记  Y 已登记  N 未登记
	//上传数据数据时唯一标示符
	@DatabaseField
	private String uuid;


	//存放位置
	@DatabaseField
	private String cardlocation;//针对于迪堡卡钞存放位置 和故障项一块上传

	public String getCardlocation() {
		return cardlocation;
	}

	public void setCardlocation(String cardlocation) {
		this.cardlocation = cardlocation;
	}

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

	@DatabaseField
	private String isRegister ="N" ;

	@DatabaseField
	private String  Result;//维修结果

	public int getFaultlevel() {
		return faultlevel;
	}

	public void setFaultlevel(int faultlevel) {
		this.faultlevel = faultlevel;
	}

	public String getFaultmessages() {
		return faultmessages;
	}

	public void setFaultmessages(String faultmessages) {
		this.faultmessages = faultmessages;
	}



	public String getArrivaltime() {
		return arrivaltime;
	}

	public void setArrivaltime(String arrivaltime) {
		this.arrivaltime = arrivaltime;
	}

	public String getTimetorepair() {
		return timetorepair;
	}

	public void setTimetorepair(String timetorepair) {
		this.timetorepair = timetorepair;
	}

	public String getFailurecause() {
		return failurecause;
	}

	public void setFailurecause(String failurecause) {
		this.failurecause = failurecause;
	}

	public String getRepairmeasures() {
		return repairmeasures;
	}

	public void setRepairmeasures(String repairmeasures) {
		this.repairmeasures = repairmeasures;
	}

	public String getNotrepaircause() {
		return notrepaircause;
	}

	public void setNotrepaircause(String notrepaircause) {
		this.notrepaircause = notrepaircause;
	}

	public String getEnginephoto() {
		return enginephoto;
	}

	public void setEnginephoto(String enginephoto) {
		this.enginephoto = enginephoto;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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


	public String getOrderedtime() {
		return orderedtime;
	}

	public void setOrderedtime(String orderedtime) {
		this.orderedtime = orderedtime;
	}

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

	public String getIsUploaded() {
		return isUploaded;
	}

	public void setIsUploaded(String isUploaded) {
		this.isUploaded = isUploaded;
	}


	public String getEngineersarrivetime() {
		return engineersarrivetime;
	}

	public void setEngineersarrivetime(String engineersarrivetime) {
		this.engineersarrivetime = engineersarrivetime;
	}

	public String getNoneed() {
		return noneed;
	}

	public void setNoneed(String noneed) {
		this.noneed = noneed;
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

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public Boolean getIsrepaired() {
		return isrepaired;
	}

	public void setIsrepaired(Boolean isrepaired) {
		this.isrepaired = isrepaired;
	}
}
