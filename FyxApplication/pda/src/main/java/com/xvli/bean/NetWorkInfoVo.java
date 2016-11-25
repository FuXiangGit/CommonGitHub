package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 网点信息表
 */
@DatabaseTable(tableName="NET_WORK_INFO")
public class NetWorkInfoVo {

    @DatabaseField
    private String netname;

    @DatabaseField
    private String netstatic;

    @DatabaseField
    private String netid;

    @DatabaseField
    private String isok;

    @DatabaseField
    private String isUploader;

    public NetWorkInfoVo() {
    }

    public String getIsok() {
        return isok;
    }

    public void setIsok(String isok) {
        this.isok = isok;
    }

    public String getIsUploader() {
        return isUploader;
    }

    public void setIsUploader(String isUploader) {
        this.isUploader = isUploader;
    }

    public String getNetid() {
        return netid;
    }

    public void setNetid(String netid) {
        this.netid = netid;
    }

    public String getNetname() {
        return netname;
    }

    public void setNetname(String netname) {
        this.netname = netname;
    }

    public String getNetstatic() {
        return netstatic;
    }

    public void setNetstatic(String netstatic) {
        this.netstatic = netstatic;
    }
}
