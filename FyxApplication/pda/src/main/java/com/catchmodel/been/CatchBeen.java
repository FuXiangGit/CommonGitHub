package com.catchmodel.been;

import java.io.Serializable;

public class CatchBeen implements Serializable{
	private int intNnumber;
	private long branchNumber;//网点编号
	private String branchName;//所属客户
	private String customerName;//网点名称
	private String branchType;//网点类型
	private String adminiStrative;//行政区县
	private int phoneNumber;//联系电话
	private String atmMessage;//ATM柜台信息
	private String branchState;//网点状态
	private String branchAddress;//网点地址
	private String branchposition;//网点位置
	private String branchLinkman;//网点联系人
	private int monitoringNumber;//监控维修号码
	private int technialNumber;//技防维修号码
	private String branchCondition;//网点情况
	private String retainCardAddress;//吞卡归还点
	private String remark;//备注
	private String gpsMarkOne;//GPS1号位
	private String gpsMarkTwo;//GPS2号位
	private String gpsMarkThree;//GPS3号位
	private int bankNumber;//银行号码
	private String bitmapUrl;//图片位置
	private String storetime;//存放时间
	private String branchGps;//网点GPS位置
	private String isUploading;//是否已经上传
	private String custno1;//记录人员工号1
	private String custno2;//记录人员工号2
	
	public String getIsUploading() {
		return isUploading;
	}
	public void setIsUploading(String isUploading) {
		this.isUploading = isUploading;
	}
	public String getCustno1() {
		return custno1;
	}
	public void setCustno1(String custno1) {
		this.custno1 = custno1;
	}
	public String getCustno2() {
		return custno2;
	}
	public void setCustno2(String custno2) {
		this.custno2 = custno2;
	}
	public int getIntNnumber() {
		return intNnumber;
	}
	public void setIntNnumber(int intNnumber) {
		this.intNnumber = intNnumber;
	}
	public String getBranchGps() {
		return branchGps;
	}
	public void setBranchGps(String branchGps) {
		this.branchGps = branchGps;
	}
	public String getStoretime() {
		return storetime;
	}
	public void setStoretime(String storetime) {
		this.storetime = storetime;
	}
	public String getBitmapUrl() {
		return bitmapUrl;
	}
	public void setBitmapUrl(String bitmapUrl) {
		this.bitmapUrl = bitmapUrl;
	}
	public String getBranchposition() {
		return branchposition;
	}
	public void setBranchposition(String branchposition) {
		this.branchposition = branchposition;
	}
	public int getBankNumber() {
		return bankNumber;
	}
	public void setBankNumber(int bankNumber) {
		this.bankNumber = bankNumber;
	}
	public long getBranchNumber() {
		return branchNumber;
	}
	public void setBranchNumber(long branchNumber) {
		this.branchNumber = branchNumber;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getBranchType() {
		return branchType;
	}
	public void setBranchType(String branchType) {
		this.branchType = branchType;
	}
	public String getAdminiStrative() {
		return adminiStrative;
	}
	public void setAdminiStrative(String adminiStrative) {
		this.adminiStrative = adminiStrative;
	}
	public int getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getAtmMessage() {
		return atmMessage;
	}
	public void setAtmMessage(String atmMessage) {
		this.atmMessage = atmMessage;
	}
	public String getBranchState() {
		return branchState;
	}
	public void setBranchState(String branchState) {
		this.branchState = branchState;
	}
	public String getBranchAddress() {
		return branchAddress;
	}
	public void setBranchAddress(String branchAddress) {
		this.branchAddress = branchAddress;
	}
	public String getBranchLinkman() {
		return branchLinkman;
	}
	public void setBranchLinkman(String branchLinkman) {
		this.branchLinkman = branchLinkman;
	}
	public int getMonitoringNumber() {
		return monitoringNumber;
	}
	public void setMonitoringNumber(int monitoringNumber) {
		this.monitoringNumber = monitoringNumber;
	}
	public int getTechnialNumber() {
		return technialNumber;
	}
	public void setTechnialNumber(int technialNumber) {
		this.technialNumber = technialNumber;
	}
	public String getBranchCondition() {
		return branchCondition;
	}
	public void setBranchCondition(String branchCondition) {
		this.branchCondition = branchCondition;
	}
	public String getRetainCardAddress() {
		return retainCardAddress;
	}
	public void setRetainCardAddress(String retainCardAddress) {
		this.retainCardAddress = retainCardAddress;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getGpsMarkOne() {
		return gpsMarkOne;
	}
	public void setGpsMarkOne(String gpsMarkOne) {
		this.gpsMarkOne = gpsMarkOne;
	}
	public String getGpsMarkTwo() {
		return gpsMarkTwo;
	}
	public void setGpsMarkTwo(String gpsMarkTwo) {
		this.gpsMarkTwo = gpsMarkTwo;
	}
	public String getGpsMarkThree() {
		return gpsMarkThree;
	}
	public void setGpsMarkThree(String gpsMarkThree) {
		this.gpsMarkThree = gpsMarkThree;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
