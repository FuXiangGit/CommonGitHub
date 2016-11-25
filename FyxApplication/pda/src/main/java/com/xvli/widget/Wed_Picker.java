package com.xvli.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.xvli.pda.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 12:06.
// */

public class Wed_Picker extends LinearLayout
{
    /** 滑动控件 */
    private ScrollerNumberPicker picker;
    /** 选择监听 */
    private OnSelectingListener onSelectingListener;
    /** 刷新界面 */
    private static final int REFRESH_VIEW = 0x001;
    /** 临时日期 */
    private int tempProvinceIndex = -1;
    private int temCityIndex = -1;
    private int tempCounyIndex = -1;
    private Context context;

    private ArrayList<String> loc_list;
//    private List<String> loc_list;
    public Wed_Picker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getdefultInfo();
        // TODO Auto-generated constructor stub
    }

    public Wed_Picker(Context context) {
        super(context);
        this.context = context;
        getdefultInfo();
        // TODO Auto-generated constructor stub
    }


    //初始化数据
    public void getdefultInfo()
    {
          loc_list=new ArrayList<String>();
//          loc_list= (ArrayList)Arrays.asList(getResources().getStringArray(R.array.atm_match));
          String[] loc=getResources().getStringArray(R.array.atm_match);
        for(int i=0;i<loc.length;i++)
        {
            loc_list.add(loc[i]);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.picker_wedgeloc, this);
        // 获取控件引用
        picker = (ScrollerNumberPicker) findViewById(R.id.picker_wedgeloc);

        picker.setData(loc_list);
        picker.setDefault(0);

        picker.setOnSelectListener(new ScrollerNumberPicker.OnSelectListener() {

            @Override
            public void endSelect(int id, String text) {
                // TODO Auto-generated method stub

                if (text.equals("") || text == null)
                    return;
                if (tempCounyIndex != id) {
                    String selectDay = picker.getSelectedText();
                    if (selectDay == null || selectDay.equals(""))
                        return;
                    String selectMonth = picker.getSelectedText();
                    if (selectMonth == null || selectMonth.equals(""))
                        return;
                    // 城市数组
//                                            city_code_string = citycodeUtil.getCouny_list_code().get(id);
                    int lastDay = Integer.valueOf(picker.getListSize());
                    if (id > lastDay) {
                        picker.setDefault(lastDay - 1);
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
        return picker.getSelectedText();
    }

}

