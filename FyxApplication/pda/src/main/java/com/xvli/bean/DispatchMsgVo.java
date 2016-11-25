package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 调度信息表
 */
@DatabaseTable(tableName = "DISPATCH_VO")
public class DispatchMsgVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    //c成功时间
    @DatabaseField
    private String time;

    //预警内容
    @DatabaseField
    private String content;

    @DatabaseField
    private String message;

    @DatabaseField
    private String taskinfoid;




    public DispatchMsgVo() {

    }


    public String getTaskinfoid() {
        return taskinfoid;
    }

    public void setTaskinfoid(String taskinfoid) {
        this.taskinfoid = taskinfoid;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}