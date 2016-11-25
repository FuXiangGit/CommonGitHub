package com.catchmodel.been;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 15:12.
 */
//采集信息表
@DatabaseTable(tableName = "T_Catchmodel_SaveAllData")
public class SaveAllDataVo implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String Type; //AtmNode网点 ，WorkNode停靠点 ，GasStation加油站 ，ServingStation维修点

    @DatabaseField
    private String Gis;//位置坐标

    @DatabaseField
    private String GisTruck1;//网点 停车位坐标1

    @DatabaseField
    private String GisTruck2;//网点 停车位坐标2

    @DatabaseField
    private String prcture_name;//位置 图片路径

    @DatabaseField
    private String 	prcture_name_Truck1 ;//网点 停车位图片1路径

    @DatabaseField
    private String 	prcture_name_Truck2 ;//网点 停车位图片2路径

    @DatabaseField
    private String  ids ;//采集信息Id

    @DatabaseField
    private String name;//名称（网点，加油站，维修站，停靠点）

    @DatabaseField
    private String isUpLoader = "N";//是否上传  Y上传  N未上传 ，数据上传

    @DatabaseField
    private String imageUpLoader = "N";//是否上传  Y上传  N未上传 .图片上传

    @DatabaseField
    private String  jobNumber ;  //用户工号

    @DatabaseField
    private String  address;//地址

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @DatabaseField

    private String  customer;//所属客户

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @DatabaseField
    private String  saveTime ; //保存时间

    @DatabaseField
    private int  day; //保存时间


    public String getImageUpLoader() {
        return imageUpLoader;
    }

    public void setImageUpLoader(String imageUpLoader) {
        this.imageUpLoader = imageUpLoader;
    }

    public String getIsUpLoader() {
        return isUpLoader;
    }

    public void setIsUpLoader(String isUpLoader) {
        this.isUpLoader = isUpLoader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getGis() {
        return Gis;
    }

    public void setGis(String gis) {
        Gis = gis;
    }

    public String getGisTruck1() {
        return GisTruck1;
    }

    public void setGisTruck1(String gisTruck1) {
        GisTruck1 = gisTruck1;
    }

    public String getGisTruck2() {
        return GisTruck2;
    }

    public void setGisTruck2(String gisTruck2) {
        GisTruck2 = gisTruck2;
    }

    public String getPrcture_name() {
        return prcture_name;
    }

    public void setPrcture_name(String prcture_name) {
        this.prcture_name = prcture_name;
    }

    public String getPrcture_name_Truck1() {
        return prcture_name_Truck1;
    }

    public void setPrcture_name_Truck1(String prcture_name_Truck1) {
        this.prcture_name_Truck1 = prcture_name_Truck1;
    }

    public String getPrcture_name_Truck2() {
        return prcture_name_Truck2;
    }

    public void setPrcture_name_Truck2(String prcture_name_Truck2) {
        this.prcture_name_Truck2 = prcture_name_Truck2;
    }
}
