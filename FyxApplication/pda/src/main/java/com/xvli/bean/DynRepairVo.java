package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 维修选择项 作为基础表
 */
@DatabaseTable(tableName = "DYN_REPAIR_VO")
public class DynRepairVo {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String id;


    @DatabaseField
    private String faultmessages;

    // 客户id号
    @DatabaseField
    private String atmcustomerid;

    @DatabaseField
    private String name;

    @DatabaseField
    private String code;

    @DatabaseField
    private String order;

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
    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public DynRepairVo() {
    }


    public String getFaultmessages() {
        return faultmessages;
    }

    public void setFaultmessages(String faultmessages) {
        this.faultmessages = faultmessages;
    }


    public String getAtmcustomerid() {
        return atmcustomerid;
    }

    public void setAtmcustomerid(String atmcustomerid) {
        this.atmcustomerid = atmcustomerid;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOeder() {
        return order;
    }

    public void setOeder(String oeder) {
        this.order = oeder;
    }

}
