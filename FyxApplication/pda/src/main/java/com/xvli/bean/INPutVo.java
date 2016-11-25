package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 9:54.
 */
//入库清单表
@DatabaseTable (tableName="T_INPUTVO")
public class INPutVo  implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String type ;  //0 钞箱  1抄袋  2 卡抄 3 废钞

    @DatabaseField
    private String code; //二维码

    @DatabaseField
    private String isScan;//是否扫描

    @DatabaseField
    private String operatetime;//操作时间

    @DatabaseField
    private String isUp = "N";//是否上已上机具

    public String getIsUp() {
        return isUp;
    }

    public void setIsUp(String isUp) {
        this.isUp = isUp;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIsScan() {
        return isScan;
    }

    public void setIsScan(String isScan) {
        this.isScan = isScan;
    }
}
