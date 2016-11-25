package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 泰国对应的物品核对清单
 */
@DatabaseTable(tableName="TAI_THINGS_VO")
public class ThingsVo implements Serializable {
    //数据库自增长ids
    @DatabaseField(generatedId = true)
    private int ids;

    //物品对应id
    @DatabaseField
    private String id;

    //物品名字
    @DatabaseField
    private String name;

    //物品类型  10：抢支    20：工作手机    30：车辆钥匙   40：ATM钥匙   50: ATM密码卡
    @DatabaseField
    private String type;

    @DatabaseField
    private String clientid;

    //二维码
    @DatabaseField
    private String barcode;

    //物品领取人  （钥匙 ，密码）交接使用

    public String getReceiptor() {
        return receiptor;
    }

    public void setReceiptor(String receiptor) {
        this.receiptor = receiptor;
    }

    @DatabaseField
    private String operators;

    public String getOperators() {
        return operators;
    }

    public void setOperators(String operators) {
        this.operators = operators;
    }

    //领取人
    @DatabaseField
    private String  receiptor;

    @DatabaseField
    private String operatedtime;

    public String getOperatedtime() {
        return operatedtime;
    }

    public void setOperatedtime(String operatedtime) {
        this.operatedtime = operatedtime;
    }

    //网点id
    @DatabaseField
    private String lineid;

    //线路名称
    @DatabaseField
    private String linename;

    // 是否已绑定
    @DatabaseField
    private String state;

    @DatabaseField
    private String notes;

    @DatabaseField
    private String flgnm;

    public String getIsUploaded() {
        return IsUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        IsUploaded = isUploaded;
    }

    @DatabaseField

    private String IsUploaded = "N";


    public String getFlgnm() {
        return flgnm;
    }

    public void setFlgnm(String flgnm) {
        this.flgnm = flgnm;
    }

    @DatabaseField
    private int flg;

    public int getFlg() {
        return flg;
    }

    public void setFlg(int flg) {
        this.flg = flg;
    }

    //是否扫描到
    @DatabaseField
    private String isScan ="N";

    //钥匙交接  交接人
    @DatabaseField
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    //交出  接收 //10：交出，20：接受
    @DatabaseField
    private String changeflg;

    public String getChangeflg() {
        return changeflg;
    }

    public void setChangeflg(String changeflg) {
        this.changeflg = changeflg;
    }

    //是否交接
    @DatabaseField
    private String isTransfer = "N";

    public String getIsTransfer() {
        return isTransfer;
    }

    public void setIsTransfer(String isTransfer) {
        this.isTransfer = isTransfer;
    }

    //钥匙交接  接收人
    @DatabaseField
    private String recvice;

    public String getRecvice() {
        return recvice;
    }

    public void setRecvice(String recvice) {
        this.recvice = recvice;
    }

    @DatabaseField
    private String outOrinput="Y"; //Y  为出库  N为入库

    public String getOutOrinput() {
        return outOrinput;
    }

    public void setOutOrinput(String outOrinput) {
        this.outOrinput = outOrinput;
    }

    public ThingsVo() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public String getIsScan() {
        return isScan;
    }

    public void setIsScan(String isScan) {
        this.isScan = isScan;
    }

    public String getLineid() {
        return lineid;
    }

    public void setLineid(String lineid) {
        this.lineid = lineid;
    }

    public String getLinename() {
        return linename;
    }

    public void setLinename(String linename) {
        this.linename = linename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}