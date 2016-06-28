package com.fyx.lovescreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.fyx.lovescreen.app.ScreenApplication;
import com.fyx.lovescreen.service.ScreenRecordService;
import com.fyx.lovescreen.utils.Utils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button hengStart,zongStart,recordStop;

    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";
    private int mResultCode;
    private Intent mResultData;

    private int mScreenDensity;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private static final int REQUEST_MEDIA_PROJECTION = 1000;

    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private MediaRecorder mMediaRecorder;
    private static final int REQUEST_PERMISSIONS = 10;
    private boolean isLuping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState != null) {
            mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE);
            mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA);
        }
        initView();
        initData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initData() {
        mMediaRecorder = new MediaRecorder();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mMediaProjectionManager = (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    private void initView() {
        hengStart = (Button) findViewById(R.id.heng_start);
        zongStart = (Button) findViewById(R.id.zong_start);
        recordStop = (Button) findViewById(R.id.record_stop);
        hengStart.setOnClickListener(this);
        zongStart.setOnClickListener(this);
        recordStop.setOnClickListener(this);
        /*if(ScreenApplication.isRecoding){
            hengStart.setVisibility(View.INVISIBLE);
            zongStart.setVisibility(View.INVISIBLE);
            recordStop.setVisibility(View.VISIBLE);
        }else{
            hengStart.setVisibility(View.VISIBLE);
            zongStart.setVisibility(View.VISIBLE);
            recordStop.setVisibility(View.INVISIBLE);
        }*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResultData != null) {
            outState.putInt(STATE_RESULT_CODE, mResultCode);
            outState.putParcelable(STATE_RESULT_DATA, mResultData);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent serviceIntent = new Intent(MainActivity.this, ScreenRecordService.class);
        int recordState = -1;
        switch (view.getId()){
            case R.id.heng_start:
                recordState= Utils.HENG_START;
//                startScreenCapture();
                getQuanxian(recordState);
                break;
            case R.id.zong_start:
                recordState=Utils.ZONG_START;
//                startScreenCapture();
                getQuanxian(recordState);
                break;
            case R.id.record_stop:
                recordState=Utils.RECORD_STOP;
//                stopScreenCapture();
                getQuanxian(recordState);
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("recordState", recordState);
        serviceIntent.putExtras(bundle);
        startService(serviceIntent);
    }

    /**
     * 检查是否有权限
     */
    private void getQuanxian(int recordState) {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale (MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (MainActivity.this, Manifest.permission.RECORD_AUDIO)) {
                Snackbar.make(findViewById(android.R.id.content), "请允许权限",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission
                                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                        REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        REQUEST_PERMISSIONS);
            }
        } else {
            if(recordState==Utils.ZONG_START||recordState==Utils.HENG_START){
                isLuping = false;
                startScreenCapture();
            }else if(recordState==Utils.RECORD_STOP){
                isLuping = true;
                stopScreenCapture();
            }
        }
    }

    private void stopScreenCapture() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        ScreenApplication.isRecoding = false;
    }

    /**
     * 开始录屏按钮点击事件
     */
    private void startScreenCapture() {
//        initRecorder();
        if (mMediaProjection != null) {
            Log.d("jack", "111111111111111111122222222222222222");
            setUpVirtualDisplay();
        } else if (mResultCode != 0 && mResultData != null) {
            Log.d("jack", "111111111111111111122222222222222222333333333333333333333");
            setUpMediaProjection();
            setUpVirtualDisplay();
        } else {
            Log.d("jack", "111111111111111111122222222222222222333333333333333333333444444444444444444");
            // This initiates a prompt dialog for the user to confirm screen projection.
            initRecorder();
            startActivityForResult(  mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        }
    }

    private void setUpVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                DISPLAY_WIDTH, DISPLAY_WIDTH, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null, null);
        ScreenApplication.isRecoding = true;
        mMediaRecorder.start();
    }

    private void setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }

    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private void initRecorder() {
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置用于录制的音频源。
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);//设置用于录制的视频源。
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//设置在录制过程中产生的输出文件的格式。
//            mMediaRecorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/video.mp4");
            mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/test/video.mp4");//在要写入的文件的文件描述符中传递。
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);//设置要捕获的视频的宽度和高度。
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//设置用于录制的视频编码器。
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置用于录制的音频编码器。
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);//设置用于录制的音频编码比特率。
            mMediaRecorder.setVideoFrameRate(30);//设置要捕获的视频的帧速率。
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);//设置输出视频播放的方向提示。
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        if (resultCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != RESULT_OK) {
                Log.i("jack", "User cancelled");
                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
                return;
            }
            mResultCode = resultCode;
            mResultData = data;
            setUpMediaProjection();
            setUpVirtualDisplay();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tearDownMediaProjection();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  @NonNull String permissions[],  @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    if( isLuping){
                        stopScreenCapture();
                    }else{
                        startScreenCapture();
                    }
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "请求权限",
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

}
