package com.xvli.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.xvli.widget.ScrollerNumberPicker.OnSelectListener;

import com.xvli.pda.R;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class WedgrTime_Picker extends LinearLayout
{

    /** 滑动控件 */
    private ScrollerNumberPicker picker_hour;
    private ScrollerNumberPicker picker_min;
    /** 选择监听 */
    private OnSelectingListener onSelectingListener;
    /** 刷新界面 */
    private static final int REFRESH_VIEW = 0x001;
    /** 临时日期 */
    private int tempProvinceIndex = -1;
    private int temCityIndex = -1;
    private int tempCounyIndex = -1;
    private Context context;
    
    private ArrayList<String> hour_list;
    private ArrayList<String> min_list;
    public WedgrTime_Picker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getdefultInfo();
        // TODO Auto-generated constructor stub
    }

    public WedgrTime_Picker(Context context) {
            super(context);
            this.context = context;
            getdefultInfo();
            // TODO Auto-generated constructor stub
    }
    
    
    //初始化数据
    public void getdefultInfo()
    {
        hour_list=new ArrayList<String>();
        min_list=new ArrayList<String>();
        for(int i=0;i<24;i++)
        {
        	DecimalFormat df=new DecimalFormat("00");
            String str2=df.format((i));
            hour_list.add(str2);
        }
        
        for(int i=0;i<60;i++)
        {
        	DecimalFormat df=new DecimalFormat("00");
            String str2=df.format((i));
            min_list.add(str2);
        }
    }
    
    @Override
    protected void onFinishInflate() {
              super.onFinishInflate();
              LayoutInflater.from(getContext()).inflate(R.layout.picker_wedgetime, this);
              // 获取控件引用
              picker_hour= (ScrollerNumberPicker) findViewById(R.id.picker_wedgehour);
              picker_min= (ScrollerNumberPicker) findViewById(R.id.picker_wedgemin);
              
              picker_hour.setData(hour_list);
              picker_hour.setDefault(0);
              
              picker_min.setData(min_list);
              picker_min.setDefault(0);
              
              picker_hour.setOnSelectListener(new OnSelectListener() {

                        @Override
                        public void endSelect(int id, String text) {
                                  // TODO Auto-generated method stub

                                  if (text.equals("") || text == null)
                                            return;
                                  if (tempCounyIndex != id) {
                                            String selectDay = picker_hour.getSelectedText();
                                            if (selectDay == null || selectDay.equals(""))
                                                      return;
                                            String selectMonth = picker_hour.getSelectedText();
                                            if (selectMonth == null || selectMonth.equals(""))
                                                      return;
                                            // 城市数组
//                                            city_code_string = citycodeUtil.getCouny_list_code().get(id);
                                            int lastDay = Integer.valueOf(picker_hour.getListSize());
                                            if (id > lastDay) {
                                                picker_hour.setDefault(lastDay - 1);
                                            }
                                  }
                                  tempCounyIndex = id;
                                  Message message = new Message();
                                  message.what = REFRESH_VIEW;
                                  handler.sendMessage(message);
                        }

                        @Override
                        public void selecting(int id, String text) {
                                  // TODO Auto-generated method stub

                        }
              });
              
              picker_min.setOnSelectListener(new OnSelectListener() {

                  @Override
                  public void endSelect(int id, String text) {
                            // TODO Auto-generated method stub

                            if (text.equals("") || text == null)
                                      return;
                            if (tempCounyIndex != id) {
                                      String selectDay = picker_hour.getSelectedText();
                                      if (selectDay == null || selectDay.equals(""))
                                                return;
                                      String selectMonth = picker_hour.getSelectedText();
                                      if (selectMonth == null || selectMonth.equals(""))
                                                return;
                                      // 城市数组
//                                      int lastDay = Integer.valueOf(picker_hour.getListSize());
//                                      if (id > lastDay) {
//                                          picker_hour.setDefault(lastDay - 1);
//                                      }
                            }
                            tempCounyIndex = id;
                            Message message = new Message();
                            message.what = REFRESH_VIEW;
                            handler.sendMessage(message);
                  }

                  @Override
                  public void selecting(int id, String text) {
                            // TODO Auto-generated method stub

                  }
        });
    }
    
    Handler handler = new Handler() {

              @Override
              public void handleMessage(Message msg) {
                        // TODO Auto-generated method stub
                        super.handleMessage(msg);
                        switch (msg.what) {
                        case REFRESH_VIEW:
                                  if (onSelectingListener != null)
                                            onSelectingListener.selected(true);
                                  break;
                        default:
                                  break;
                        }
              }

    };
    
    public void setOnSelectingListener(OnSelectingListener onSelectingListener) {
        this.onSelectingListener = onSelectingListener;
    }

    public interface OnSelectingListener {
    
            public void selected(boolean selected);
    }
    /**
     * 返回获取数据
     * @return
     */
    public String getresult()
    {
        return picker_hour.getSelectedText()+":"+picker_min.getSelectedText();
    }
}
