package com.fyx.bean;

import java.util.LinkedList;

/**
 * 作者 ：付昱翔
 * 时间 ：2017/12/26
 * 描述 ：
 */
public class ExpFatherBean {
    private String strExpTitle;
    private LinkedList<ExpSonBean> sonBeanList = new LinkedList<>();
    private boolean isShow;
    private int id;//不重复

    public void addSonBean(ExpSonBean expSonBean) {
        sonBeanList.add(expSonBean);
    }

    /**
     * 根据ID删除
     * @param sonId
     */
    public void deleteSonBean(int sonId) {
        for (ExpSonBean sonBean : sonBeanList) {
            if (sonId == sonBean.getSonId()) {
                sonBeanList.remove(sonBean);
            }
        }
    }

    /**
     * 根据内容删除
     * @param expSonContent
     */
    public void deleteSonBean(String expSonContent) {
        for (ExpSonBean sonBean : sonBeanList) {
            if (expSonContent == sonBean.getExpSonContent()) {
                sonBeanList.remove(sonBean);
            }
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrExpTitle() {
        return strExpTitle;
    }

    public void setStrExpTitle(String strExpTitle) {
        this.strExpTitle = strExpTitle;
    }

    public LinkedList<ExpSonBean> getSonBeanList() {
        return sonBeanList;
    }

    public void setSonBeanList(LinkedList<ExpSonBean> sonBeanList) {
        this.sonBeanList = sonBeanList;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
