package com.fyx.mvp.model;

import com.fyx.listener.ListenCallBack;
import com.fyx.mvp.bean.TestPresentBean;

/**
 * Created by Mrfu on 2017/5/9.
 */

public class TestPresentModel {
    public void loadData(final String name, final String pass, final ListenCallBack listenCallBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TestPresentBean bean = new TestPresentBean();
                bean.setName(name);
                bean.setPassword(pass);
                listenCallBack.loadSuccess(bean);
            }
        }).start();
    }

}
