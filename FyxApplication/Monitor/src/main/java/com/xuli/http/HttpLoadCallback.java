package com.xuli.http;

import org.xutils.common.Callback;


/**
 * Created by Administrator on 2016/1/12.
 */
public interface HttpLoadCallback<String> extends Callback{
    void onSuccess(String result);
    void onError(Throwable ex, boolean isOnCallback, String errMsg);


}
