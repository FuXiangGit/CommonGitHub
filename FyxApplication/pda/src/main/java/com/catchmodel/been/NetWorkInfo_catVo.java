package com.catchmodel.been;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 10:58.
 */

//网点信息表
@DatabaseTable(tableName = "T_NETWORK_INFO")
public class NetWorkInfo_catVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String  Code;//网点编号

    @DatabaseField
    private String Name;//网点名称

    @DatabaseField
    private String AtmCustomerName;//所属客户

    @DatabaseField
    private String AtmNodeType ;//网点类型

    @DatabaseField
    private String Districts;//行政区县

    @DatabaseField
    private String Telephone;//联系电话

    @DatabaseField
    private String Address ;//地址

    @DatabaseField
    private String Contacts;//联系人

    @DatabaseField
    private String City ;//城市

    @DatabaseField
    private String ids; //网点Id

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

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAtmCustomerName() {
        return AtmCustomerName;
    }

    public void setAtmCustomerName(String atmCustomerName) {
        AtmCustomerName = atmCustomerName;
    }

    public String getAtmNodeType() {
        return AtmNodeType;
    }

    public void setAtmNodeType(String atmNodeType) {
        AtmNodeType = atmNodeType;
    }

    public String getDistricts() {
        return Districts;
    }

    public void setDistricts(String districts) {
        Districts = districts;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getContacts() {
        return Contacts;
    }

    public void setContacts(String contacts) {
        Contacts = contacts;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
