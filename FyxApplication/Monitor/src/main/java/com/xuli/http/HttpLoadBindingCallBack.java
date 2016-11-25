package com.xuli.http;

import org.xutils.common.Callback;

/**
 * Created by Administrator on 11:30.
 */
public interface HttpLoadBindingCallBack<String> extends Callback {
    void onSuccess(String result);
    void onError(Throwable ex, boolean isOnCallback, String errMsg);
    void onLoad();
    void onstart();



}
