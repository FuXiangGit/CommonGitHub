package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 零散数据库保存
 */
@DatabaseTable(tableName = "T_SCATTER_VO")
public class ScatteredVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;
    //手机的imei号
    @DatabaseField
    private String imei;
    //GPS间隔时间
    @DatabaseField
    private int id;

    public ScatteredVo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
