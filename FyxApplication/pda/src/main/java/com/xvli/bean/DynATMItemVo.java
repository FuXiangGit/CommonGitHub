package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 机具基础表 不做删除
 */
@DatabaseTable(tableName = "DYN_ATM_ITEM")
public class DynATMItemVo {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String id;

    @DatabaseField
    private String atmno;

    // 客户id号
    @DatabaseField
    private String customerid;
    @DatabaseField
    private String terminalnum;

    @DatabaseField
    private String atmtypeid;

    @DatabaseField
    private String nodeid;

    @DatabaseField
    private int atmstatusid;

    @DatabaseField
    private int jobtype;

    @DatabaseField
    private String startmdate;

    @DatabaseField
    private String endmdate;

    @DatabaseField
    private int alarmmaxmoney;

    @DatabaseField
    private int alarmminmoney;
    @DatabaseField
    private int installationmethod;

    @DatabaseField
    private String barcode;

    @DatabaseField
    private int updateboxcount;
    @DatabaseField
    private String delete;

    @DatabaseField
    private long version;


    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
    public DynATMItemVo() {
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public int getAlarmmaxmoney() {
        return alarmmaxmoney;
    }

    public void setAlarmmaxmoney(int alarmmaxmoney) {
        this.alarmmaxmoney = alarmmaxmoney;
    }

    public int getAlarmminmoney() {
        return alarmminmoney;
    }

    public void setAlarmminmoney(int alarmminmoney) {
        this.alarmminmoney = alarmminmoney;
    }

    public String getAtmno() {
        return atmno;
    }

    public void setAtmno(String atmno) {
        this.atmno = atmno;
    }

    public int getAtmstatusid() {
        return atmstatusid;
    }

    public void setAtmstatusid(int atmstatusid) {
        this.atmstatusid = atmstatusid;
    }

    public String getAtmtypeid() {
        return atmtypeid;
    }

    public void setAtmtypeid(String atmtypeid) {
        this.atmtypeid = atmtypeid;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getEndmdate() {
        return endmdate;
    }

    public void setEndmdate(String endmdate) {
        this.endmdate = endmdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getInstallationmethod() {
        return installationmethod;
    }

    public void setInstallationmethod(int installationmethod) {
        this.installationmethod = installationmethod;
    }

    public int getJobtype() {
        return jobtype;
    }

    public void setJobtype(int jobtype) {
        this.jobtype = jobtype;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getStartmdate() {
        return startmdate;
    }

    public void setStartmdate(String startmdate) {
        this.startmdate = startmdate;
    }

    public String getTerminalnum() {
        return terminalnum;
    }

    public void setTerminalnum(String terminalnum) {
        this.terminalnum = terminalnum;
    }

    public int getUpdateboxcount() {
        return updateboxcount;
    }

    public void setUpdateboxcount(int updateboxcount) {
        this.updateboxcount = updateboxcount;
    }
}
