package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 机具分线路显示
 */
@DatabaseTable(tableName = "TAI_LINE_VO")
public class TaiLineVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;

    //客户ID号
    @DatabaseField
    private String customerid;

    //线路id
    @DatabaseField
    private String linenchid;


    @DatabaseField
    private String customername;

    //机具所属线路名称
    @DatabaseField
    private String linenumber;

    //机具所属线路类型  :0 作业,1巡检,2维修
    @DatabaseField
    private int linetype;

    //线路类型名字
    @DatabaseField
    private String Linetypenm;


    //是否是撤销任务  如果atm 下的所有任务都撤销 则该 atm也不用显示 Y为是取消时任   N 为没取消需要显示
    @DatabaseField
    private String iscancel = "N";

    // 操作时间
    @DatabaseField
    private String operatedtime;

    // 操作人，jobnumber用逗号隔开
    @DatabaseField
    private String operator;


    public TaiLineVo() {
    }

    public int getLinetype() {
        return linetype;
    }

    public void setLinetype(int linetype) {
        this.linetype = linetype;
    }

    public String getLinetypenm() {
        return Linetypenm;
    }

    public void setLinetypenm(String linetypenm) {
        Linetypenm = linetypenm;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getLinenchid() {
        return linenchid;
    }

    public void setLinenchid(String linenchid) {
        this.linenchid = linenchid;
    }


    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }


    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }


    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }


    public String getOperatedtime() {
        return operatedtime;
    }

    public void setOperatedtime(String operatedtime) {
        this.operatedtime = operatedtime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }


    public String getLinenumber() {
        return linenumber;
    }

    public void setLinenumber(String linenumber) {
        this.linenumber = linenumber;
    }

    public String getIscancel() {
        return iscancel;
    }

    public void setIscancel(String iscancle) {
        this.iscancel = iscancle;
    }
}