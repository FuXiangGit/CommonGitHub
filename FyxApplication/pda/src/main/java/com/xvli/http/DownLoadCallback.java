package com.xvli.http;

import org.xutils.common.Callback;

import java.io.File;

/**
 * Created by Administrator on 2016/1/13.
 */
public interface DownLoadCallback<File> extends Callback {
    void onStart(String startMsg);
    void onSuccess(String filePath);
    void onLoading(long total, long current, boolean isDownloading);
    void onError(Throwable ex, boolean isOnCallback,String errMsg);

}
