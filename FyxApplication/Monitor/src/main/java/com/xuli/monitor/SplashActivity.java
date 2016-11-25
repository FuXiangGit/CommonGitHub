package com.xuli.monitor;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xuli.dao.TruckChildDao;
import com.xuli.dao.TruckDao;
import com.xuli.dao.TruckGroupDao;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {

    private TextView tv_skip;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        mHandler.post(timeRunnable);
        tv_skip = (TextView) findViewById(R.id.tv_skip);

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeRunnable !=null){
                    mHandler.removeCallbacks(timeRunnable);
                }
                startActivity(new Intent(v.getContext(), LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }
    private Handler mHandler = new Handler();

    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            tv_skip.setText(getResources().getString(R.string.tv_skip) + count);
            if (count == 1) {
                if(timeRunnable !=null){
                    mHandler.removeCallbacks(timeRunnable);
                }
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }else {
                count--;
                mHandler.postDelayed(timeRunnable, 1000);
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timeRunnable !=null){
            mHandler.removeCallbacks(timeRunnable);
        }
    }
}
