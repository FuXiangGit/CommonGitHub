package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 网点基础表 不做删除
 */
@DatabaseTable(tableName = "DYN_NODE_ITEM")
public class DynNodeItemVo {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String id;

    @DatabaseField
    private int alarmarrivedusertime;

    // 客户id号
    @DatabaseField
    private String customerid;

    @DatabaseField
    private String code;

    @DatabaseField
    private String name;

    @DatabaseField
    private String nodetypeid;

    @DatabaseField
    private String districts;

    @DatabaseField
    private String telephone;

    @DatabaseField
    private String address;

    @DatabaseField
    private String contacts;

    @DatabaseField
    private String location;

    @DatabaseField
    private String baidugeoid;

    @DatabaseField
    private String truckgis1;

    @DatabaseField
    private String truckgis2;

    @DatabaseField
    private String city;
    @DatabaseField
    private String picture;

    @DatabaseField
    private String mainpicture;
    @DatabaseField
    private String returnaddress;

    @DatabaseField
    private String barcode;

    @DatabaseField
    private String isinbank;
    @DatabaseField
    private int nodestatusid;

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
    public DynNodeItemVo() {
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAlarmarrivedusertime() {
        return alarmarrivedusertime;
    }

    public void setAlarmarrivedusertime(int alarmarrivedusertime) {
        this.alarmarrivedusertime = alarmarrivedusertime;
    }

    public String getBaidugeoid() {
        return baidugeoid;
    }

    public void setBaidugeoid(String baidugeoid) {
        this.baidugeoid = baidugeoid;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsinbank() {
        return isinbank;
    }

    public void setIsinbank(String isinbank) {
        this.isinbank = isinbank;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMainpicture() {
        return mainpicture;
    }

    public void setMainpicture(String mainpicture) {
        this.mainpicture = mainpicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNodestatusid() {
        return nodestatusid;
    }

    public void setNodestatusid(int nodestatusid) {
        this.nodestatusid = nodestatusid;
    }

    public String getNodetypeid() {
        return nodetypeid;
    }

    public void setNodetypeid(String nodetypeid) {
        this.nodetypeid = nodetypeid;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getReturnaddress() {
        return returnaddress;
    }

    public void setReturnaddress(String returnaddress) {
        this.returnaddress = returnaddress;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTruckgis1() {
        return truckgis1;
    }

    public void setTruckgis1(String truckgis1) {
        this.truckgis1 = truckgis1;
    }

    public String getTruckgis2() {
        return truckgis2;
    }

    public void setTruckgis2(String truckgis2) {
        this.truckgis2 = truckgis2;
    }
}
