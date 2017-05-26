package com.xvli.cit.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 所有操作数据的时间和Gps上传
 * @author wdm
 * @Description:
 */
@DatabaseTable(tableName = "OPERATELOG_VO")
public class OperateLogVo {//及时发送    操作日志对应发送

	public static String LOGTYPE_KEY_END = "KEY_END";// 钥匙扫描结束
	public static String LOGTYPE_PASSWORD_END = "PASSWORD_END";// 密码扫描结束
	public static String LOGTYPE_TRUCK_OUT = "TRUCK_OUT";// 出车
	public static String LOGTYPE_TRUCK_BACK = "TRUCK_BACK";// 回到公司
	public static String LOGTYPE_ITEM_OUT = "ITEM_OUT";// 出车后物品上车
	public static String LOGTYPE_OFF_BEGIN = "OFF_BEGIN";// 到达网点后，开始下车
	public static String LOGTYPE_OFF_END = "OFF_END";// 下车结束
	public static String LOGTYPE_ARRIVE_BRANCH = "ARRIVE_BRANCH";// 到达网点
	public static String LOGTYPE_LEAVE_BRANCH = "LEAVE_BRANCH";// 离开网点
	public static String LOGTYPE_ATM_BEGIN = "ATM_BEGIN";// 开始ATM机具操作
	public static String LOGTYPE_ATM_END = "ATM_END";// 结束ATM机具操作
	public static String OUT_FINISH = "OUT_FINISH";// 出库完成 针对迪堡无清分
	@DatabaseField(generatedId = true)
	private int id;

	// 日志类型
	@DatabaseField
	private String logtype;

	// 扫描物品的二维码，到达离开网点时，填写网点二维码| 开始结束机具操作时，填写机具二维码
	@DatabaseField
	private String barcode;

	// 车牌号，若有绑定车辆，则填写，无则空
	@DatabaseField
	private String platenumber;

	// 操作人
	@DatabaseField
	private String operator;

	// 操作时间
	@DatabaseField
	private String operatetime;

	// 车辆的GIS位置
	@DatabaseField
	private String gisx;

	@DatabaseField
	private String gisy;

	@DatabaseField
	private String clientid;
	
	
    //对应任务ID
    @DatabaseField
    private String taskinfoid;
	
	// GPS坐标海拔高度
	@DatabaseField
	private String gisz;

	// 泰国出车回库公里数
	@DatabaseField
	private String kilometre;

	// 是否已经上传过
	@DatabaseField
	private String isUploaded = "N";
	//上传数据数据时唯一标示符
	@DatabaseField
	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLogtype() {
		return logtype;
	}

	public void setLogtype(String logtype) {
		this.logtype = logtype;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getPlatenumber() {
		return platenumber;
	}

	public void setPlatenumber(String platenumber) {
		this.platenumber = platenumber;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}


	public String getOperatetime() {
		return operatetime;
	}

	public void setOperatetime(String operatetime) {
		this.operatetime = operatetime;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getTaskinfoid() {
		return taskinfoid;
	}

	public void setTaskinfoid(String taskinfoid) {
		this.taskinfoid = taskinfoid;
	}

	public String getKilometre() {
		return kilometre;
	}

	public void setKilometre(String kilometre) {
		this.kilometre = kilometre;
	}
}
