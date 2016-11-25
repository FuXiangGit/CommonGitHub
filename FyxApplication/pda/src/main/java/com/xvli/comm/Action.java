package com.xvli.comm;

import java.io.Serializable;

public class Action implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 通用传值
     */
    private Object commObj;

    private Object commObj_1;
    
    public Object getCommObj()
    {
        return commObj;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setCommObj(Object commObj)
    {
        this.commObj = commObj;
    }

    public Object getCommObj_1()
    {
        return commObj_1;
    }

    public void setCommObj_1(Object commObj_1)
    {
        this.commObj_1 = commObj_1;
    }
    
    
}
