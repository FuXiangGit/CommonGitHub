package com.xvli.cit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.xvli.cit.IMyAidlInterface;
import com.xvli.cit.R;


/**
 * Created by Stander on 2017/05/13.
 * 双进程守护程序
 */
public class RemoteCastielService extends Service {

    MyBinder myBinder;
    private PendingIntent pintent;
    MyServiceConnection myServiceConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        myServiceConnection = new MyServiceConnection();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(this,CitService.class), myServiceConnection, Context.BIND_IMPORTANT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        // 必需的通知内容  
        builder.setContentTitle("CIT")
                .setContentText("service is running")
                .setSmallIcon(R.drawable.icon);
        Intent notifyIntent = new Intent(this,CitService.class);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this,0,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(notifyPendingIntent);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,notification);


        // 设置service为前台进程，避免手机休眠时系统自动杀掉该服务
        startForeground(1,notification);
        return START_STICKY;
    }


    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Log.i("castiel", "本地服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // 连接出现了异常断开了，LocalCastielService被杀死了
//            Toast.makeText(RemoteCastielService.this, "本地服务Local被干掉", Toast.LENGTH_LONG).show();
            // 启动LocalCastielService
            RemoteCastielService.this.startService(new Intent(RemoteCastielService.this,CitService.class));
            RemoteCastielService.this.bindService(new Intent(RemoteCastielService.this,CitService.class), myServiceConnection, Context.BIND_IMPORTANT);
        }

    }


    class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public String getProName() throws RemoteException {
            return "Remote Service";
        }

    }

}
