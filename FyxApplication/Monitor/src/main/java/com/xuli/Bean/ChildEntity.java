package com.xuli.Bean;

import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;



public class ChildEntity {

	private int groupColor;

	private String groupName;

	private boolean istruck;//二级菜单是否是车辆

	private String truchid;//二级菜单是否是车辆

	private boolean ischeck;//是否选中

	private boolean isonLine;//是否在线

	private ArrayList<String> childNames;

	private ArrayList<String> carId;//车辆id

	private ArrayList<Boolean> carIsOnline;//车辆是否在线

	private ArrayList<Boolean> carIsCheck;//车辆是否选中

	/* ==========================================================
	 * ======================= get method =======================
	 * ========================================================== */
	
	public int getGroupColor() {
		return groupColor;
	}

	public String getGroupName() {
		return groupName;
	}

	public ArrayList<String> getChildNames() {
		return childNames;
	}

	/* ==========================================================
	 * ======================= set method =======================
	 * ========================================================== */
	
	public void setGroupColor(int groupColor) {
		this.groupColor = groupColor;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setChildNames(ArrayList<String> childNames) {
		this.childNames = childNames;
	}

	public boolean istruck() {
		return istruck;
	}

	public void setIstruck(boolean istruck) {
		this.istruck = istruck;
	}

	public boolean ischeck() {
		return ischeck;
	}

	public void setIscheck(boolean ischeck) {
		this.ischeck = ischeck;
	}

	public String getTruchid() {
		return truchid;
	}

	public void setTruchid(String truchid) {
		this.truchid = truchid;
	}

	public ArrayList<String> getCarId() {
		return carId;
	}

	public void setCarId(ArrayList<String> carId) {
		this.carId = carId;
	}

	public ArrayList<Boolean> getCarIsOnline() {
		return carIsOnline;
	}

	public void setCarIsOnline(ArrayList<Boolean> carIsOnline) {
		this.carIsOnline = carIsOnline;
	}

	public boolean isonLine() {
		return isonLine;
	}

	public void setIsonLine(boolean isonLine) {
		this.isonLine = isonLine;
	}

	public ArrayList<Boolean> getCarIsCheck() {
		return carIsCheck;
	}

	public void setCarIsCheck(ArrayList<Boolean> carIsCheck) {
		this.carIsCheck = carIsCheck;
	}
}
