package com.xuli.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/09/28.
 * 一个用户登录
 */
@DatabaseTable(tableName = "LOGIN_VO")
public class LoginVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;

    @DatabaseField
    private String name1;

    @DatabaseField
    private String name2;

    @DatabaseField
    private String pwd1;

    @DatabaseField
    private String pwd2;

    @DatabaseField
    private String jobnumber1;

    @DatabaseField
    private String jobnumber2;

    @DatabaseField
    private String department1;

    @DatabaseField
    private String department2;


    //构造方法
    public LoginVo() {
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
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


    public String getPwd1() {
        return pwd1;
    }

    public void setPwd1(String pwd1) {
        this.pwd1 = pwd1;
    }

    public String getPwd2() {
        return pwd2;
    }

    public void setPwd2(String pwd2) {
        this.pwd2 = pwd2;
    }


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


    public String getDepartment1() {
        return department1;
    }

    public void setDepartment1(String department1) {
        this.department1 = department1;
    }

    public String getDepartment2() {
        return department2;
    }

    public void setDepartment2(String department2) {
        this.department2 = department2;
    }


}
