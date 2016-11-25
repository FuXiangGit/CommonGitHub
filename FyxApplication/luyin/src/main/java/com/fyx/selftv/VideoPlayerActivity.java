package com.fyx.selftv;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.fyx.bean.VideoItem;

import config.Config;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private TableLayout tabDetail;
    private TextView title, created, screen, fileSize;
    private String path;
    private int mLastPlayedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        initView();
        initData();
        initPlay();
        //如果是横屏
        if (this.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
            tabDetail.setVisibility(View.GONE);
        }
    }

    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.video_view);
        tabDetail = (TableLayout) findViewById(R.id.tabDetail);
        title = (TextView) findViewById(R.id.video_title);
        created = (TextView) findViewById(R.id.video_create_time);
        screen = (TextView) findViewById(R.id.video_width_height);
        fileSize = (TextView) findViewById(R.id.video_size);
    }

    private void initPlay() {
        mVideoView.setVideoPath(path);
        MediaController controller = new MediaController(this);
        mVideoView.setMediaController(controller);
    }

    /**
     * 找出视频信息
     */
    private void initData() {
        Uri uri = getIntent().getData();
        path = uri.getPath();
        String[] searchKey = new String[]{
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED
        };
        String where = MediaStore.Video.Media.DATA + " = '" + path + "'";
        String[] keywords = null;
        String sortOrder = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, searchKey, where, keywords, sortOrder);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                String createdTime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
                VideoItem item = new VideoItem(path, name, createdTime);

                title.setText(item.name);
                created.setText(item.createdTime);
                screen.setText(width + "*" + height);
                fileSize.setText(String.valueOf(size / 1024 / 1024) + "M");
            } else {
                title.setText(R.string.unknown);
                created.setText(R.string.unknown);
                screen.setText(R.string.unknown);
                fileSize.setText(R.string.unknown);
            }
            cursor.close();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastPlayedTime = savedInstanceState.getInt(Config.LAST_PLAYED_TIME);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mLastPlayedTime > 0) {
            mVideoView.seekTo(mLastPlayedTime);
        }
        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
        mLastPlayedTime = mVideoView.getCurrentPosition();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Config.LAST_PLAYED_TIME, mVideoView.getCurrentPosition());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
