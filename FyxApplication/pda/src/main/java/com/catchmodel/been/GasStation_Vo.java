package com.catchmodel.been;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 10:49.
 */

@DatabaseTable(tableName = "T_GasStation_INFO")
public class GasStation_Vo implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String Name;

    @DatabaseField
    private String Address;

    @DatabaseField
    private String City;

    @DatabaseField
    private String  Contacts ; //联系人

    @DatabaseField
    private String Telephone;

    @DatabaseField
    private String License;//经营许可证期限

    @DatabaseField
    private String ids;//  加油站id

    @DatabaseField
    public String allresult; //用于拼接字符串  模糊查询

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

    public String getLicense() {
        return License;
    }

    public void setLicense(String license) {
        License = license;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getAllresult() {
        return allresult;
    }

    public void setAllresult(String allresult) {
        this.allresult = allresult;
    }




}
