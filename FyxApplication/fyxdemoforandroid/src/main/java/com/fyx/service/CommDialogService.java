package com.fyx.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.fyx.utils.Utils;

public class CommDialogService extends Service {
    public CommDialogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Utils.showQuanJuDialog(CommDialogService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.showQuanJuDialog(CommDialogService.this);
        return super.onStartCommand(intent, flags, startId);
    }
}
