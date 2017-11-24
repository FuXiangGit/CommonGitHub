package com.fyx.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/24 0024.
 */
public class ListTypeOperate implements Serializable {
    private int ids;
    private String head;
    private String cotent;
    private String date;
    private String checkBtn;
    private String photo;
    private int type;
    private boolean isChecked;

    public ListTypeOperate() {
    }

    /**
     * @param head     标题
     * @param cotent   操作内容
     * @param date     日期
     * @param checkBtn 选择按钮
     * @param photo    拍照
     * @param type     类型
     */
    public ListTypeOperate(String head, String cotent, String date, String checkBtn, String photo, int type) {
        this.head = head;
        this.cotent = cotent;
        this.date = date;
        this.checkBtn = checkBtn;
        this.photo = photo;
        this.type = type;
    }

    public ListTypeOperate(String head, boolean isChecked) {
        this.head = head;
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getCotent() {
        return cotent;
    }

    public void setCotent(String cotent) {
        this.cotent = cotent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCheckBtn() {
        return checkBtn;
    }

    public void setCheckBtn(String checkBtn) {
        this.checkBtn = checkBtn;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
