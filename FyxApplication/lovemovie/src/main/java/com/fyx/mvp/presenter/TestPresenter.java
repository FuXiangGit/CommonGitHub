package com.fyx.mvp.presenter;

import android.os.Handler;

import com.fyx.listener.ListenCallBack;
import com.fyx.mvp.bean.TestPresentBean;
import com.fyx.mvp.iview.ITestPresentView;
import com.fyx.mvp.model.TestPresentModel;

/**
 * Created by Mrfu on 2017/4/28.
 * 测试版本的present模式
 */

public class TestPresenter {

    Handler mHandler = new Handler();

    ITestPresentView iTestPresentView;
    TestPresentModel testPresentbiz;

    public TestPresenter(ITestPresentView iTestPresentView) {
        this.iTestPresentView = iTestPresentView;
        testPresentbiz = new TestPresentModel();
    }

    public void loadData(){
        iTestPresentView.showLoading();
        testPresentbiz.loadData("张三", "123", new ListenCallBack() {
            @Override
            public void loadSuccess(final TestPresentBean testPresentBean) {
                //需要在UI线程执行
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iTestPresentView.showUserInfo(testPresentBean);
                        iTestPresentView.dismissLoading();
                    }
                });

            }

            @Override
            public void loadFailed() {
                //需要在UI线程执行
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iTestPresentView.dismissLoading();
                    }
                });
            }
        });
    }

}
