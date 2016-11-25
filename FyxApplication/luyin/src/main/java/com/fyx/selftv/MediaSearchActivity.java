package com.fyx.selftv;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fyx.adapter.MediaAdapter;
import com.fyx.asy.VideoUpdateTask;
import com.fyx.bean.VideoItem;
import com.fyx.permission.AfterPermissionGranted;
import com.fyx.permission.AppSettingsDialog;
import com.fyx.permission.EasyPermissions;

import java.util.ArrayList;
import java.util.List;

public class MediaSearchActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MediaSearchActivity";

    private ListView list_video;
    private ArrayList<VideoItem> videoList;
    //    private MediaAdapter mediaAdapter;
    private AsyncTask mVideoUpdateTask;

    private static final int RC_READ_EXTERNAL_STORAGE = 122;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_search);

        initView();
//        initData();
        initThread();
    }


    private void initView() {
        list_video = (ListView) findViewById(R.id.list_video);
        videoList = new ArrayList<>();
        list_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(MediaSearchActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();
                VideoItem item = videoList.get(position);
                Intent i = new Intent(MediaSearchActivity.this, VideoPlayerActivity.class);
                i.setData(Uri.parse(item.getPath()));
                startActivity(i);
            }
        });
    }

    /**
     * 异步加载所有视频
     */
    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    private void initThread() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            videoList.clear();
            MediaAdapter mediaAdapter = new MediaAdapter(this,videoList, R.layout.adp_media_item);
            list_video.setAdapter(mediaAdapter);
            mVideoUpdateTask = new VideoUpdateTask(this, list_video, videoList,null);
            mVideoUpdateTask.execute();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_read), RC_READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * 初始化和刷新数据
     */
    /*@AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    private void initData() {
        if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(this, "TODO: READ_EXTERNAL_STORAGE ok", Toast.LENGTH_LONG).show();

            //清空数据
            videoList.clear();
            //查询视频文件指向外部存储的uri:EXTERNAL_CONTENT_URI,内部存储uri:INTERNAL_CONTENT_URI
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] searchKey = new String[]{
                    MediaStore.Video.Media.TITLE, //-->对应文件的标题
                    MediaStore.Images.Media.DATA, //-->对应文件的存放位置
                    MediaStore.Images.Media.DATE_ADDED //-->对应文件的创建时间
            };
            String [] keywords = null;
            //查询所有叫做Movies的目录
            String where = MediaStore.Video.Media.DATA + " like \"%" + "/Movies" + "%\"";
            //默认排序方式
            String sortOrder = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
            //获取ContentResolver对象,让它使用前面的参数向Media Provider发起查询请求
            ContentResolver resolver = getContentResolver();
            Cursor cursor = resolver.query(
                    uri,
                    searchKey,
                    where,
                    null,
                    sortOrder);
            if(cursor != null) {
                Log.d(TAG, "onPermissionsGranted:" + cursor.getCount() );
                while (cursor.moveToNext()) {
                    //获取视频的存放路径
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    //获取视频的标题
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    //获取视频的创建时间
                    String createdTime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                    VideoItem videoItem = new VideoItem(path, name, createdTime);
                    videoList.add(videoItem);
                }
                //Cursor使用完了之后要把它关闭掉
                cursor.close();
            }
            mediaAdapter = new MediaAdapter(this,videoList,R.layout.adp_media_item);
            list_video.setAdapter(mediaAdapter);

        }else{
            EasyPermissions.requestPermissions(this,getString(R.string.rationale_read),RC_READ_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }*/

    private MenuItem mRefreshMenuItem;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sp_list_menu, menu);
        mRefreshMenuItem  = menu.findItem(R.id.menu_refresh);
//        //当VideoUpdateTask处于运行的状态时，菜单项的标题显示“停止刷新”，
//        if((mVideoUpdateTask != null) && (mVideoUpdateTask.getStatus() == AsyncTask.Status.RUNNING)) {
//            mRefreshMenuItem.setTitle(R.string.stop_refresh);
//        }
//        //当VideoUpdateTask没有处于运行的状态时，菜单项的标题显示“刷新”，
//        else {
//            mRefreshMenuItem.setTitle(R.string.refresh);
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                if((mVideoUpdateTask != null) && (mVideoUpdateTask.getStatus() == AsyncTask.Status.RUNNING)) {
                    //当VideoUpdateTask处于运行的状态时，取消VideoUpdateTask的工作
                    mVideoUpdateTask.cancel(true);
                    mVideoUpdateTask = null;
                    mRefreshMenuItem.setTitle(R.string.refresh);
                }else {
                    videoList.clear();
                    //当VideoUpdateTask没有处于运行的状态时，启动VideoUpdateTask的工作
                    mVideoUpdateTask = new VideoUpdateTask(this,list_video,videoList,mRefreshMenuItem);
                    mVideoUpdateTask.execute();
                    //修改菜单项的标题为“停止刷新”
                    if(mRefreshMenuItem != null) {
                        mRefreshMenuItem.setTitle(R.string.stop_refresh);
                    }
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), null /* click listener */)
                    .setRequestCode(RC_READ_EXTERNAL_STORAGE)
                    .build()
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ((mVideoUpdateTask != null) &&(mVideoUpdateTask.getStatus() == AsyncTask.Status.RUNNING)) {
            mVideoUpdateTask.cancel(true);
        }
        mVideoUpdateTask = null;
    }
}
