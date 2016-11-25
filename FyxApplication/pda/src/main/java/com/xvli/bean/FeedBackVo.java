package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * 调度反馈
 */
@DatabaseTable(tableName="FEED_BACK_VO")
public class FeedBackVo implements Serializable{
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;

    //调度id
    @DatabaseField
    private String dispatchid;

    //处理结果 处理结果  0 成功  1 失败
    @DatabaseField
    private String  result;

    //是否上传
    @DatabaseField
    private String IsUploaded = "N";


    public FeedBackVo() {
    }

    public String getDispatchid() {
        return dispatchid;
    }
    //上传数据数据时唯一标示符
    @DatabaseField
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDispatchid(String dispatchid) {
        this.dispatchid = dispatchid;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getIsUploaded() {
        return IsUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        IsUploaded = isUploaded;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }
}
