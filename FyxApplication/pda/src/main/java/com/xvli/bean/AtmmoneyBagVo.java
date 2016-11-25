package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 迪堡   机具对应的钞包编码
 */
@DatabaseTable(tableName = "T_MONEY_BAG")
public class AtmmoneyBagVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;

    //钞箱抄袋0为钞箱，1为钞袋    2 卡钞  3 废钞   6 钞包    5 扎袋   7 Tebag   8 废钞箱（泰国项目 作为 上下机具操作）
    @DatabaseField
    private int bagtype;

    // 钞箱抄袋编号
    @DatabaseField
    private String barcodeno;

    @DatabaseField
    private String clientid;

    //机具对应的任务id
    @DatabaseField
    private String taskid;

    //钞箱抄袋对应的机具
    @DatabaseField
    private String atmid;

    //机具编号
    @DatabaseField
    private String atmno;

    //网点id
    @DatabaseField
    private String branchname;

    //计划外的branchid设置为 -1
    @DatabaseField
    private String branchid;


    //钞箱抄袋对应的运送和回收状态（运送状态0，回收状态1）
    @DatabaseField
    private int sendOrRecycle;

    @DatabaseField
    private String isScan = "N";//是否扫描

    //是否是计划中数据
    @DatabaseField
    private String isPlan = "Y";


    //从接口获取 出库清单 isOut 都标记为 Y 有数据就修改没数据就添加
    //是出库数据 Y 为 是出库数据  N为不是出库数据
    @DatabaseField
    private String isOut = "Y";

    //从接口获取 获取入库清单 inPda 已经存在的标记为N  不存在的直接添加
    // 是否在Pda上  Y为 在即为出库物品 N为已经入库不需要出库
    @DatabaseField
    private String inPda = "Y";


    public String getMoneyBag() {
        return moneyBag;
    }

    public void setMoneyBag(String moneyBag) {
        this.moneyBag = moneyBag;
    }

    public String getBarcode() {
        return barcodeno;
    }

    public void setBarcode(String barcode) {
        this.barcodeno = barcode;
    }

    @DatabaseField

    private String moneyBag ; //钞包编码



    //操作时间
    @DatabaseField
    private String operatedtime ;
    public AtmmoneyBagVo() {
    }

    public String getOperatedtime() {
        return operatedtime;
    }

    public void setOperatedtime(String operatedtime) {
        this.operatedtime = operatedtime;
    }

    public String getAtmid() {
        return atmid;
    }

    public void setAtmid(String atmid) {
        this.atmid = atmid;
    }


    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }


    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public int getBagtype() {
        return bagtype;
    }

    public void setBagtype(int bagtype) {
        this.bagtype = bagtype;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getBranchid() {
        return branchid;
    }

    public void setBranchid(String branchid) {
        this.branchid = branchid;
    }

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }

    public int getSendOrRecycle() {
        return sendOrRecycle;
    }

    public void setSendOrRecycle(int sendOrRecycle) {
        this.sendOrRecycle = sendOrRecycle;
    }

    public String getAtmno() {
        return atmno;
    }

    public void setAtmno(String atmno) {
        this.atmno = atmno;
    }

    public String getIsScan() {
        return isScan;
    }

    public void setIsScan(String isScan) {
        this.isScan = isScan;
    }

    public String getIsPlan() {
        return isPlan;
    }

    public void setIsPlan(String isPlan) {
        this.isPlan = isPlan;
    }

    public String getInPda() {
        return inPda;
    }

    public void setInPda(String inPda) {
        this.inPda = inPda;
    }

    public String getIsOut() {
        return isOut;
    }

    public void setIsOut(String isOut) {
        this.isOut = isOut;
    }
}
