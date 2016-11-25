package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 银行客户编号表
 */
@DatabaseTable(tableName = "BANK_CUSTOMER_VO")
public class BankCustomerVo implements Serializable{

	private static final long serialVersionUID = 1L;

	//网点id
	@DatabaseField
	private String id;

	//客户编码  两位
	@DatabaseField
	private String code;

	//网点名称
	@DatabaseField
	private String name;

	//版本
	@DatabaseField
	private long version;

	public BankCustomerVo() {
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

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
