package com.fyx.lovescreen.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;

import com.fyx.lovescreen.app.ScreenApplication;
import com.fyx.lovescreen.utils.Utils;

import java.io.IOException;


/**
 * 这里是录像服务
 */
public class ScreenRecordService extends Service {
    private MediaRecorder mMediaRecorder;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;

//    private static final String STATE_RESULT_CODE = "result_code";
//    private static final String STATE_RESULT_DATA = "result_data";

    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;

    public ScreenRecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("jack", "这里创建了Service");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(intent!=null){
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                int recordState = bundle.getInt("recordState");
                switch (recordState){
                    case Utils.HENG_START:
                        hengStart();
                        break;
                    case Utils.ZONG_START:
                        zongStart();
                        break;
                    case Utils.RECORD_STOP:
                        recordStop();
                        break;
                }
            }
        }
    }

    /**
     * 横向录屏
     */
    public void hengStart(){
//        if (mSurface == null || activity == null) {
            Log.d("jack","hengStart");
//            return;
//        }
    }

    /**
     * 纵向录屏
     */
    public void zongStart(){
        Log.d("jack","zongStart");
    }

    /**
     * 停止结束录屏
     */
    public void recordStop(){
        Log.d("jack","recordStop");
    }

    //=============================================

}
