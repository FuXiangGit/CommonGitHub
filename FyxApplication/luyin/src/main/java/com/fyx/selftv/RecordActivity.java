package com.fyx.selftv;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener{
    private Button start,stop,show,showend;

//    private String fileName = Environment.getExternalStorageDirectory() + "/test/audiorecordtest.3gp";
    private String fileName = Environment.getExternalStorageDirectory() + "/test/video.mp4";
    private MediaRecorder recorder;

    private MediaPlayer mPlayer;
    private static final int REQUEST_PERMISSIONS = 10;
    private boolean isLuyin = false;

    //录像屏幕方向提示
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    //屏幕的宽和高
    private static  int DISPLAY_WIDTH = 720;
    private static  int DISPLAY_HEIGHT = 1280;

    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;
    private static final int REQUEST_CODE = 1000;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        initView();
        initData();

    }

    private void initView() {
        start = (Button) findViewById(R.id.btn_luyin);
        stop = (Button) findViewById(R.id.btn_stop);
        show = (Button) findViewById(R.id.btn_show_start);
        showend = (Button) findViewById(R.id.btn_show_stop);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        show.setOnClickListener(this);
        showend.setOnClickListener(this);
        initBrocast();
    }

    private void initData() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;

//        int mwidthPixels = metrics.widthPixels;//得到宽度
//        int heightPixels = metrics.heightPixels;//得到高度
        DISPLAY_WIDTH = metrics.widthPixels;//得到宽度
        DISPLAY_HEIGHT = metrics.heightPixels;//得到高度
//        mMediaRecorder = new MediaRecorder();
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_luyin:
                getQuanxian();
//                startLuyin();
                break;
            case R.id.btn_stop:
                getQuanxian();
//                stopLuyin();
                break;
            case R.id.btn_show_start:
//                bofangLuyin();
                testService();
                break;
            case R.id.btn_show_stop:
//                bofangStop();
                stoptest();
                break;
        }
    }

    private void stoptest() {
        stopService(new Intent(RecordActivity.this,LuyinService.class));
    }

    private void testService() {
        startService(new Intent(RecordActivity.this,LuyinService.class));
    }

    /**
     * 检查是否有权限
     */
    private void getQuanxian() {

        if (ContextCompat.checkSelfPermission(RecordActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)  + ContextCompat.checkSelfPermission(RecordActivity.this,  Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("jack","检查是否有权限后，需要申请权限就到这了checkSelfPermission");
            if (ActivityCompat.shouldShowRequestPermissionRationale (RecordActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale (RecordActivity.this, Manifest.permission.RECORD_AUDIO)) {
                Log.d("jack","检查是否有权限后，是否确定不在弹出就到这了需要申请权限就到这了shouldShowRequestPermissionRationale");
                Snackbar.make(findViewById(android.R.id.content), "请允许权限",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(RecordActivity.this,
                                        new String[]{Manifest.permission
                                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                        REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                Log.d("jack","检查是否有权限后，是否确定不在弹出就到这了需要申请权限就到这一直手动控制默认弹出到这了");
                ActivityCompat.requestPermissions(RecordActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        REQUEST_PERMISSIONS);
            }
        } else {//有权限到这里 了
            if(isLuyin){
                Log.d("jack","有权限到这里结束 了");
                stopLuyin();
            }else {
                Log.d("jack","有权限到这里开始了");
                startLuyin();
            }
        }
    }

    /**
     * 结束播放
     */
    private void bofangStop() {
        mPlayer.release();
        mPlayer = null;
    }

    /**
     * 播放录音
     */
    private void bofangLuyin() {
        mPlayer = new MediaPlayer();
        try{
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        }catch(IOException e){
            Log.e("jack","播放失败");
        }
    }

    private void stopLuyin() {
        //added by ouyang start

        try {
            //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
            recorder.setOnErrorListener(null);
            recorder.setOnInfoListener(null);
            recorder.setPreviewDisplay(null);
            recorder.stop();
        } catch (IllegalStateException e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        }catch (RuntimeException e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        }catch (Exception e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        }
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
//        recorder.release(); // Now the object cannot be reused
        recorder = null;
        isLuyin = false;
        stopScreenSharing();
    }

    /**
     * 开始录制
     */
    private void startLuyin() {

        initRecorder();
        shareScreen();
    }

    private void shareScreen() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        recorder.start(); // Recording is now started
        isLuyin = true;
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("RecordActivity",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                recorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    private void initRecorder() {
        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置用于录制的音频源。
        recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);//设置用于录制的视频源。
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//设置在录制过程中产生的输出文件的格式。
        recorder.setOutputFile(fileName);//在要写入的文件的文件描述符中传递。
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//设置用于录制的视频编码器。
        recorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);//设置要捕获的视频的宽度和高度。
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置用于录制的音频编码器。
//        recorder.setVideoEncodingBitRate(512 * 1000);//设置用于录制的音频编码比特率。
        recorder.setVideoEncodingBitRate(5*1024*1024);//设置用于录制的音频编码比特率。//这个值越大越清晰，用8*1024*1024保存差不多一秒1M，太大了

        recorder.setVideoFrameRate(15);//设置要捕获的视频的帧速率。一般有30是比较高的了，太低了就会卡顿，游戏录屏是要高点的比较好推荐30和25，还是有点用的，不设置可能会模糊
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Log.d("jack",rotation+"hahahahaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        int orientation = ORIENTATIONS.get(rotation + 90);
        recorder.setOrientationHint(orientation);//设置输出视频播放的方向提示。

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if (isLuyin) {
//                mToggleButton.setChecked(false);
                isLuyin = false;
                recorder.stop();
                recorder.reset();
                Log.v("jack", "Recording Stopped");
            }
            mMediaProjection = null;
            stopScreenSharing();
        }
    }

    private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        //mMediaRecorder.release(); //If used: mMediaRecorder object cannot be reused again
        destroyMediaProjection();
    }
    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i("jack", "MediaProjection Stopped");
    }

    @Override
    public void  onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            Log.e("jack", "Unknown request code: " + resultCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,  "Screen Cast Permission Denied权限拒绝了", Toast.LENGTH_SHORT).show();
//            mToggleButton.setChecked(false);//设置不可点击了
            return;
        }
        mMediaProjectionCallback = new MediaProjectionCallback();
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
        mVirtualDisplay = createVirtualDisplay();
        recorder.start();
        isLuyin = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                Log.d("jack","申请权限后到这了");
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    if(isLuyin){
                        stopLuyin();
                    }else {
                        startLuyin();
                    }
                } else {//用户没有给权限
                    Snackbar.make(findViewById(android.R.id.content), "需要权限",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }

    private MyBroadcastReceiver mBroadCast;
    private void initBrocast() {
        mBroadCast = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppAction.LUYIN_START);
        intentFilter.addAction(AppAction.LUYIN_STOP);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String TAG = "MyBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w(TAG, "intent:" + intent);
            String name = intent.getStringExtra("name");


        }
    }

}
