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
import com.fyx.utils.ANFileUtils;

import net.bither.util.NativeUtil;

import java.io.File;

public class TakePhotoActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_take_photo;
    private ImageView img_photo_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        initView();
    }

    private void initView() {
        btn_take_photo = (Button) findViewById(R.id.btn_take_photo);
        img_photo_show = (ImageView) findViewById(R.id.img_photo_show);
        btn_take_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_take_photo:
                takePhoto();
                break;
        }
    }
    private static final int TAKE_PICTURE = 0x000001;
    private String fileName = null;
    private void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileName = String.valueOf(System.currentTimeMillis());
        File photoFile = ANFileUtils.createPic(fileName);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TAKE_PICTURE ){
            if(resultCode==RESULT_OK){
                // 获取相机返回的数据，并转换为Bitmap图片格式，这是缩略图
                Bitmap bitmap = null;
                File file=ANFileUtils.getPic(fileName);
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Log.d("jack", "bitmap.length:" + bitmap.getByteCount());
//                img_photo_show.setImageBitmap(bitmap);
                File fileYasuo = ANFileUtils.createPic(fileName+"new");
                NativeUtil.compressBitmap(bitmap,fileYasuo.getAbsolutePath());
                bitmap = BitmapFactory.decodeFile(fileYasuo.getAbsolutePath());
                Log.d("jack", "bitmap.length:" + bitmap.getByteCount());
                img_photo_show.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
