package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 动态检查项信息
 * 2016-03-04
 */
@DatabaseTable(tableName = "ATM_ROUT_VO")
public class ATMRouteVo implements Serializable{

	private static final long serialVersionUID = 1L;

	public ATMRouteVo() {
	}

	@DatabaseField(generatedId = true)
	private int ids;
	//  共 16个字段
//	{"id": "a89cd2af-fe8a-4ba8-8aa5-17e9cb35e7a5",
//			"name": "机具4",
//			"code": "3",
//			"atmcustomerid": "2bfe346d-d7a2-4988-99bf-4462a140bb7d",
//			"order": 4,
//			"enabled": true,
//			"isphoto": false,
//			"atmtype": "4cd6e444-dfdf-4
// 93f-a433-bd390b569fe4",
//			"isatmornode": true,
//			"atminstallationmethod": "4",
//			"atmnodetype": "12576aa9-b5b5-4e16-9f7f-6847f508c431",
//			"inputtype": 0,
//			"selectitems": "4",
//			"isoperatetask": true,
//			"isrepairtask": true,
//			"isroutetask": true}
	//======================================================================
	// 主键ID
	@DatabaseField
	private String id;

	//检查项名称
	@DatabaseField
	private String name ;
	//检查项简称
	@DatabaseField
	private String name_full ;
	// 检查项编码
	@DatabaseField
	private String code ;

	//所属客户
	@DatabaseField
	private String atmcustomerid ;
	//顺序
	@DatabaseField
	private int order ;

	// 是否启用
	@DatabaseField
	private boolean enabled ;

	//是否拍照0false 1 true
	@DatabaseField
	private boolean isphoto;

	//此检查项是机具还是网点
	@DatabaseField
	private boolean isatmornode;

	//ATM 安装方式
	@DatabaseField
	private int atminstallationmethod;

	//网点类型
	@DatabaseField
	private String atmnodetype;


	//检查项输入类型  0:正常异常   1:异常说明   2:说明(备注)  3: 日期时间(点击获取)   4:日期(选择) 5:签名   6照片  7 单选择项  8 多选项

	@DatabaseField
	private int inputtype ;

	// 选择项列表(下拉选择)
	@DatabaseField
	private String selectitems;

	//atm类型
	@DatabaseField
	private String atmtype;

	//======================================================================

	// 是否异常Y异常，N正常
	@DatabaseField
	private String operonoff = "Y";
	// 操作的备注，填写文本
	@DatabaseField
	private String opercontent="";

	@DatabaseField
	private String clientid;

	// 操作时间
	@DatabaseField
	private String operatedtime;

	// 操作人，jobnumber用逗号隔开
	@DatabaseField
	private String operator;


	@DatabaseField
	private String isUploaded = "N";

	//机具检查 是否完成
	@DatabaseField
	private String isDone = "N";

	//机具检查 是否可以上传
	@DatabaseField
	private String isCanUp = "N";

	//网点code
	@DatabaseField
	private String branchcode ;

	//机具code
	@DatabaseField
	private String barcode ;
	//网点名称
	@DatabaseField
	private String branchname;
	// 客户签名照片名称
	@DatabaseField
	private String signphoto;

	// 异常照片
	@DatabaseField
	private String errorphoto;
	// 异常照片
	@DatabaseField
	private int singleitem;

	// atm编号
	@DatabaseField
	private String atmno;
	// atmid
	@DatabaseField
	private String atmid;

	// atm任务id
	@DatabaseField
	private String taskid;

	//网点id
	@DatabaseField
	private String branchid;

	//是否登记  Y 已登记  N 未登记
	@DatabaseField
	private String isRegister ="N" ;

	public String getIsRegister() {
		return isRegister;
	}

	public String getShort_name() {
		return name_full;
	}

	public String getName_full() {
		return name_full;
	}

	public void setName_full(String name_full) {
		this.name_full = name_full;
	}

	public ATMRouteVo(String atmcustomerid, int atminstallationmethod, String atmnodetype, String atmtype, String clientid, String code, boolean enabled, String id, int ids, int inputtype, boolean isatmornode, String isCanUp, String isDone,  boolean isphoto,  String isUploaded, String name, String operatedtime, String operator, int order, String selectitems) {
		this.atmcustomerid = atmcustomerid;
		this.atminstallationmethod = atminstallationmethod;
		this.atmnodetype = atmnodetype;
		this.atmtype = atmtype;
		this.clientid = clientid;
		this.code = code;
		this.enabled = enabled;
		this.id = id;
		this.ids = ids;
		this.inputtype = inputtype;
		this.isatmornode = isatmornode;
		this.isCanUp = isCanUp;
		this.isDone = isDone;
		this.isphoto = isphoto;
		this.isUploaded = isUploaded;
		this.name = name;
		this.operatedtime = operatedtime;
		this.operator = operator;
		this.order = order;
		this.selectitems = selectitems;
	}

	public void setIsRegister(String isRegister) {
		this.isRegister = isRegister;
	}

	public String getAtmcustomerid() {
		return atmcustomerid;
	}

	public void setAtmcustomerid(String atmcustomerid) {
		this.atmcustomerid = atmcustomerid;
	}

	public int getAtminstallationmethod() {
		return atminstallationmethod;
	}

	public void setAtminstallationmethod(int atminstallationmethod) {
		this.atminstallationmethod = atminstallationmethod;
	}

	public String getAtmnodetype() {
		return atmnodetype;
	}

	public void setAtmnodetype(String atmnodetype) {
		this.atmnodetype = atmnodetype;
	}

	public String getAtmtype() {
		return atmtype;
	}

	public void setAtmtype(String atmtype) {
		this.atmtype = atmtype;
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

	public int getInputtype() {
		return inputtype;
	}

	public void setInputtype(int inputtype) {
		this.inputtype = inputtype;
	}

	public boolean isatmornode() {
		return isatmornode;
	}

	public void setIsatmornode(boolean isatmornode) {
		this.isatmornode = isatmornode;
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

	public String getBranchcode() {
		return branchcode;
	}

	public void setBranchcode(String branchcode) {
		this.branchcode = branchcode;
	}

	public String getBranchname() {
		return branchname;
	}

	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}

	public String getBranchid() {
		return branchid;
	}

	public void setBranchid(String branchid) {
		this.branchid = branchid;
	}

	public String getSignphoto() {
		return signphoto;
	}

	public void setSignphoto(String signphoto) {
		this.signphoto = signphoto;
	}

	public String getErrorphoto() {
		return errorphoto;
	}

	public void setErrorphoto(String errorphoto) {
		this.errorphoto = errorphoto;
	}

	public int getSingleitem() {
		return singleitem;
	}

	public void setSingleitem(int singleitem) {
		this.singleitem = singleitem;
	}

	public String getAtmno() {
		return atmno;
	}

	public void setAtmno(String atmno) {
		this.atmno = atmno;
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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
}
