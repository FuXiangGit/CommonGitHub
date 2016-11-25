package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 配置文件
 */
@DatabaseTable(tableName="CONFIG_VO")
public class ConfigVo implements Serializable {

    @DatabaseField(generatedId = true)
    private int ids;

    // 客户名字
    @DatabaseField
    private String name;

    // 客户名字缩写
    @DatabaseField
    private String shortname;

    // 类型简写
    @DatabaseField
    private String nametype;

    // 参考值
    @DatabaseField
    private String value;

    // Pda出车前扫描出库物品 等 类型
    @DatabaseField
    private String displayname;

    // 所属客户图片url
    @DatabaseField
    private String picture;

    // 所属客户保存图片地址
    @DatabaseField
    private String localaddress;

    // 所属客户图片url 大图
    @DatabaseField
    private String bigpicurl;

    // 所属客户保存大保存图片地址
    @DatabaseField
    private String biglocaladd;

    // key": "dbd-00",代表迪堡招商银行   sss-00  押运通用
    @DatabaseField
    private String key;

    @DatabaseField
    private String projectname;//项目组


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public ConfigVo() {
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getNametype() {
        return nametype;
    }

    public void setNametype(String nametype) {
        this.nametype = nametype;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLocaladdress() {
        return localaddress;
    }

    public void setLocaladdress(String localaddress) {
        this.localaddress = localaddress;
    }

    public String getBiglocaladd() {
        return biglocaladd;
    }

    public void setBiglocaladd(String biglocaladd) {
        this.biglocaladd = biglocaladd;
    }

    public String getBigpicurl() {
        return bigpicurl;
    }

    public void setBigpicurl(String bigpicurl) {
        this.bigpicurl = bigpicurl;
    }
}