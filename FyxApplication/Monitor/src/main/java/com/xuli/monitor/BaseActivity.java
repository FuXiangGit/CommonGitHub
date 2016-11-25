package com.xuli.monitor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xuli.Util.ActivityManager;
import com.xuli.database.DatabaseHelper;

import org.xutils.x;

public class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_ACTION     = "android.intent.extra.ACTION";
    DatabaseHelper databaseHelper = null;
    private ActivityManager activityManager ;
    /**
     * 整个Activity视图的根视图
     */
    View decorView;
    /**
     * 手指按下时的坐标
     */
    float downX, downY;
    /**
     * 手机屏幕的宽度和高度
     */
    float screenWidth, screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        x.view().inject(this);
        //获取到decorView
        decorView = getWindow().getDecorView();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        ActivityManager.getActivityManager().pushActivity(this);
    }
    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 通过重写该方法，对触摸事件进行处理
     */

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.i("event.getAction() = ", event.getAction() + "");
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:// 当按下时
//                // 获得按下时的X坐标
//                downX = event.getX();
//                break;
//            case MotionEvent.ACTION_MOVE:// 当手指滑动时
//                // 获得滑过的距离
//                float moveDistanceX = event.getX() - downX;
//                if (moveDistanceX > 0) {// 如果是向右滑动
//                    decorView.setX(moveDistanceX); // 设置界面的X到滑动到的位置
//                }
//                break;
//            case MotionEvent.ACTION_UP:// 当抬起手指时
//                // 获得滑过的距离
//                float moveDistanceX1 = event.getX() - downX;
//                if (moveDistanceX1 > screenWidth / 5) {
//                    // 如果滑动的距离超过了手机屏幕的1/5, 结束当前Activity
////                    finish();
//                    continueMove(moveDistanceX1,200);
//                } else { // 如果滑动距离没有超过一半
//                    // 恢复初始状态
////                    decorView.setX(0);
//                    rebackToLeft(moveDistanceX1);
//                }
//                break;
//        }
//
//        return super.onTouchEvent(event);
//    }

    /**
     * 从当前位置一直往右滑动到消失。
     * 这里使用了属性动画。
     */
    public void continueMove(float moveDistanceX ,long duration){
        // 从当前位置移动到右侧。
        ValueAnimator anim = ValueAnimator.ofFloat(moveDistanceX, screenWidth);
        anim.setDuration(duration); // 一秒的时间结束, 为了简单这里固定为1秒
        anim.start();

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 位移
                float x = (float) (animation.getAnimatedValue());
                decorView.setX(x);
            }
        });

        // 动画结束时结束当前Activity
        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0,0);
            }

        });
    }

    /**
     * Activity被滑动到中途时，滑回去~
     */
    public void rebackToLeft(float moveDistanceX){
        ObjectAnimator.ofFloat(decorView, "X", moveDistanceX, 0).setDuration(200).start();
    }




    //右滑返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            continueMove(0,300);
        }
        return false;
    }


}
