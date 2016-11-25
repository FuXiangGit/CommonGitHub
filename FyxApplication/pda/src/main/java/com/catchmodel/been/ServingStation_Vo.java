package com.catchmodel.been;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 13:51.
 */
//维修点信息采集表
@DatabaseTable(tableName = "T_ServingStation_INFO")
public class ServingStation_Vo implements Serializable{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String Name; //维修点名称

    @DatabaseField
    private String Address;//地址

    @DatabaseField
    private String City;//城市

    @DatabaseField
    private String Contacts;//联系人

    @DatabaseField
    private String  Telephone;//电话

    @DatabaseField
    private String  Classify;//分类

    @DatabaseField
    private String ids;//  维修站id

    @DatabaseField
    public String allresult; //用于拼接字符串  模糊查询

    public String getAllresult() {
        return allresult;
    }

    public void setAllresult(String allresult) {
        this.allresult = allresult;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getContacts() {
        return Contacts;
    }

    public void setContacts(String contacts) {
        Contacts = contacts;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getClassify() {
        return Classify;
    }

    public void setClassify(String classify) {
        Classify = classify;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }





}
