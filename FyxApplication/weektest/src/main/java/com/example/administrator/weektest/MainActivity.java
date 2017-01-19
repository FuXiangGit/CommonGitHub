package com.example.administrator.weektest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button buttonLeft,buttonRight;
    private TextView textView,first_day_of_week,last_day_of_week;
    private int operateWeek;
    private int operateYear;
    private int operateMonth;
    private Boolean showWeekOrMonth = false;//true周，false是月

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        buttonLeft  = (Button) findViewById(R.id.left_btn);
        buttonRight  = (Button) findViewById(R.id.right_btn);
        buttonLeft.setOnClickListener(this);
        buttonRight.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.show_title);
        first_day_of_week = (TextView) findViewById(R.id.first_day_of_week);
        last_day_of_week = (TextView) findViewById(R.id.last_day_of_week);
    }

    private void initData() {//初始状态
        operateWeek = DateUtil.getWeekOfYear(new Date());//第几周
        operateYear = DateUtil.getYear();//当前系统所在的年份
        operateMonth = DateUtil.getMonthOfTaday();//获取当前的月份，已经自动加一处理
        if(showWeekOrMonth) {
            first_day_of_week.setText(DateUtil.dateToStrLong(DateUtil.getFirstDayOfWeek(operateYear, operateWeek)));
            textView.setText("第" + operateWeek + "周" + DateUtil.getMaxWeekNumOfYear(operateYear));
            last_day_of_week.setText(DateUtil.dateToStrLong(DateUtil.getLastDayOfWeek(operateYear, operateWeek)));
        }else{
            first_day_of_week.setText(DateUtil.dateToStrLong(DateUtil.getFirstDayOfMonth(operateYear, operateMonth)));
            textView.setText("第" + operateMonth + "月");
            last_day_of_week.setText(DateUtil.dateToStrLong(DateUtil.getLastDayOfMonth(operateYear, operateMonth)));
        }
    }

    /**
     * 计算并且显示新的状态
     * @param addOrMin
     */
    private void resetData(Boolean addOrMin){
        if(addOrMin){//点击了向右按钮
            if(showWeekOrMonth) {//这个是周的右翻页
                if (operateWeek == DateUtil.getMaxWeekNumOfYear(operateYear)) {//如果当前周是某年的最大周
                    operateWeek = 1;//新一年第一周
                    operateYear = operateYear + 1;//年要加了
                } else {
                    operateWeek = operateWeek + 1;
                }
            }else{//月份的右翻页
                if(operateMonth==12){
                    operateMonth = 1;
                    operateYear = operateYear + 1;//年要加了
                }else{
                    operateMonth = operateMonth+1;
                }
            }
        }else{//点击了向左按钮
            if(showWeekOrMonth) {//周的左翻页
                if (operateWeek == 1) {//如果已经是第一周了，那就要返回上一年了
                    operateYear = operateYear - 1;
                    operateWeek = DateUtil.getMaxWeekNumOfYear(operateYear);//获取上一年的最大周
                } else {
                    operateWeek = operateWeek - 1;
                }
            }else{//月份的左翻页
                if(operateMonth==1){
                    operateMonth = 12;
                    operateYear = operateYear - 1;//年要加了
                }else{
                    operateMonth = operateMonth-1;
                }
            }
        }

        if(showWeekOrMonth) {
            first_day_of_week.setText(DateUtil.dateToStrLong(DateUtil.getFirstDayOfWeek(operateYear, operateWeek)));
            textView.setText("第" + operateWeek + "周");
            last_day_of_week.setText(DateUtil.dateToStrLong(DateUtil.getLastDayOfWeek(operateYear, operateWeek)));
        }else{
            first_day_of_week.setText(DateUtil.dateToStrLong(DateUtil.getFirstDayOfMonth(operateYear, operateMonth)));
            textView.setText("第" + operateMonth + "月");
            last_day_of_week.setText(DateUtil.dateToStrLong(DateUtil.getLastDayOfMonth(operateYear, operateMonth)));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.left_btn:
                //这里添加线程“成功”后调用下面的方法
                resetData(false);

                break;
            case R.id.right_btn:
                //这里添加线程“成功”后调用下面的方法
                resetData(true);
                break;
        }
    }


}
