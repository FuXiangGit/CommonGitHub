package com.catchmodel.been;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 14:04.
 */
//停靠点信息采集表
@DatabaseTable(tableName = "T_WorkNode_INFO")
public class WorkNode_Vo implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String Name ; //停靠点名称

    @DatabaseField
    private String Company;//单位

    @DatabaseField
    private String Districts;//行政区县

    @DatabaseField
    private String Address ;//地址

    @DatabaseField
    private String City ;//城市

    @DatabaseField
    private String ids;//  停靠点id

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

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getDistricts() {
        return Districts;
    }

    public void setDistricts(String districts) {
        Districts = districts;
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

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
