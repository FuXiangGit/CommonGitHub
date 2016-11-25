package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 16:20.
 */
//接口请求成功事件表
@DatabaseTable(tableName = "T_Interface_Events")
public class InterfaceEventsVo implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    //接口url
    @DatabaseField
    private String url;

    //操作人
    @DatabaseField
    private String operator;

    //操作时间
    @DatabaseField
    private String operatetime;

    @DatabaseField
    private String clientId;



    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }





}
