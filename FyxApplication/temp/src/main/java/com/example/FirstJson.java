package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/29 0029.
 */
public class FirstJson {
    private String clientid;
    private String Lineid;
    private List<SecondJson> data = new ArrayList<SecondJson>();

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getLineid() {
        return Lineid;
    }

    public void setLineid(String lineid) {
        Lineid = lineid;
    }

    public List<SecondJson> getData() {
        return data;
    }

    public void setData(List<SecondJson> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FirstJson{" +
                "clientid='" + clientid + '\'' +
                ", Lineid='" + Lineid + '\'' +
                ", data=" + data +
                '}';
    }
}
