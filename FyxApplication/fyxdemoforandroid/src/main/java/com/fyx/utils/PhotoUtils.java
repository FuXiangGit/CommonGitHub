package com.fyx.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Mrfu on 2017/5/25.
 */

public class PhotoUtils {
    public static final int REQUEST_CODE_PICK_IMAGE = 1;
    public static final int REQUEST_CODE_CROP_IMAGE = 2;

    public static void takePhto(){

    }

    /**
     * 不建议这样传递Activity，这样会造成内存泄露（这里为了方便备份，所以写出来了）
     * @param activity
     */
    public static void getPhoto(Activity activity){
        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent1, 1);
    }

    /**
     * 根据Uri获取File的文件路径
     * @param contentUri
     * @return
     */
    public static String getFilePathFromContentUri(Context context,Uri contentUri) {
        String filePath=null;
        if(contentUri==null){
            return filePath;
        }
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, filePathColumn, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /**
     * 根据文件获取对应的uri
     * @param file
     * @return
     */
    public Uri getImageUri(File file){
        Uri imageUri = Uri.fromFile(file);
        return imageUri;
    }

    /**
     * 通过uri获取bitmap
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap decodeUriAsBitmap(Context context,Uri uri) {
        Bitmap bitmap = null;
        if(uri==null) {
            return bitmap;
        }
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            Log.d("jack", "bitmap.length:" + bitmap.getByteCount());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 照片裁剪（此方法在原图上进行剪裁，所以建议先复制再剪裁）[解决了大图片裁剪问题]
     * @param context
     * @param uri
     * @param outputX
     * @param outputY
     * @param requestCode
     */
    public static void cropImageUri(Activity context,Uri uri, int outputX, int outputY, int requestCode){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 5);
        intent.putExtra("aspectY", 3);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 复制图片到目标路径，两个都是文件路径
     * @param filePath
     * @param destPath
     * @return
     */
    public static boolean copyFile(String filePath, String destPath) {
        File originFile = new File(filePath);
        File destFile = new File(destPath);
        BufferedInputStream reader = null;
        BufferedOutputStream writer = null;
        try {
            if (!destFile.exists()) {
                boolean result = destFile.createNewFile();
                Log.d("jack", "create new file result: " + result + " file : " + destPath);
            }
            InputStream in = new FileInputStream(originFile);
            OutputStream out = new FileOutputStream(destFile);
            reader = new BufferedInputStream(in);
            writer = new BufferedOutputStream(out);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, length);
            }
        } catch (Exception exception) {
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignore) {
                }
            }
        }
        return true;
    }

}
