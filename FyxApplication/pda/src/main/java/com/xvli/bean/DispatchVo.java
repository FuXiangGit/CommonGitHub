package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * 调度内容
 * Created by Administrator on 2015/12/8.
 */
@DatabaseTable(tableName="T_DISPATCH_VO")
public class DispatchVo implements Serializable{
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;
    //调度id
    @DatabaseField
    private String dispatchid;

    //事件类型
    @DatabaseField
    private String  eventtype;

    //消息内容
    @DatabaseField
    private String eventmsg;

    //事件时间
    @DatabaseField
    private String eventdate;

    //项目id
    @DatabaseField
    private String projectid;

    //企业id
    @DatabaseField
    private String enterprised;

    //时间戳
    @DatabaseField
    private long version ;

    public DispatchVo() {
    }

    public String getDispatchid() {
        return dispatchid;
    }

    public void setDispatchid(String dispatchid) {
        this.dispatchid = dispatchid;
    }

    public String getEnterprised() {
        return enterprised;
    }

    public void setEnterprised(String enterprised) {
        this.enterprised = enterprised;
    }

    public String getEventdate() {
        return eventdate;
    }

    public void setEventdate(String eventdate) {
        this.eventdate = eventdate;
    }

    public String getEventmsg() {
        return eventmsg;
    }

    public void setEventmsg(String eventmsg) {
        this.eventmsg = eventmsg;
    }

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }
}
