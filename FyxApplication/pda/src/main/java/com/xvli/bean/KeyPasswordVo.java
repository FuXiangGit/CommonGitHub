package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author wdm
 * @Description:密码和钥匙存储
 */
@DatabaseTable(tableName = "KEY_PWD")
public class KeyPasswordVo {

	public KeyPasswordVo() {
	}

	@DatabaseField(generatedId = true)
	private int id;

	// 扫到的网点钥匙 对应的网点id
	@DatabaseField
	private String iskey;

	// 扫到的密码 对应的网点id
	@DatabaseField
	private String ispwd;

	// 网点二维码
	@DatabaseField
	private String networkno;

	@DatabaseField
	private String clientid;

	// 网点id号
	@DatabaseField
	private String branchid;

	// 钥匙是否扫描过
	@DatabaseField
	private String isScan = "N";

	// 密码是否扫描过
	@DatabaseField
	private String isPwdScan;
    //是否计划
	@DatabaseField
	private String isPlan = "Y";

    //是否交接
	@DatabaseField
	private String isTransfer = "N";
	//新添加
	public static String KEY="KEY";
	public static String PASSWORD="PASS";
	
	//物品类型，密码或钥匙
	@DatabaseField
    private String itemtype;
    
    //钥匙或者密码的二维码
	@DatabaseField
    private String barcode;

	//接收人
	@DatabaseField
	private String transfer;

	//交接人
	@DatabaseField
	private String recvice;

	//操作人
	@DatabaseField
	private String operator;
	
	//操作时间
	@DatabaseField
	private String operatetime;


	@DatabaseField
	private String isDelete = "Y";//下载数据不做删除  y删除

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	//车辆的GIS位置
	@DatabaseField
	private String gisx;
	@DatabaseField
	private String gisy;
	
	//GPS坐标海拔高度
	@DatabaseField
	private String gisz;

	//网点名称
	@DatabaseField
    private String branchname;

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	//网点code
	@DatabaseField

	private  String branchCode;


	@DatabaseField
	private String  isCurrency ="N";//Y  /  N  是否通用钥匙

	public String getIsCurrency() {
		return isCurrency;
	}

	public void setIsCurrency(String isCurrency) {
		this.isCurrency = isCurrency;
	}

	//是否上传成功
	@DatabaseField
	private String IsUploaded = "N";

	//发送提交指令，但未提交成功，后台执行
	@DatabaseField
	private String isSubmit = "N" ;
	//上传数据数据时唯一标示符
	@DatabaseField
	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getIsSubmit() {
		return isSubmit;
	}

	public void setIsSubmit(String isSubmit) {
		this.isSubmit = isSubmit;
	}

	//id
	@DatabaseField
	private String remake ;

	public String getRemake() {
		return remake;
	}

	public void setRemake(String remake) {
		this.remake = remake;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNetworkno() {
		return networkno;
	}

	public void setNetworkno(String networkno) {
		this.networkno = networkno;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getBranchid() {
		return branchid;
	}

	public void setBranchid(String branchid) {
		this.branchid = branchid;
	}

	public String getIsScan() {
		return isScan;
	}

	public void setIsScan(String isScan) {
		this.isScan = isScan;
	}


	public String getIsPwdScan() {
		return isPwdScan;
	}

	public void setIsPwdScan(String isPwdScan) {
		this.isPwdScan = isPwdScan;
	}

	public String getIskey() {
		return iskey;
	}

	public void setIskey(String iskey) {
		this.iskey = iskey;
	}

	public String getIspwd() {
		return ispwd;
	}

	public void setIspwd(String ispwd) {
		this.ispwd = ispwd;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
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

	public String getIsUploaded() {
		return IsUploaded;
	}

	public void setIsUploaded(String isUploaded) {
		IsUploaded = isUploaded;
	}

	public String getBranchname() {
		return branchname;
	}

	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}

	public String getIsTransfer() {
		return isTransfer;
	}

	public void setIsTransfer(String isTransfer) {
		this.isTransfer = isTransfer;
	}


	public String getTransfer() {
		return transfer;
	}

	public void setTransfer(String transfer) {
		this.transfer = transfer;
	}

	public String getRecvice() {
		return recvice;
	}

	public void setRecvice(String recvice) {
		this.recvice = recvice;
	}

	public String getIsPlan() {
		return isPlan;
	}

	public void setIsPlan(String isPlan) {
		this.isPlan = isPlan;
	}
}
