package com.fyx.asy;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.ListView;

import com.fyx.adapter.MediaAdapter;
import com.fyx.bean.VideoItem;
import com.fyx.selftv.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/27 0027.
 */
public class VideoUpdateTask extends AsyncTask<Object, VideoItem, Void>  {
    private Context mContext;
    private ListView mListView;
    private ArrayList<VideoItem> mVideoItems;
    private MenuItem mRefreshMenuItem = null;


    public VideoUpdateTask(Context context, ListView listView, ArrayList<VideoItem> lists, MenuItem menuItem) {
        this.mContext = context;
        this.mListView = listView;
        this.mVideoItems = lists;
        this.mRefreshMenuItem = menuItem;
    }

    @Override
    protected Void doInBackground(Object... params) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] searchKey = new String[] {
                MediaStore.Video.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };
        String [] keywords = null;
        String where = MediaStore.Video.Media.DATA + " like \"%"+"/Movies"+"%\"";
        String sortOrder = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(
                uri,
                searchKey,
                where,
                keywords,
                sortOrder);
        if(cursor != null)
        {
            while(cursor.moveToNext() && ! isCancelled())
            {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String createdTime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                VideoItem item = new VideoItem(path, name, createdTime);
                publishProgress(item);
            }
            cursor.close();
        }
        return null;
    }

    /**
     * 进度控制刷新（找到一个刷新一次）
     * @param progresses
     */
    @Override
    protected void onProgressUpdate(VideoItem... progresses) {
        VideoItem item = (VideoItem) progresses[0];
        mVideoItems.add(item);
        //更新界面
//        MediaAdapter mediaAdapter = new MediaAdapter(mContext,mVideoItems, R.layout.adp_media_item);
//        mListView.setAdapter(mediaAdapter);
//        mediaAdapter.notifyDataSetChanged();
        MediaAdapter mediaAdapter = (MediaAdapter) mListView.getAdapter();
        mediaAdapter.notifyDataSetChanged();
    }

    /**
     * 所有的数据加载完了
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //更新界面
        MediaAdapter mediaAdapter = (MediaAdapter) mListView.getAdapter();
        mediaAdapter.notifyDataSetChanged();
        if(mRefreshMenuItem!=null){
            mRefreshMenuItem.setTitle(R.string.refresh);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(mRefreshMenuItem!=null){
            mRefreshMenuItem.setTitle(R.string.refresh);
        }
    }
}
