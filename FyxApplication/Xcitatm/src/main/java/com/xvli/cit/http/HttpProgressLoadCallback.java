package com.xvli.cit.http;

import org.xutils.common.Callback;


/**
 * Created by Administrator on 2016/1/12.
 */
public interface HttpProgressLoadCallback<String> extends Callback{
    void onStart(String startMsg);
    void onSuccess(String result);
    void onLoading(long total, long current, boolean isDownloading);
    void onError(Throwable ex, boolean isOnCallback, String errMsg);
    void onFinished(String finishMsg);
}
