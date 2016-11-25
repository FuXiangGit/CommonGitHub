package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * 网点线路
 * Created by Administrator on 2015/12/8.
 */
@DatabaseTable(tableName="T_BRANCH_LINE")
public class BranchLineVo implements Serializable{
    @DatabaseField(generatedId = true)
    private int ids;
    //网点id号
    @DatabaseField
    private String branchid;

    @DatabaseField
    private String  customername;

    //网点代码
    @DatabaseField
    private String code;

    @DatabaseField
    private String barcode;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    //网点名称
    @DatabaseField
    private String branchname;

    //网点操作类型 大唐 穿墙 等等
    @DatabaseField
    private String branchtypes;

    //客户ID号
    @DatabaseField
    private String customerid;

    //类型
    @DatabaseField
    private String type;

    //区域
    @DatabaseField
    private String districts;

    //区域
    @DatabaseField
    private String linenumber;
    //编号
    @DatabaseField
    private String atmno;

    //地址
    @DatabaseField
    private String address;

    //备注
    @DatabaseField
    private String remarks;
    @DatabaseField
    private String gisx;
    @DatabaseField
    private String gisy;

    @DatabaseField
    private String clientid;
    /**
     * 对应网点是否完成
     */
    @DatabaseField
    private String isnetdone="N";//Y 为已完成 N 为未完成  R为撤销(Revoke)    C为变更(change)   A 为新增（add）

    @DatabaseField
    private String isrevoke ;

    //该网点是否是巡检任务
    @DatabaseField
    private String isroute;  //1 为网点是巡检任务


    //是否是撤销任务 N否  　Ｙ是
    @DatabaseField
    private String iscancel ="N" ;

    private List<AtmVo> atmList;

    public BranchLineVo() {
    }

    public String getIsrevoke() {
        return isrevoke;
    }

    public void setIsrevoke(String isrevoke) {
        this.isrevoke = isrevoke;
    }

    public BranchLineVo(int ids) {
        this.ids = ids;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getBranchid() {
        return branchid;
    }

    public void setBranchid(String branchid) {
        this.branchid = branchid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getLinenumber() {
        return linenumber;
    }

    public void setLinenumber(String linenumber) {
        this.linenumber = linenumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }

    public String getAtmno() {
        return atmno;
    }

    public void setAtmno(String atmno) {
        this.atmno = atmno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }


    public String getIsnetdone() {
        return isnetdone;
    }

    public void setIsnetdone(String isnetdone) {
        this.isnetdone = isnetdone;
    }

    public List<AtmVo> getAtmList() {
        return atmList;
    }

    public void setAtmList(List<AtmVo> atmList) {
        this.atmList = atmList;
    }

    public String getBranchtypes() {
        return branchtypes;
    }

    public void setBranchtypes(String branchtypes) {
        this.branchtypes = branchtypes;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getIsroute() {
        return isroute;
    }

    public void setIsroute(String isroute) {
        this.isroute = isroute;
    }

    public String getIscancel() {
        return iscancel;
    }

    public void setIscancel(String iscancel) {
        this.iscancel = iscancel;
    }
}
