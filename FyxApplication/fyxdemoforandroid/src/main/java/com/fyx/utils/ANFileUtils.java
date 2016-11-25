package com.fyx.utils;

import android.os.Environment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class ANFileUtils {
    public static String SDPATH = Environment.getExternalStorageDirectory()+ "/Photo_LJ/";

    public static File createPic(String picName) {
        File file = new File(SDPATH, picName + ".JPEG");
        try {
            if (!file.exists()) {//文件不存在就创建对应文件夹
                FileUtils.forceMkdirParent(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.exists()) {//存在的话就删除
            file.delete();
        }
        return new File(SDPATH, picName + ".JPEG");
    }

    public static File getPic(String picName) {
        return new File(SDPATH, picName + ".JPEG");
    }
}
