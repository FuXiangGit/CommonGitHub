package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 动态凭条信息保存
 * 2016-03-04
 */
@DatabaseTable(tableName = "TMR_CYCLEITEM_VO")
public class DynCycleItemVo implements Serializable{

	private static final long serialVersionUID = 1L;

	public DynCycleItemVo() {
	}

	@DatabaseField(generatedId = true)
	private int ids;
//	{
//		"id": "355f473f-01ef-41c7-aa04-00276cb66e39",
//			"name": "eee",
//			"code": "3",
//			"atmcustomerid": "2bfe346d-d7a2-4988-99bf-4462a140bb7d",
//			"order": 1,
//			"enabled": true,
//			"isneeded": false
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

    // 是否启用
	@DatabaseField
	private boolean isneeded;


	//保存用户输入信息Value
	@DatabaseField

	private String Value;

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	//======================================================================
	// 任务id号
	@DatabaseField
	private long taskid;
	
	// atm id号
	@DatabaseField
	private long atmid;

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


	@DatabaseField
	private String delete;

	@DatabaseField
	private long version;


	public String getDelete() {
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getAtmCustomerId() {
		return atmCustomerId;
	}

	public DynCycleItemVo(String atmCustomerId, long atmid, String clientid, String code, boolean enabled, String id, int ids, String isCanUp, String isDone, boolean isneeded, String isUploaded, String name, String operatedtime, String operator, int order, long taskid) {
		this.atmCustomerId = atmCustomerId;
		this.atmid = atmid;
		this.code = code;
		this.enabled = enabled;
		this.id = id;
		this.ids = ids;
		this.isCanUp = isCanUp;
		this.isDone = isDone;
		this.isneeded = isneeded;
		this.isUploaded = isUploaded;
		this.name = name;
		this.operatedtime = operatedtime;
		this.operator = operator;
		this.order = order;
		this.taskid = taskid;
	}

	public void setAtmCustomerId(String atmCustomerId) {
		this.atmCustomerId = atmCustomerId;
	}

	public long getAtmid() {
		return atmid;
	}

	public void setAtmid(long atmid) {
		this.atmid = atmid;
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

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public long getTaskid() {
		return taskid;
	}

	public void setTaskid(long taskid) {
		this.taskid = taskid;
	}
}
