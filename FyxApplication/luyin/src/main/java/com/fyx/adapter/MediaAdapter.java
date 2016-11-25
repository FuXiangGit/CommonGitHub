package com.fyx.adapter;

import android.content.Context;

import com.example.administrator.commadapterlib.CommonAdapter;
import com.example.administrator.commadapterlib.CommonViewHolder;
import com.fyx.bean.VideoItem;
import com.fyx.selftv.R;

import java.util.List;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class MediaAdapter extends CommonAdapter<VideoItem> {
    public MediaAdapter(Context context, List<VideoItem> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(CommonViewHolder commonViewHolder, VideoItem videoItem) {
        commonViewHolder.setImageBitmap(R.id.img_media,videoItem.getThumb());
        commonViewHolder.setText(R.id.media_title,videoItem.getName());
        commonViewHolder.setText(R.id.media_creat_time,videoItem.getCreatedTime());
    }
}
