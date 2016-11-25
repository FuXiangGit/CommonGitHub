package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 动态故障任务列表显示项
 * 2016-03-04
 */
@DatabaseTable(tableName = "ATM_TROUB_VO")
public class ATMTroubleVo implements Serializable{

	private static final long serialVersionUID = 1L;

	public ATMTroubleVo() {
	}

	@DatabaseField(generatedId = true)
	private int ids;
//	{共10个字段
//		"id": "d413ab7e-17a8-4209-8f18-e5b9e3c49fa3",
//			"name": "ddd",
//			"code": "3",
//			"atmcustomerid": "2bfe346d-d7a2-4988-99bf-4462a140bb7d",
//			"order": 3,
//			"enabled": true,
//			"inputtypes": 1,
//			"isneeded": false,
//			"isphoto": false,
//			"selectitems": "44"
//	},
	//======================================================================
	// 主键ID
	@DatabaseField
	private String id;

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

	// 是否启用
	@DatabaseField
	private boolean enabled ;

	//检查项输入类型  0:正常异常   1:异常说明   2:说明  3: 日期时间 4:日期  5:时间  6:签名
	@DatabaseField
	private int inputtypes ;

    // 是否启用
	@DatabaseField
	private boolean isneeded;

	// 是否拍照
	@DatabaseField
	private boolean isphoto;
	// 选择项列表
	@DatabaseField
	private String selectitems;

	//======================================================================
	@DatabaseField
	private String clientid;
	// 任务id号
	@DatabaseField
	private String taskid;

	// atm id号
	@DatabaseField
	private String atmid;

	@DatabaseField
	private String atmno;

	@DatabaseField
	private String branchname;

	@DatabaseField
	private String branchid;

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
	// 是否异常Y异常，N正常
	@DatabaseField
	private String operonoff = "Y";
	// 操作的备注，填写文本
	@DatabaseField
	private String opercontent="";

	@DatabaseField
	private int  singItem;//单选项选择内容

	@DatabaseField
	private String  signphoto;//签名照片

	public String getIsRegister() {
		return isRegister;
	}

	public void setIsRegister(String isRegister) {
		this.isRegister = isRegister;
	}

	//是否登记  Y 已登记  N 未登记
	@DatabaseField

	private String isRegister ="N" ;

	public ATMTroubleVo(String atmCustomerId, String atmid, String clientid, String code, boolean enabled, String id, int ids, int inputtypes, String isCanUp, String isDone, boolean isneeded, boolean isphoto, String isUploaded, String name, String operatedtime, String operator, int order, String selectitems, String taskid) {
		this.atmCustomerId = atmCustomerId;
		this.atmid = atmid;
		this.clientid = clientid;
		this.code = code;
		this.enabled = enabled;
		this.id = id;
		this.ids = ids;
		this.inputtypes = inputtypes;
		this.isCanUp = isCanUp;
		this.isDone = isDone;
		this.isneeded = isneeded;
		this.isphoto = isphoto;
		this.isUploaded = isUploaded;
		this.name = name;
		this.operatedtime = operatedtime;
		this.operator = operator;
		this.order = order;
		this.selectitems = selectitems;
		this.taskid = taskid;
	}

	public String getAtmCustomerId() {
		return atmCustomerId;
	}

	public void setAtmCustomerId(String atmCustomerId) {
		this.atmCustomerId = atmCustomerId;
	}

	public String getAtmid() {
		return atmid;
	}

	public void setAtmid(String atmid) {
		this.atmid = atmid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIds() {
		return ids;
	}

	public void setIds(int ids) {
		this.ids = ids;
	}

	public int getInputtypes() {
		return inputtypes;
	}

	public void setInputtypes(int inputtypes) {
		this.inputtypes = inputtypes;
	}

	public String getIsCanUp() {
		return isCanUp;
	}

	public void setIsCanUp(String isCanUp) {
		this.isCanUp = isCanUp;
	}

	public String getIsDone() {
		return isDone;
	}

	public void setIsDone(String isDone) {
		this.isDone = isDone;
	}

	public boolean isneeded() {
		return isneeded;
	}

	public void setIsneeded(boolean isneeded) {
		this.isneeded = isneeded;
	}

	public boolean isphoto() {
		return isphoto;
	}

	public void setIsphoto(boolean isphoto) {
		this.isphoto = isphoto;
	}

	public String getIsUploaded() {
		return isUploaded;
	}

	public void setIsUploaded(String isUploaded) {
		this.isUploaded = isUploaded;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getSelectitems() {
		return selectitems;
	}

	public void setSelectitems(String selectitems) {
		this.selectitems = selectitems;
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

	public String getBranchid() {
		return branchid;
	}

	public void setBranchid(String branchid) {
		this.branchid = branchid;
	}

	public String getBranchname() {
		return branchname;
	}

	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}

	public String getAtmno() {
		return atmno;
	}

	public void setAtmno(String atmno) {
		this.atmno = atmno;
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

	public String getSignphoto() {
		return signphoto;
	}

	public void setSignphoto(String signphoto) {
		this.signphoto = signphoto;
	}

	public int getSelectItem() {
		return singItem;
	}

	public void setSelectItem(int selectItem) {
		this.singItem = selectItem;
	}
}
