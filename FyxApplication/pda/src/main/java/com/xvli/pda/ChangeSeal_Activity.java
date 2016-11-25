package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andoird.mytools.ui.adapterview.DataHolder;
import com.andoird.mytools.ui.adapterview.GenericAdapter;
import com.andoird.mytools.ui.adapterview.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.MyAtmError;
import com.xvli.bean.TaiRepairSealVo;
import com.xvli.bean.ThingsVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.MyErrorDao;
import com.xvli.dao.TaiRepairDao;
import com.xvli.dao.ThingsDao;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.widget.WedgeLoc_Picker;
import com.xvli.widget.WedgrTime_Picker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 泰国维修任务 更换钞箱
 */
public class ChangeSeal_Activity extends BaseActivity implements OnClickListener {
    private Button btn_back;
    private TextView tv_title, btn_ok, chick_dyc;
    private ListView list_code;
    //扫描记录
    private String scanResult = "";
    private long scanTime = -1;
    private TimeCount time;//扫描倒計時

    private LoginDao login_dao;
    private MyErrorDao error_dao;
    private String clientid;
    private UniqueAtmVo atm_bean;
    private boolean isShow;
    private ThingsDao things_dao;
    private TaiRepairDao repair_dao;
    private List<TaiRepairSealVo>  sealVoList;
    private EditText tv_code1,tv_code2;
    private boolean  isScanCode =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_show);

        Action action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        atm_bean = (UniqueAtmVo) action.getCommObj();

        login_dao = new LoginDao(getHelper());
        error_dao = new MyErrorDao(getHelper());
        things_dao = new ThingsDao(getHelper());
        repair_dao = new TaiRepairDao(getHelper());

        List<LoginVo> users = login_dao.queryAll();
        clientid = users.get(users.size() - 1).getClientid();
        time = new TimeCount(500, 1);//构造CountDownTimer对象

        /*//测试代码
        for (int i= 0; i< 10 ;i++){
            ThingsVo thingsVo = new ThingsVo();
            thingsVo.setId("17100000005" + i);
            thingsVo.setType("80");
            thingsVo.setFlg(171);
            thingsVo.setClientid(clientid);
            thingsVo.setBarcode("17100000005" + i);

            if(things_dao.contentsNumber(thingsVo) > 0){
            } else {
                things_dao.create(thingsVo);
            }
        }*/


        initeview();
    }

    public void initeview() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.tv_change_seal_1));
        chick_dyc = (TextView) findViewById(R.id.chick_dyc);

        chick_dyc = (TextView) findViewById(R.id.chick_dyc);
        list_code = (ListView) findViewById(R.id.lv_show_atm);

        chick_dyc.setText(getResources().getString(R.string.tv_change_tip));
        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        initListView();
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == btn_back) {
            finish();
        } else if (arg0 == btn_ok) {
            CustomDialog dialog = new CustomDialog(this,getResources().getString(R.string.confirm_finish_ok)+getResources().getString(R.string.tv_change_seal)+"?");
            dialog.showConfirmDialog();
        }
    }

    //是否已经存在
    public boolean isexist(String code) {
        Map<String, Object> isexist = new HashMap<String, Object>();
        isexist.put("code", code);
        List<MyAtmError> errors = error_dao.quaryForDetail(isexist);
        if (errors != null && errors.size() > 0) {
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.toast_is_exist));
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置列表
     */
    public void initListView() {

        HashMap<String,Object> value = new HashMap<>();
        value.put("atmid",atm_bean.getAtmid());
       sealVoList = repair_dao.quaryForDetail(value);
        if(sealVoList != null && sealVoList.size() > 0){

            ShowCodeAdapter codeAdapter = new ShowCodeAdapter(this);
            list_code.setAdapter(codeAdapter);
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
            if ((System.currentTimeMillis() - scanTime) > 500) {
                time.start();
                scanResult = "" + event.getCharacters();
                scanTime = System.currentTimeMillis();
            } else {
                scanResult = scanResult + (event.getKeyCode() - 7);
            }
        }
        return super.dispatchKeyEvent(event);
    }


    public void setData() {

    }

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {

            if(isScanCode){
                if(Regex.isTaiCashbog(scanResult) || Regex.isRepairBog(scanResult)){
                    if(tv_code1.isFocusable()) {
                        if (!TextUtils.isEmpty(tv_code1.getText())) {
                            tv_code1.setText("");
                            tv_code1.setText(scanResult);
                        } else {
                            tv_code1.setText(scanResult);
                        }
                    }else if(tv_code2.isFocusable()){
                        if(!TextUtils.isEmpty(tv_code1.getText())){

                            tv_code2.setText("");
                            tv_code2.setText(scanResult);
                        } else {
                            tv_code2.setText(scanResult);
                        }
                    }else{
                        tv_code1.setText(scanResult);
                    }
                }else{
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.tv_change_tip));
                }
            }else {
                ScanResult(scanResult);
            }

        }



        @Override
        public void onTick(long millisUntilFinished) {
            //计时过程显示

        }
    }
    private void ScanResult( String scanResult) {
        Map<String, Object> where_error = new HashMap<String, Object>();
        where_error.put("code", scanResult);
        List<MyAtmError> errors = error_dao.quaryForDetail(where_error);
        if (errors != null && errors.size() > 0)
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
        else if (Regex.isTaiCashbog(scanResult) || Regex.isRepairBog(scanResult)) {
            if (!isShow) {
                showChangeDialog(scanResult);//计时完毕时触发
                tv_code1.setText(scanResult);
                isShow = true;
            }

        } else {
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
        }

    }
    private void showChangeDialog(final String codeResult) {

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_repair, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_code_tip = (TextView) view.findViewById(R.id.tv_code_tip);
        tv_code_tip.setText(getResources().getString(R.string.tv_change_tip));
        tv_code1 = (EditText) view.findViewById(R.id.et_new_code);
        tv_code2 = (EditText) view.findViewById(R.id.et_older_code);
        isScanCode = true;
        tv_code1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tv_code1.setFocusable(true);
                tv_code1.setFocusableInTouchMode(true);
                tv_code1.requestFocus();
                return false;
            }
        });

        tv_code2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tv_code2.setFocusable(true);
                tv_code2.setFocusableInTouchMode(true);
                tv_code2.requestFocus();
                return false;
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
                    if ((System.currentTimeMillis() - scanTime) > 500) {
                        time.start();
                        scanResult = "" + event.getCharacters();
                        scanTime = System.currentTimeMillis();
                    } else {
                        scanResult = codeResult + event.getCharacters();
                    }
/*
                    if (Regex.isTaiCashbog(scanResult) || Regex.isRepairBog(scanResult)) {
                        tv_code2.setText(scanResult);

                    } else
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));*/
                }

                return false;
            }
        });

        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String result1 = tv_code1.getText().toString();
                String result2 = tv_code2.getText().toString();

                if (TextUtils.isEmpty(result1) || TextUtils.isEmpty(result2)) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_isEmpty));
                } else {
                    if(Regex.isTaiCashbox(result1) || Regex.isRepairBog(result1) ||Regex.isTaiCashbox(result2) || Regex.isRepairBog(result2) ){
                        setDatatoDb(result1, result2, dialog);
                        isScanCode = false;
                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                    }
                }
                isShow = false;
            }

        });
        bt_miss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isShow = false;
                isScanCode = false;
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();

    }

    //codeResult第一次扫描到的值    scanResult 第二次扫描到的值
    private void setDatatoDb(String codeResult, String scanResult, Dialog dialog) {


        // 扫描条码是否已经存在
        if(isCodeExist(codeResult) | isOldCodeExist(codeResult) ||isCodeExist(scanResult) | isOldCodeExist(scanResult )){
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_exist_ok));
        } else {
            if (codeResult.equals(scanResult)) {//两次数据是否相同
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_change_tip));
            } else {
                // 第一个   或者第二个 是否是 新条码即 接口取到的数据
                PDALogger.d("-111--->" + isNewCode(codeResult));
                PDALogger.d("-222-->" + isNewCode(scanResult));
                if (isNewCode(codeResult)) {
                    setDataResult(codeResult, scanResult, 1);
                    dialog.dismiss();
                } else if (isNewCode(scanResult)) {
                    setDataResult(codeResult, scanResult, 2);
                    dialog.dismiss();
                } else {
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.tv_change_tip));
                }

            }
        }
    }

    private void setDataResult(String oneCode, String twoCode,int witch) {

        TaiRepairSealVo repairSealVo = new TaiRepairSealVo();
        repairSealVo.setClientid(clientid);
        repairSealVo.setAtmid(atm_bean.getAtmid());
        repairSealVo.setAtmcustomerid(atm_bean.getCustomerid());
        repairSealVo.setAtmno(atm_bean.getAtmno());
        if(witch == 1){
            repairSealVo.setNewbarcode(oneCode);
            repairSealVo.setOldbarcode(twoCode);
        } else {
            repairSealVo.setNewbarcode(twoCode);
            repairSealVo.setOldbarcode(oneCode);
        }

        if(repair_dao.contentsNumber(repairSealVo) > 0){
        } else {
            repair_dao.create(repairSealVo);
        }
        initListView();
    }

    // 第一个   或者第二个 是否是 新条码  接口取到的数据
    public boolean isNewCode(String codeResult) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("barcode", codeResult);
        hashMap.put("flg", 171);
        List<ThingsVo> thingsVoList = things_dao.quaryForDetail(hashMap);
        if (thingsVoList != null && thingsVoList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    // 数据是否已经存在数据库
    private boolean isCodeExist(String result) {
        //新的编码
        HashMap<String,Object> value = new HashMap<>();
        value.put("newbarcode",result);
        List<TaiRepairSealVo> repairSealVoList = repair_dao.quaryForDetail(value);
        if(repairSealVoList != null && repairSealVoList.size() >0){
            return true;
        } else {
            return false;
        }
    }
    // 数据是否已经存在数据库
    private boolean isOldCodeExist(String result) {
        HashMap<String,Object> value = new HashMap<>();
        value.put("oldbarcode",result);
        List<TaiRepairSealVo> repairSealVoList = repair_dao.quaryForDetail(value);
        if(repairSealVoList != null && repairSealVoList.size() >0){
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (time != null) {
            time.cancel();
            time = null;
        }

    }
    public class ShowCodeAdapter extends BaseAdapter {

        private Context mContext;
        private AtmVoDao unique_dao;

        public ShowCodeAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return sealVoList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_barcode_scan, null);
                holder.tv_item_1 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tv_item_2 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tv_item_3 = (TextView) convertView.findViewById(R.id.tv_item_3);
                holder.tv_item_2.setVisibility(View.GONE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_item_1.setText(getResources().getString(R.string.tv_change_new) + sealVoList.get(position).getNewbarcode());
            holder.tv_item_3.setText(getResources().getString(R.string.tv_change_older) + sealVoList.get(position).getOldbarcode());

            return convertView;
        }

        public class ViewHolder {
            TextView tv_item_1;
            TextView tv_item_2;
            TextView tv_item_3;
        }

    }
}
