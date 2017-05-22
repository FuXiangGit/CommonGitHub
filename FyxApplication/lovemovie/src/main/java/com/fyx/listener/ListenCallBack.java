package com.fyx.listener;

import com.fyx.mvp.bean.TestPresentBean;

/**
 * Created by Mrfu on 2017/5/9.
 */

public interface ListenCallBack {
        void loadSuccess(TestPresentBean testPresentBean);

        void loadFailed();
}
