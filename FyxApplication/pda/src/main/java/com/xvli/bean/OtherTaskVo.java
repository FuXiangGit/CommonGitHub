package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 其他任务详情
 * 
 */
@DatabaseTable(tableName = "OTHER_TASK_VO")
public class OtherTaskVo {

	@DatabaseField(generatedId = true)
	private int ids;

	/*// 网点名称
	@DatabaseField
	private String branchid;*/

	@DatabaseField
	private String clientid;
	// 操作人
	@DatabaseField
	private String operator;

	// 区分唯一性
	@DatabaseField
	private String branchname;

	//线路
	@DatabaseField
	private String linenumber;
	@DatabaseField
	private String taskid;
/*	@DatabaseField
	private String branchbacode;
	@DatabaseField
	private String branchtypes;*/
	@DatabaseField
	private String customerid;
/*	@DatabaseField
	private String atmid;
	@DatabaseField
	private String atmbarcode;

	@DatabaseField
	private String branchaddress;*/

	@DatabaseField
	private String taskcontent;

	@DatabaseField
	private String results;

	@DatabaseField
	private String arrivaltime;

	@DatabaseField
	private String leavetime;

	@DatabaseField
	private String isUploaded = "N";
	
	@DatabaseField
	private String isCan = "N";//N为不能上传  Y为可以上传

	@DatabaseField
	private String isDone = "N";

	@DatabaseField
	private String isexist ;//是否已经存在
	@DatabaseField
	private String gisx;
	@DatabaseField
	private String gisy;

    //	目的地
	@DatabaseField
	private String destination;

	//任务说明
	@DatabaseField
	private String taskinfo;

	//address
	@DatabaseField
	private String address;//目的地地址




	//GPS坐标海拔高度
	@DatabaseField
	private String gisz;
	//上传数据数据时唯一标示符
	@DatabaseField
	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public OtherTaskVo() {
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

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getTaskcontent() {
		return taskcontent;
	}

	public void setTaskcontent(String taskcontent) {
		this.taskcontent = taskcontent;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public String getArrivaltime() {
		return arrivaltime;
	}

	public void setArrivaltime(String arrivaltime) {
		this.arrivaltime = arrivaltime;
	}

	public String getLeavetime() {
		return leavetime;
	}

	public void setLeavetime(String leavetime) {
		this.leavetime = leavetime;
	}

	public String getBranchname() {
		return branchname;
	}

	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}

	public String getIsUploaded() {
		return isUploaded;
	}

	public void setIsUploaded(String isUploaded) {
		this.isUploaded = isUploaded;
	}

	public String getIsCan() {
		return isCan;
	}

	public void setIsCan(String isCan) {
		this.isCan = isCan;
	}

	/*public String getBranchid() {
		return branchid;
	}

	public void setBranchid(String branchid) {
		this.branchid = branchid;
	}*/

	public String getLinenumber() {
		return linenumber;
	}

	public void setLinenumber(String linenumber) {
		this.linenumber = linenumber;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	/*public String getBranchbacode() {
		return branchbacode;
	}

	public void setBranchbacode(String branchbacode) {
		this.branchbacode = branchbacode;
	}

	public String getBranchtypes() {
		return branchtypes;
	}

	public void setBranchtypes(String branchtypes) {
		this.branchtypes = branchtypes;
	}*/

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	/*public String getAtmid() {
		return atmid;
	}

	public void setAtmid(String atmid) {
		this.atmid = atmid;
	}

	public String getAtmbarcode() {
		return atmbarcode;
	}

	public void setAtmbarcode(String atmbarcode) {
		this.atmbarcode = atmbarcode;
	}

	public String getBranchaddress() {
		return branchaddress;
	}

	public void setBranchaddress(String branchaddress) {
		this.branchaddress = branchaddress;
	}*/

	public String getIsDone() {
		return isDone;
	}

	public void setIsDone(String isDone) {
		this.isDone = isDone;
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

	public String getIsexist() {
		return isexist;
	}

	public void setIsexist(String isexist) {
		this.isexist = isexist;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getTaskinfo() {
		return taskinfo;
	}

	public void setTaskinfo(String taskinfo) {
		this.taskinfo = taskinfo;
	}
}
