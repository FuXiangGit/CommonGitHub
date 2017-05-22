package com.fyx.mvp.iview;

import com.fyx.mvp.bean.TestPresentBean;

/**
 * Created by Mrfu on 2017/4/28.
 */

public interface ITestPresentView {

    void findView();
    void showLoading();
    void dismissLoading();
    void showUserInfo(TestPresentBean bean);

}
