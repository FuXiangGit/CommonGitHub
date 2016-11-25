package com.xvli.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/7.
 */
@DatabaseTable(tableName = "LOGIN_VO")
public class LoginVo implements Serializable {
    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField
    private String clientid;
    @DatabaseField
    private String name1;
    @DatabaseField
    private String name2;
    @DatabaseField
    private String name3;
    @DatabaseField
    private String pwd1;
    @DatabaseField
    private String pwd2;
    @DatabaseField
    private String pwd3;
    @DatabaseField
    private String jobnumber1;
    @DatabaseField
    private String jobnumber2;
    @DatabaseField
    private String jobnumber3;
    @DatabaseField
    private String department1;
    @DatabaseField
    private String department2;
    @DatabaseField
    private String department3;
    @DatabaseField
    private String trucknumber;//计划车牌号，目前不用显示，不用管
    @DatabaseField
    private String truckid;
    @DatabaseField
    private String user1logintime;//用户1登录时间
    //上次登录时间
    @DatabaseField
    private String user2logintime;
    @DatabaseField
    private String user3logintime;//用户3登陆时间
    @DatabaseField
    private String bindtruckcode;//实际绑定的车辆编码
    @DatabaseField
    private String bindplatenumber; //实际绑定的车牌号
    @DatabaseField
    private String bindtime;//实际绑定车辆的时间
    //实际绑定的车牌号的状态
    //0.未曾扫描
    //1.未曾联网操作绑定与解绑的操作但已扫描需要联网操作
    //2.已联网绑定车辆
    //3.已联网解绑车辆
    //4.已联网绑定车辆但等待联网解绑
    //5.已联网解绑车辆但已扫描等待联网绑定
    @DatabaseField
    private String bindtruck_stuts = "0";
    @DatabaseField
    private String local_login_time;//本地记录登录时间，如果再次登录，先比较这次时间，如果在同一天，则不访问服务器
    @DatabaseField
    private String local_task_time;//本地记录获取或更新任务的时间xxxx年xx月xx日 aa:bb:cc

    @DatabaseField
    private String tasktype="";//计划中当天任务类型(0全天，1上午，2下午，默认为"")

    //行车状态记录  0：未出车；1：路途中(行驶中 出车 到 下车 )；2：网点操作中；3：车辆返回  4  行走中（下车到 到达网点 \ 离开网点到出车）

    @DatabaseField
    private String truckState ;

    @DatabaseField
    private String workercard1;//员工卡1

    @DatabaseField
    private String workercard2;//员工卡2

    @DatabaseField
    private String workercard3;//员工卡3

    public String getWorkercard1() {
        return workercard1;
    }

    public void setWorkercard1(String workercard1) {
        this.workercard1 = workercard1;
    }

    public String getWorkercard3() {
        return workercard3;
    }

    public void setWorkercard3(String workercard3) {
        this.workercard3 = workercard3;
    }

    public String getWorkercard2() {
        return workercard2;
    }

    public void setWorkercard2(String workercard2) {
        this.workercard2 = workercard2;
    }





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

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getPwd1() {
        return pwd1;
    }

    public void setPwd1(String pwd1) {
        this.pwd1 = pwd1;
    }

    public String getPwd2() {
        return pwd2;
    }

    public void setPwd2(String pwd2) {
        this.pwd2 = pwd2;
    }

    public String getPwd3() {
        return pwd3;
    }

    public void setPwd3(String pwd3) {
        this.pwd3 = pwd3;
    }

    public String getJobnumber1() {
        return jobnumber1;
    }

    public void setJobnumber1(String jobnumber1) {
        this.jobnumber1 = jobnumber1;
    }

    public String getJobnumber2() {
        return jobnumber2;
    }

    public void setJobnumber2(String jobnumber2) {
        this.jobnumber2 = jobnumber2;
    }

    public String getJobnumber3() {
        return jobnumber3;
    }

    public void setJobnumber3(String jobnumber3) {
        this.jobnumber3 = jobnumber3;
    }

    public String getDepartment1() {
        return department1;
    }

    public void setDepartment1(String department1) {
        this.department1 = department1;
    }

    public String getDepartment2() {
        return department2;
    }

    public void setDepartment2(String department2) {
        this.department2 = department2;
    }

    public String getDepartment3() {
        return department3;
    }

    public void setDepartment3(String department3) {
        this.department3 = department3;
    }

    public String getTruckid() {
        return truckid;
    }

    public void setTruckid(String truckid) {
        this.truckid = truckid;
    }

    public String getTrucknumber() {
        return trucknumber;
    }

    public void setTrucknumber(String trucknumber) {
        this.trucknumber = trucknumber;
    }

    public String getUser1logintime() {
        return user1logintime;
    }

    public void setUser1logintime(String user1logintime) {
        this.user1logintime = user1logintime;
    }

    public String getUser2logintime() {
        return user2logintime;
    }

    public void setUser2logintime(String user2logintime) {
        this.user2logintime = user2logintime;
    }

    public String getUser3logintime() {
        return user3logintime;
    }

    public void setUser3logintime(String user3logintime) {
        this.user3logintime = user3logintime;
    }

    public String getBindtruckcode() {
        return bindtruckcode;
    }

    public void setBindtruckcode(String bindtruckcode) {
        this.bindtruckcode = bindtruckcode;
    }

    public String getBindplatenumber() {
        return bindplatenumber;
    }

    public void setBindplatenumber(String bindplatenumber) {
        this.bindplatenumber = bindplatenumber;
    }

    public String getBindtime() {
        return bindtime;
    }

    public void setBindtime(String bindtime) {
        this.bindtime = bindtime;
    }

    public String getBindtruck_stuts() {
        return bindtruck_stuts;
    }

    public void setBindtruck_stuts(String bindtruck_stuts) {
        this.bindtruck_stuts = bindtruck_stuts;
    }

    public String getLocal_login_time() {
        return local_login_time;
    }

    public void setLocal_login_time(String local_login_time) {
        this.local_login_time = local_login_time;
    }

    public String getLocal_task_time() {
        return local_task_time;
    }

    public void setLocal_task_time(String local_task_time) {
        this.local_task_time = local_task_time;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public String getTruckState() {
        return truckState;
    }

    public void setTruckState(String truckState) {
        this.truckState = truckState;
    }
}
