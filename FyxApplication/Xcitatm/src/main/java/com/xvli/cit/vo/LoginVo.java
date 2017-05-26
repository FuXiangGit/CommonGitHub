package com.xvli.cit.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/09/28.
 * 队长用户登录
 */
@DatabaseTable(tableName = "LOGIN_VO")
public class LoginVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;

    @DatabaseField
    private String name;

    @DatabaseField
    private String pwd;


    @DatabaseField
    private String jobnumber;

    @DatabaseField
    private String department;

    @DatabaseField
    private boolean iscaptain;// 是否是队长

    @DatabaseField
    private int userstate;// 1 为未签到  2 为已签到  3 为签出


    @DatabaseField
    private int truckState ;//  0：未出车；1：路途中(车行走 出车 到 下车/ 上车完成之后1 )；2：网点操作中（到达网点 到 离开网点）；3：车辆返回  4  行走中（人走路 下车到 到达网点 \ 离开网点到上车）

    //构造方法
    public LoginVo() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean iscaptain() {
        return iscaptain;
    }

    public void setIscaptain(boolean iscaptain) {
        this.iscaptain = iscaptain;
    }


    public int getUserstate() {
        return userstate;
    }

    public void setUserstate(int userstate) {
        this.userstate = userstate;
    }


    public int getTruckState() {
        return truckState;
    }

    public void setTruckState(int truckState) {
        this.truckState = truckState;
    }
}
