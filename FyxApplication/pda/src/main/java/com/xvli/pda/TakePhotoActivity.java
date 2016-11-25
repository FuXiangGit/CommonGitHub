package com.xvli.pda;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.andoird.mytools.imageloader.ExtraSourceImageDownloader;
import com.andoird.mytools.memory.MemoryManager;
import com.andoird.mytools.util.LogManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xvli.bean.BankCardVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.utils.PDALogger;


@SuppressLint({ "SdCardPath", "SimpleDateFormat" })
public class TakePhotoActivity extends BaseActivity implements OnClickListener
{
    /**
     * 拍照保存路径
     */
    private static final int    CASE_UPLOAD_IMAGE              = 1;

    private ImageView           imgPreview;
    private TextView            txtCancel;
    private TextView            txtUpload;

    private long                lastTime                       = 0;

    /**
     * 拍照的图片
     */
    private File                imageFile;


    private Bitmap              bitmap;

    private BankCardVo bean;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        takeImage();
        setContentView(R.layout.take_photo);
        
        Action action=(Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        bean=(BankCardVo) action.getCommObj();
        
        initViews();
        
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .memoryCacheExtraOptions(250, 250)
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .discCacheFileNameGenerator(new Md5FileNameGenerator())
        .discCacheSize(10 * 1024 * 1024).memoryCacheSizePercentage((int) 12.5)
        .imageDownloader(new ExtraSourceImageDownloader(this))
        .discCacheFileCount(10)
        .build();
        ImageLoader.getInstance().init(config);
        
        DisplayImageOptions  options = new DisplayImageOptions.Builder()  
        //.showImageForEmptyUri(R.drawable.ic_launcher) // image连接地址为空时  
        .cacheInMemory(false) // 加载图片时不会在内存中加载缓存  
        .cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存  
        .displayer(new RoundedBitmapDisplayer(20)) // 设置用户加载图片task(这里是圆角图片显示)  
        .build();
    }

    /**
     * 初始化界面
     */
    private void initViews()
    {
        imgPreview = (ImageView) findViewById(R.id.img_photo_preview);
        txtCancel = (TextView) findViewById(R.id.txt_photo_cancel);
        txtUpload = (TextView) findViewById(R.id.txt_photo_upload);

        txtCancel.setOnClickListener(this);
        txtUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.txt_photo_cancel:
                finish();
                break;

            case R.id.txt_photo_upload:
                if (System.currentTimeMillis() - lastTime > 1000)
                {
                    lastTime = System.currentTimeMillis();
                    if (imageFile != null)
                    {
                        String path = imageFile.getPath();
                        PDALogger.d("吞卡照片path------->"+path);
                        Action action = new Action();
                        bean.setPhoto(path);
                        action.setCommObj(bean);
                        Intent  resultintent=new Intent();
                        Bundle extras=new Bundle();
                        extras.putSerializable("store", action);
                        resultintent.putExtras(extras);
                        setResult(Activity.RESULT_OK, resultintent);
                        finish();

                    }
                }
                break;

            default:
                break;
        }
    }
    /**
     * 拍照
     */
    private void takeImage()
    {
        imageFile = doTakePicture(TakePhotoActivity.this, CASE_UPLOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        LogManager.logE(getClass(), requestCode+"onActivityResult"+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == CASE_UPLOAD_IMAGE)
            {
                setImage();
            }
        } else
        {
            finish();
        }
    }

    private void setImage()
    {
        File instanceFile=new File(imageFile.getPath());
        if (imageFile != null)
        {
//            BitmapFactory.Options justBoundsOptions = MemoryManager.createJustDecodeBoundsOptions();
//            BitmapFactory.decodeFile(imageFile.getPath(), justBoundsOptions);
//            bitmap = BitmapFactory.decodeFile(imageFile.getPath(), MemoryManager.createSampleSizeOptions(justBoundsOptions, 500, 500));
//            ByteArrayOutputStream outCache = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outCache);
//            imgPreview.setImageBitmap(bitmap);
            if(instanceFile.exists())
            {
              BitmapFactory.Options justBoundsOptions = MemoryManager.createJustDecodeBoundsOptions();
              BitmapFactory.decodeFile(imageFile.getPath(), justBoundsOptions);
              bitmap = BitmapFactory.decodeFile(imageFile.getPath(), MemoryManager.createSampleSizeOptions(justBoundsOptions, 500, 500));
              ByteArrayOutputStream outCache = new ByteArrayOutputStream();
              bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outCache);
              imgPreview.setImageBitmap(bitmap);
                
//                ImageLoader.getInstance().displayImage("file://"+imageFile.getPath(), imgPreview);
            }
//            else
//                LogManager.logE(getClass(), "拍照的文件不存在，读取不出来"+imageFile.getPath());
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (event.getKeyCode())
            {
                case KeyEvent.KEYCODE_BACK:
                    takeImage();
                    break;
            } 
        }

        return super.dispatchKeyEvent(event);
    }

    /**
     * 调用拍照功能并保存到sdcard/DCIM/Camera/目录下
     * 
     * @return File 照片文件
     */
    public static File doTakePicture(Activity activity, int requestCode)
    {
        String temp = Config.tempDir;
        File imagePath = new File(temp);
        if (!imagePath.exists())
        {
            imagePath.mkdirs();
        }
        File imageFile = new File(temp, getSystemTime("yyyyMMdd_HHmmss") + ".jpg");

        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        imageIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        imageIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 90);
        activity.startActivityForResult(imageIntent, requestCode);
        return imageFile;
    }

    /**
     * 获取当前时间
     * 
     * @return String
     */
    public static String getSystemTime(String strFormat)
    {
        SimpleDateFormat tempDate = new SimpleDateFormat(strFormat);
        String datetime = tempDate.format(new java.util.Date());
        return datetime.toString();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy()
    {
        LogManager.logE(getClass(), "onDestroy");
        super.onDestroy();
        if (bitmap != null)
            bitmap.recycle();
    }

}
