package com.fyx.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fyx.andr.R;
import com.fyx.bean.ListTypeOperate;

import java.util.ArrayList;
import java.util.List;

public class ListViewMutiplType extends AppCompatActivity {

    List<ListTypeOperate> lists = new ArrayList<ListTypeOperate>();
    ListView mutipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_mutipl_type);
        initData();
        initListView();
    }

    private void initListView() {
        mutipList = (ListView) findViewById(R.id.mutip_list);
        MutiplyAdapter adapter = new MutiplyAdapter(this);
        mutipList.setAdapter(adapter);
    }

    private void initData() {
        ListTypeOperate listTypeOperate;
        for (int i = 0; i < 15; i++) {
            listTypeOperate = new ListTypeOperate("位置" + i, "内容", "日期", "Y", "拍照", 0);
            lists.add(listTypeOperate);
        }
        for (int i = 0; i < 15; i++) {
            listTypeOperate = new ListTypeOperate("位置" + i, "内容", "日期", "Y", "拍照", 1);
            lists.add(listTypeOperate);
        }
        for (int i = 0; i < 5; i++) {
            listTypeOperate = new ListTypeOperate("位置" + i, "内容", "日期", "Y", "拍照", i);
            lists.add(listTypeOperate);
        }

    }

    private class MutiplyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mcontext;
        public static final int TYPE_1 = 0;
        public static final int TYPE_2 = 1;
        public static final int TYPE_3 = 2;
        public static final int TYPE_4 = 3;
        public static final int TYPE_5 = 4;
        private int index = -1;


        public MutiplyAdapter(Context context) {
            super();
            this.mcontext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            Log.d("jack","getCount");
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            Log.d("jack","getItem");
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            Log.d("jack","getItemId");
            return position;
        }

        @Override
        public int getViewTypeCount() {//有几种类型就写返回几
            Log.d("jack","getViewTypeCount");
            return 5;
        }

        @Override
        public int getItemViewType(int position) {
            Log.d("jack","getItemViewType");
            if (lists != null && position < lists.size()) {//这里要注意，一定要保证这个类型和上面的TYPE1234后面的值对应，比如这里都是从0-4
                return lists.get(position).getType();//返回当前的lists里面对应的position位置的类型
            } else {
                return super.getItemViewType(position);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder1 holder1 = null;
            ViewHolder2 holder2 = null;
            ViewHolder3 holder3 = null;
            ViewHolder4 holder4 = null;
            ViewHolder5 holder5 = null;
            int type = getItemViewType(position);
Log.d("jack",type+"");
            switch (type) {
                case TYPE_1:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.activity_check_a_style, parent, false);
                        holder1 = new ViewHolder1();
                        holder1.left_text = (TextView) convertView.findViewById(R.id.left_text1);
                        holder1.right_cbx_routing = (CheckBox) convertView.findViewById(R.id.right_cbx_routing1);
                        convertView.setTag(holder1);
                    } else {
                        holder1 = (ViewHolder1) convertView.getTag();
                    }
                    break;
                case TYPE_2:
//                    if (convertView == null) {//这里重点注释，如果包含了EditText那么这里就不能使用holder了，否则会出现弹出键盘问题还有内容混乱问题
                        convertView = mInflater.inflate(R.layout.activity_check_b_style, parent, false);
                        holder2 = new ViewHolder2();
                        holder2.left_text = (TextView) convertView.findViewById(R.id.left_text2);
                        holder2.right_cbx_routing = (CheckBox) convertView.findViewById(R.id.right_cbx_routing2);
                        holder2.et_other = (EditText) convertView.findViewById(R.id.et_other2);
                        convertView.setTag(holder2);
                        holder2.et_other.setTag(position);
//                    } else {
//                        holder2 = (ViewHolder2) convertView.getTag();
//                    }
                    break;
                case TYPE_3:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.activity_check_c_style, parent, false);
                        holder3 = new ViewHolder3();
                        holder3.left_text = (TextView) convertView.findViewById(R.id.left_text3);
                        holder3.et_other = (EditText) convertView.findViewById(R.id.et_other3);
                        convertView.setTag(holder3);
                    } else {
                        holder3 = (ViewHolder3) convertView.getTag();
                    }
                    break;
                case TYPE_4:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.activity_check_d_style, parent, false);
                        holder4 = new ViewHolder4();
                        holder4.left_text = (TextView) convertView.findViewById(R.id.left_text4);
                        holder4.tv_other = (TextView) convertView.findViewById(R.id.tv_other4);
                        convertView.setTag(holder4);
                    } else {
                        holder4 = (ViewHolder4) convertView.getTag();
                    }
                    break;
                case TYPE_5:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.activity_check_e_style, parent, false);
                        holder5 = new ViewHolder5();
                        holder5.left_text = (TextView) convertView.findViewById(R.id.left_text5);
                        holder5.tv_other = (TextView) convertView.findViewById(R.id.tv_other5);
                        convertView.setTag(holder5);
                    } else {
                        holder5 = (ViewHolder5) convertView.getTag();
                    }
                    break;
            }
            //这里是赋值和操作
            switch (type) {
                case TYPE_1:
                    holder1.right_cbx_routing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//（前）
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                lists.get(position).setCheckBtn("Y");
                            } else {
                                lists.get(position).setCheckBtn("N");
                            }
                        }
                    });
                    if (lists.get(position).getCheckBtn().equals("Y")) {//（后）这块儿代码要放在setOnCheckedChangeListener后面，否则会导致checkbox上下滑动后恢复到默认状态
                        holder1.right_cbx_routing.setChecked(true);
                    } else {
                        holder1.right_cbx_routing.setChecked(false);
                    }
                    if (TextUtils.isEmpty(lists.get(position).getHead())) {
                        holder1.left_text.setText("");
                    } else {
                        holder1.left_text.setText(lists.get(position).getHead());
                    }

                    break;
                case TYPE_2:
                    if (TextUtils.isEmpty(lists.get(position).getHead())) {
                        holder2.left_text.setText("");
                    } else {
                        holder2.left_text.setText(lists.get(position).getHead());
                    }
                    holder2.right_cbx_routing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//前
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                lists.get(position).setCheckBtn("Y");
                            } else {
                                lists.get(position).setCheckBtn("N");
                            }
                        }
                    });
                    if (lists.get(position).getCheckBtn().equals("Y")) {//后
                        holder2.right_cbx_routing.setChecked(true);
                    } else {
                        holder2.right_cbx_routing.setChecked(false);
                    }
                    //EditText处理
                    holder2.et_other.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
//                                Log.d("jack",v.getTag()+"");
                                index = (Integer) v.getTag();
                            }
                            return false;
                        }
                    });
                    holder2.et_other.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
//						将editText中改变的值保存
                            lists.get(position).setCotent(s.toString());
                        }
                    });
                    if (!TextUtils.isEmpty(lists.get(position).getCheckBtn())) {
                        holder2.et_other.setText(lists.get(position).getCotent());
                    }
                    //下面这里注释掉是避免EditText当前item获取焦点输入完毕划出范围又滑入，重新获取焦点，索性划出后直接就移除焦点调用holder2.et_other.clearFocus();
                    /*if (index == position) {
                        // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                        holder2.et_other.requestFocus();
                    } else {
                        holder2.et_other.clearFocus();
                    }*/
                    break;
                case TYPE_3:
                    break;
                case TYPE_4:
                    break;
                case TYPE_5:
                    break;

            }

            return convertView;
        }

        public class ViewHolder1 {
            TextView left_text;
            CheckBox right_cbx_routing;
        }

        public class ViewHolder2 {
            TextView left_text;
            CheckBox right_cbx_routing;
            EditText et_other;
        }

        public class ViewHolder3 {
            TextView left_text;
            EditText et_other;
        }

        public class ViewHolder4 {
            TextView left_text;
            TextView tv_other;
        }

        public class ViewHolder5 {
            TextView left_text;
            TextView tv_other;
        }

       /* @Override
        public boolean dispatchKeyEventPreIme(KeyEvent event) {
            if (mcontext != null) {
                InputMethodManager imm = (InputMethodManager) mcontext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    //释放焦点
//                    for (int i = 0; i < getChildCount(); i++) {
//                        View view = getChildAt(i);
//                        EditText editText1 = (EditText) view
//                                .findViewById(R.id.editText1);
//                        editText1.clearFocus();
//                    }
                }
            }
//            return super.dispatchKeyEventPreIme(event);
        }*/
    }





}
