package com.fyx.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fyx.andr.R;
import com.fyx.utils.PhotoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    Button btnGetPhoto;
    ImageView img_photo;
    Uri CropUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        btnGetPhoto = (Button) findViewById(R.id.get_photo);
        img_photo = (ImageView) findViewById(R.id.img_photo);

        initClick();
    }

    private void initClick() {
        btnGetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtils.getPhoto(PhotoActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){//operation succeeded.成功返回
            if(requestCode==PhotoUtils.REQUEST_CODE_PICK_IMAGE){//相册选择
                getBitmapOne(data);
//                getBitmapTwo(data);
            }
            if(requestCode==PhotoUtils.REQUEST_CODE_CROP_IMAGE){//裁剪返回
                Bitmap bitmap = PhotoUtils.decodeUriAsBitmap(PhotoActivity.this,CropUri);
                img_photo.setImageBitmap(bitmap);
            }
        }

    }
    /**
     * 第一种获取bitmap方式
     * @param data
     */
    private void getBitmapOne(Intent data) {
        Bitmap bitmap = null;
        try {
            Uri uri = data.getData();//此时获取的uri为（content://media/external/images/media/11056)模式
            String filePath = PhotoUtils.getFilePathFromContentUri(PhotoActivity.this,uri);//获取文件路径
            File file = new File(filePath);
            if(file==null)return;
            if(file.length()==0){
                file.delete();
                return;
            }
            Log.d("jack","file"+file.getName()+",length"+file.length());
            FileInputStream fileInputStream = new FileInputStream(filePath);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            Log.d("jack","bitmap.length:"+bitmap.getByteCount());
            img_photo.setImageBitmap(bitmap);
            //这里添加裁剪
            CropUri  = uri;
            PhotoUtils.cropImageUri(this,uri,800,600,PhotoUtils.REQUEST_CODE_CROP_IMAGE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取bitmap方式有人说这种方式容易出现OOM
     * @param data
     */
    private void getBitmapTwo(Intent data) {
        Bitmap bitmap = null;
        Uri uri = data.getData();//获得图片的uri
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            Log.d("jack", "bitmap.length:" + bitmap.getByteCount());
            img_photo.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
