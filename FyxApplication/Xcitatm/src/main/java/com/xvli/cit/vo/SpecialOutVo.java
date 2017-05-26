package com.xvli.cit.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/05/22.
 * 特别支出
 */
@DatabaseTable(tableName = "SPECIL_OUT_VO")
public class SpecialOutVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;

    @DatabaseField
    private String category;//特别支出类别

    @DatabaseField
    private int cateindex;//特别支出类别

    @DatabaseField
    private String outtime;//支出时间

    @DatabaseField
    private String address;//地址

    @DatabaseField
    private String feeamount;//费用金额

    @DatabaseField
    private String remark;// 备注

    @DatabaseField
    private String operator; // 操作人

    @DatabaseField
    private String operatetime;// 操作时间

    //构造方法
    public SpecialOutVo() {
    }


    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOuttime() {
        return outtime;
    }

    public void setOuttime(String outtime) {
        this.outtime = outtime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFeeamount() {
        return feeamount;
    }

    public void setFeeamount(String feeamount) {
        this.feeamount = feeamount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
    }

    public int getCateindex() {
        return cateindex;
    }

    public void setCateindex(int cateindex) {
        this.cateindex = cateindex;
    }
}
