package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 预警信息表
 */
@DatabaseTable(tableName = "WARN_VO")
public class WarnVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    //预警时间
    @DatabaseField
    private String time;

    //预警内容
    @DatabaseField
    private String content;


    public WarnVo() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}