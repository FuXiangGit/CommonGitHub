package com.catchmodel.been;

import java.io.Serializable;

public class LoginBeen implements Serializable{
	private String jobnumber1;
	private String jobnumber2;
	private String name1;
	private String name2;
	public String getJobnumber1() {
		return jobnumber1;
	}
	public void setJobnumber1(String jobnumber1) {
		this.jobnumber1 = jobnumber1;
	}
	public String getJobnumber2() {
		return jobnumber2;
	}
	public void setJobnumber2(String jobnumber2) {
		this.jobnumber2 = jobnumber2;
	}
	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
}
