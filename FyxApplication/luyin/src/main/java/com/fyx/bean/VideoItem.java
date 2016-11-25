package com.fyx.bean;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.fyx.util.Utils;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class VideoItem {

    public String name;//视频名字
    public String path;//视频路径
    public Bitmap thumb;//视频缩略图
    public String createdTime;//视频创建时间

    public VideoItem(String strPath, String strName, String createdTime) {
        this.name = strName;
        this.path = strPath;
        this.createdTime = Utils.getFormatTime(createdTime);
        //获取视频的缩略图MINI_KIND表示小的缩略图； FULL_SCREEN_KIND表示大尺寸的缩略图；MICRO_KIND表示超小图的缩略图；
        this.thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
