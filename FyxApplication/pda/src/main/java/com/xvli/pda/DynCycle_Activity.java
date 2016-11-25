package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.bean.AtmVo;
import com.xvli.bean.DynCycleItemValueVo;
import com.xvli.bean.DynCycleItemVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Config;
import com.xvli.dao.DynCycleDao;
import com.xvli.dao.DynCycleItemValueVoDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//ATM 凭条登记
public class DynCycle_Activity extends BaseActivity implements View.OnClickListener {

    private Button btn_back;
    private ListView mlist ;
    private AtmVo atmVo;
    private DynCycleDao  dynCycleDao;
    private List<DynCycleItemVo> dynCycleItemVos = new ArrayList<>();
    private MyKeyTransferAdpater1 myKeyTransferAdpater;
    private DynCycleItemValueVoDao  dynCycleItemValueVoDao;
    private List<DynCycleItemValueVo> dynCycleItemValueVoList = new ArrayList<>();
    private DynCycleAdpater dynCycleAdpater;
    private UniqueAtmDao uniqueAtmDao;
    private boolean isFalse = false ;
    private List<LoginVo> users;
    private LoginDao login_dao;
    private String  clientid;
    private TextView btn_ok , tv_title;
    private List<HashMap<Integer,String >> hashMaps = new ArrayList<>();
    private EditText editText ;
    private boolean ispager =  false;
    private String [] arrTemp ;

    private EditText balance,cycleNo,amount_deposited,amount_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dyn_cycle);
        atmVo = (AtmVo)getIntent().getSerializableExtra("atm_bean");


        ininView();


    }

    private void ininView(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.ATM_regidtrater));
        btn_ok = (TextView)findViewById(R.id.btn_ok);
        btn_back =(Button) findViewById(R.id.btn_back);
        btn_ok.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        mlist = (ListView)findViewById(R.id.Atm_resgistrater);
        dynCycleDao = new DynCycleDao(getHelper());
        dynCycleItemValueVoDao = new DynCycleItemValueVoDao(getHelper());
        uniqueAtmDao = new UniqueAtmDao(getHelper());

        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            clientid = UtilsManager.getClientid(users);

        }

        initData();
    }


    private  void initData(){
        HashMap<String ,Object> has =new HashMap<>();
        has.put("atmCustomerId" ,atmVo.getAtmcustomerid() );
        has.put("atmid" ,atmVo.getAtmid());
        has.put("branchid" ,atmVo.getBranchid());
        dynCycleItemValueVoList = dynCycleItemValueVoDao.quaryForDetail(has);
        if(dynCycleItemValueVoList!=null && dynCycleItemValueVoList.size()>0){
            arrTemp = new String[dynCycleItemValueVoList.size()];
            for(int i = 0 ; i <dynCycleItemValueVoList.size() ; i++){
//                hashMaps.add(new HashMap<Integer, String>());
                if(i==0){
                    if(!TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getBalance())){
                        String balance = dynCycleItemValueVoList.get(i).getBalance().toString();
                        arrTemp[i] = balance;
                    }
                }else if(i==1){
                    if(!TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getCycleNo())) {
                        String CycleNo = dynCycleItemValueVoList.get(i).getCycleNo().toString();
                        arrTemp[i] = CycleNo;
                    }
                }else if(i==2){
                    if(!TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getDepositamount())) {
                        String Depositamount = dynCycleItemValueVoList.get(i).getDepositamount().toString();
                        arrTemp[i] = Depositamount;
                    }
                }else if(i==3){
                    if(!TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getWithdrawamount())) {
                        String Withdrawamount = dynCycleItemValueVoList.get(i).getWithdrawamount().toString();
                        arrTemp[i] = Withdrawamount;
                    }
                }else{
                    if(!TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getValue())){
                        String  Value = dynCycleItemValueVoList.get(i).getValue().toString();
                        arrTemp[i] = Value;
                    }
                }

            }

            myKeyTransferAdpater = new MyKeyTransferAdpater1(this , dynCycleItemValueVoList);
            mlist.setAdapter(myKeyTransferAdpater);
            isFalse = true;

        }else{
            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("atmCustomerId",atmVo.getAtmcustomerid());
            dynCycleItemVos = dynCycleDao.quaryWithOrderByLists(hashMap);
            //

            dynCycleItemValueVoList = new ArrayList<>();
            DynCycleItemValueVo dynCycleItemValueVo1 = new DynCycleItemValueVo();
            dynCycleItemValueVo1.setBarcode(atmVo.getBarcode());
            dynCycleItemValueVo1.setAtmid(atmVo.getAtmid());
            dynCycleItemValueVo1.setTaskid(atmVo.getTaskid());
            dynCycleItemValueVo1.setName(getResources().getString(R.string.balance));
//            dynCycleItemValueVo.setCode(dynCycleItemVos.get(i).getCode());
            dynCycleItemValueVo1.setAtmCustomerId(atmVo.getAtmcustomerid());
//            dynCycleItemValueVo.setOrder(dynCycleItemVos.get(i).getOrder());
            dynCycleItemValueVo1.setEnabled(true);
            dynCycleItemValueVo1.setIsneeded(false);
            dynCycleItemValueVo1.setClientid(clientid);
            dynCycleItemValueVo1.setIsUploaded("N");
            dynCycleItemValueVo1.setOperatedtime(Util.getNowDetial_toString());
            dynCycleItemValueVo1.setOperator(UtilsManager.getOperaterUsers(users));
            dynCycleItemValueVoList.add(dynCycleItemValueVo1);
//            hashMaps.add(new HashMap<Integer, String>());


            DynCycleItemValueVo dynCycleItemValueVo2 = new DynCycleItemValueVo();
            dynCycleItemValueVo2.setBarcode(atmVo.getBarcode());
            dynCycleItemValueVo2.setName(getResources().getString(R.string.cycleNo));
            dynCycleItemValueVo2.setAtmCustomerId(atmVo.getAtmcustomerid());
            dynCycleItemValueVo2.setEnabled(true);
            dynCycleItemValueVo2.setIsneeded(false);
            dynCycleItemValueVo2.setClientid(clientid);
            dynCycleItemValueVo2.setIsUploaded("N");
            dynCycleItemValueVo2.setAtmid(atmVo.getAtmid());
            dynCycleItemValueVo2.setTaskid(atmVo.getTaskid());
            dynCycleItemValueVo2.setOperatedtime(Util.getNowDetial_toString());
            dynCycleItemValueVo2.setOperator(UtilsManager.getOperaterUsers(users));
            dynCycleItemValueVoList.add(dynCycleItemValueVo2);
//            hashMaps.add(new HashMap<Integer, String>());

            DynCycleItemValueVo dynCycleItemValueVo3 = new DynCycleItemValueVo();
            dynCycleItemValueVo3.setBarcode(atmVo.getBarcode());
            dynCycleItemValueVo3.setName(getResources().getString(R.string.amount_deposited));
            dynCycleItemValueVo3.setAtmCustomerId(atmVo.getAtmcustomerid());
            dynCycleItemValueVo3.setEnabled(true);
            dynCycleItemValueVo3.setIsneeded(false);
            dynCycleItemValueVo3.setClientid(clientid);
            dynCycleItemValueVo3.setIsUploaded("N");
            dynCycleItemValueVo3.setAtmid(atmVo.getAtmid());
            dynCycleItemValueVo3.setTaskid(atmVo.getTaskid());
            dynCycleItemValueVo3.setOperatedtime(Util.getNowDetial_toString());
            dynCycleItemValueVo3.setOperator(UtilsManager.getOperaterUsers(users));
            dynCycleItemValueVoList.add(dynCycleItemValueVo3);
//            hashMaps.add(new HashMap<Integer, String>());

            DynCycleItemValueVo dynCycleItemValueVo4 = new DynCycleItemValueVo();
            dynCycleItemValueVo4.setBarcode(atmVo.getBarcode());
            dynCycleItemValueVo4.setName(getResources().getString(R.string.amount_money));
            dynCycleItemValueVo4.setAtmCustomerId(atmVo.getAtmcustomerid());
            dynCycleItemValueVo4.setEnabled(true);
            dynCycleItemValueVo4.setIsneeded(false);
            dynCycleItemValueVo4.setClientid(clientid);
            dynCycleItemValueVo4.setIsUploaded("N");
            dynCycleItemValueVo4.setAtmid(atmVo.getAtmid());
            dynCycleItemValueVo4.setTaskid(atmVo.getTaskid());
            dynCycleItemValueVo4.setOperatedtime(Util.getNowDetial_toString());
            dynCycleItemValueVo4.setOperator(UtilsManager.getOperaterUsers(users));
            dynCycleItemValueVoList.add(dynCycleItemValueVo4);

//            hashMaps.add(new HashMap<Integer, String>());

            if(dynCycleItemVos!=null && dynCycleItemVos.size()>0){

                for(int i = 0 ; i < dynCycleItemVos.size() ; i ++ ){
                    DynCycleItemValueVo dynCycleItemValueVo = new DynCycleItemValueVo();
                    dynCycleItemValueVo.setBarcode(atmVo.getBarcode());
                    dynCycleItemValueVo.setName(dynCycleItemVos.get(i).getName());
                    dynCycleItemValueVo.setCode(dynCycleItemVos.get(i).getCode());
                    dynCycleItemValueVo.setAtmCustomerId(dynCycleItemVos.get(i).getAtmCustomerId());
                    dynCycleItemValueVo.setOrder(dynCycleItemVos.get(i).getOrder());
                    dynCycleItemValueVo.setEnabled(dynCycleItemVos.get(i).isEnabled());
                    dynCycleItemValueVo.setIsneeded(dynCycleItemVos.get(i).isneeded());
                    dynCycleItemValueVo.setClientid(clientid);
                    dynCycleItemValueVo.setIsUploaded("N");
                    dynCycleItemValueVo.setAtmid(atmVo.getAtmid());
                    dynCycleItemValueVo.setTaskid(atmVo.getTaskid());
                    dynCycleItemValueVo.setOperatedtime(Util.getNowDetial_toString());
                    dynCycleItemValueVo.setOperator(UtilsManager.getOperaterUsers(users));
                    dynCycleItemValueVoList.add(dynCycleItemValueVo);
//                    hashMaps.add(new HashMap<Integer, String>());
                }
            }

////            //测试
//            DynCycleItemValueVo dynCycleItemValueVo = new DynCycleItemValueVo();
//            dynCycleItemValueVo.setBarcode(atmVo.getBarcode());
//            dynCycleItemValueVo.setName("aaa");
//            dynCycleItemValueVo.setCode("aaa");
//            dynCycleItemValueVo.setAtmCustomerId(atmVo.getAtmcustomerid());
//            dynCycleItemValueVo.setOrder(1);
//            dynCycleItemValueVo.setEnabled(true);
//            dynCycleItemValueVo.setIsneeded(true);
//            dynCycleItemValueVo.setClientid(clientid);
//            dynCycleItemValueVo.setIsUploaded("N");
//            dynCycleItemValueVo.setOperatedtime(Util.getNowDetial_toString());
//            dynCycleItemValueVo.setOperator(UtilsManager.getOperaterUsers(users));
//            dynCycleItemValueVoList.add(dynCycleItemValueVo);
//            hashMaps.add(new HashMap<Integer, String>());
////
//            DynCycleItemValueVo dynCycleItemVa = new DynCycleItemValueVo();
//            dynCycleItemVa.setBarcode(atmVo.getBarcode());
//            dynCycleItemVa.setName("bbb");
//            dynCycleItemVa.setCode("bbb");
//            dynCycleItemVa.setAtmCustomerId(atmVo.getAtmcustomerid());
//            dynCycleItemVa.setOrder(1);
//            dynCycleItemVa.setEnabled(true);
//            dynCycleItemVa.setIsneeded(true);
//            dynCycleItemVa.setClientid(clientid);
//            dynCycleItemVa.setIsUploaded("N");
//            dynCycleItemVa.setOperatedtime(Util.getNowDetial_toString());
//            dynCycleItemVa.setOperator(UtilsManager.getOperaterUsers(users));
//            dynCycleItemValueVoList.add(dynCycleItemVa);
//            hashMaps.add(new HashMap<Integer, String>());

            arrTemp = new String[dynCycleItemValueVoList.size()];


            myKeyTransferAdpater = new MyKeyTransferAdpater1(this ,dynCycleItemValueVoList);
            mlist.setAdapter(myKeyTransferAdpater);
        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                if(!checkAll(dynCycleItemValueVoList)){
                    if(isFalse){
                        if(isChange()){
                            showTimeConfirmDialog();
                        }else{
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.dyncle_saved));
                        }
                    }else{
                        showTimeConfirmDialog();
                    }
                }
                break;
            case R.id.btn_back:
                finish();
                break;
        }

    }

    //数据有改变更新数据上传
    private boolean isChange() {
        if (isFalse) {
            //再次对凭条登记做修改 ，上传数据 需先判断数据是否改变
            HashMap<String, Object> has = new HashMap<>();
            has.put("atmCustomerId", atmVo.getAtmcustomerid());
            has.put("atmid", atmVo.getAtmid());
            has.put("branchid", atmVo.getBranchid());
            List<DynCycleItemValueVo> dynCycleItemList = dynCycleItemValueVoDao.quaryForDetail(has);

            if (dynCycleItemList != null && dynCycleItemList.size() > 0) {
                for (int i = 0; i < dynCycleItemList.size(); i++) {
                    if (i == 0) {
                        if (!dynCycleItemList.get(i).getBalance().equals(dynCycleItemValueVoList.get(i).getBalance())) {
                            return true;
                        }
                    } else if (i == 1) {
                        if (TextUtils.isEmpty(dynCycleItemList.get(i).getCycleNo()) && TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getCycleNo())) {

                        } else {
                            if (TextUtils.isEmpty(dynCycleItemList.get(i).getCycleNo()) && !TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getCycleNo())) {
                                return true;
                            }

                            if (!TextUtils.isEmpty(dynCycleItemList.get(i).getCycleNo()) && TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getCycleNo())) {
                                return true;
                            }

                            if (!dynCycleItemList.get(i).getCycleNo().equals(dynCycleItemValueVoList.get(i).getCycleNo())) {
                                return true;
                            }
                        }
                    } else if (i == 2) {

                        if (TextUtils.isEmpty(dynCycleItemList.get(i).getDepositamount()) && TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getDepositamount())) {

                        } else {
                            if (TextUtils.isEmpty(dynCycleItemList.get(i).getDepositamount()) && !TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getDepositamount())) {
                                return true;
                            }

                            if (!TextUtils.isEmpty(dynCycleItemList.get(i).getDepositamount()) && TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getDepositamount())) {
                                return true;
                            }

                            if (!dynCycleItemList.get(i).getDepositamount().equals(dynCycleItemValueVoList.get(i).getDepositamount())) {
                                return true;
                            }
                        }

//                        if (!dynCycleItemList.get(i).getDepositamount().equals(dynCycleItemValueVoList.get(i).getDepositamount())) {
//                            return true;
//                        }
                    } else if (i == 3) {

                        if (TextUtils.isEmpty(dynCycleItemList.get(i).getWithdrawamount()) && TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getWithdrawamount())) {

                        } else {
                            if (TextUtils.isEmpty(dynCycleItemList.get(i).getWithdrawamount()) && !TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getWithdrawamount())) {
                                return true;
                            }

                            if (!TextUtils.isEmpty(dynCycleItemList.get(i).getWithdrawamount()) && TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getWithdrawamount())) {
                                return true;
                            }

                            if (!dynCycleItemList.get(i).getWithdrawamount().equals(dynCycleItemValueVoList.get(i).getWithdrawamount())) {
                                return true;
                            }
                        }




//                        if (!dynCycleItemList.get(i).getWithdrawamount().equals(dynCycleItemValueVoList.get(i).getWithdrawamount())) {
//                            return true;
//                        }
                    } else {

                        if (TextUtils.isEmpty(dynCycleItemList.get(i).getValue()) && TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getValue())) {

                        } else {
                            if (TextUtils.isEmpty(dynCycleItemList.get(i).getValue()) && !TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getValue())) {
                                return true;
                            }

                            if (!TextUtils.isEmpty(dynCycleItemList.get(i).getValue()) && TextUtils.isEmpty(dynCycleItemValueVoList.get(i).getValue())) {
                                return true;
                            }

                            if (!dynCycleItemList.get(i).getValue().equals(dynCycleItemValueVoList.get(i).getValue())) {
                                return true;
                            }
                        }


//                        if (!dynCycleItemList.get(i).getValue().equals(dynCycleItemValueVoList.get(i).getValue())) {
//                            return true;
//                        }
                    }

                }
            }


        } else {
            return false;
        }

        return false;

    }


    //检测是否有必填项 未填
    private boolean checkAll(List<DynCycleItemValueVo> dynCycleItemValueVoList) {
        if (dynCycleItemValueVoList != null && dynCycleItemValueVoList.size() > 0) {
            if (TextUtils.isEmpty(dynCycleItemValueVoList.get(0).getBalance())) {
                CustomToast.getInstance().showShortToast(
                        String.format(getResources().getString(R.string.tost_is_need),
                                dynCycleItemValueVoList.get(0).getName()));
                return true;
            }
            for (DynCycleItemValueVo dyn : dynCycleItemValueVoList) {
                if(dyn.isneeded()){
                    if(TextUtils.isEmpty(dyn.getValue())){
                        CustomToast.getInstance().showShortToast(
                                String.format(getResources().getString(R.string.tost_is_need),
                                        dyn.getName()));
                        return true;
                    }
                }
            }

        } else {
            return false;
        }
        return false;
    }


    public final class ViewHolder {
        public TextView tv_item;
        public EditText tv_item_edit;
        int ref;

    }

    public class MyKeyTransferAdpater extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<DynCycleItemValueVo> dynCycleItemVos;

        public MyKeyTransferAdpater(Context context, List<DynCycleItemValueVo> dynCycleItemVoList) {

            layoutInflater = LayoutInflater.from(context);
            dynCycleItemVos = dynCycleItemVoList;
        }

        @Override
        public int getCount() {
            return dynCycleItemVos.size();
        }

        @Override
        public Object getItem(int position) {
            return dynCycleItemVos.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        private Integer index = -1;


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if (convertView == null) {

                PDALogger.d("DynCycle ==" + convertView);
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.dyncycleitem, null);
                viewHolder.tv_item= (TextView) convertView.findViewById(R.id.local_name);
                viewHolder.tv_item_edit = (EditText) convertView.findViewById(R.id.Local_UP);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
//                viewHolder.tv_item_edit.setTag(position+"");
//                viewHolder.tv_item.setTag(position+"");
                PDALogger.d("DynCycle == tag" + convertView);
                if (editText!=null) {
                    editText.requestFocus();
                }
            }


            viewHolder.tv_item_edit.setTag(position + "");
            viewHolder.tv_item.setTag(position + "");


            viewHolder.tv_item_edit.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View view, MotionEvent event) {

                    // 在TOUCH的UP事件中，要保存当前的行下标，因为弹出软键盘后，整个画面会被重画

                    // 在getView方法的最后，要根据index和当前的行下标手动为EditText设置焦点

                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        index = Integer.parseInt(view.getTag().toString());
                        editText = (EditText) view;
                        editText.requestFocus();
                    }

                    return false;

                }

            });



            class MyTextWatcher implements TextWatcher {
                public MyTextWatcher(ViewHolder holder) {
                    mHolder = holder;
                }

                private ViewHolder mHolder;

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null && !"".equals(s.toString())) {
                        int position  = Integer.parseInt(mHolder.tv_item_edit.getTag().toString());
                        hashMaps.get(position).put(position, s.toString());
                        if(position == 0){
                            dynCycleItemValueVoList.get(position).setBalance(String.valueOf(s));
                            dynCycleItemValueVoList.get(position).setValue(String.valueOf(s));
                        }else if(position == 1){
                            dynCycleItemValueVoList.get(position).setCycleNo(String.valueOf(s));
                        }else if(position == 2){
                            dynCycleItemValueVoList.get(position).setDepositamount(String.valueOf(s));
                        }else if(position == 3){
                            dynCycleItemValueVoList.get(position).setWithdrawamount(String.valueOf(s));
                        }else {
                            dynCycleItemValueVoList.get(position).setValue(String.valueOf(s));
                        }
                        // 当EditText数据发生改变的时候存到data变量中
                    }
                }
            }
            viewHolder.tv_item.setText(dynCycleItemVos.get(position).getName());
            viewHolder.tv_item_edit.addTextChangedListener(new MyTextWatcher(viewHolder));
            if(hashMaps.get(position).get(position)!=null){
                viewHolder.tv_item_edit.setText(hashMaps.get(position).get(position));
            }



            viewHolder.tv_item_edit.clearFocus();

            if(editText!=null) {

                // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。

                editText.requestFocus();

            }


            if (index!=-1&&(index==position)) {
                viewHolder.tv_item_edit.requestFocus();
            }
            return convertView;
        }


    }


    //登记过数据

    public class DynCycleAdpater extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<DynCycleItemValueVo> dynCycleItemVos;

        public DynCycleAdpater(Context context, List<DynCycleItemValueVo> dynCycleItemVoList) {

            layoutInflater = LayoutInflater.from(context);
            dynCycleItemVos = dynCycleItemVoList;
        }

        @Override
        public int getCount() {
            return dynCycleItemVos.size();
        }

        @Override
        public Object getItem(int position) {
            return dynCycleItemVos.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        private Integer index = -1;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.dyncycleitem, null);
                viewHolder.tv_item = (TextView) convertView.findViewById(R.id.local_name);
                viewHolder.tv_item_edit = (EditText) convertView.findViewById(R.id.Local_UP);
                viewHolder.tv_item.setText(dynCycleItemVos.get(position).getName());
                if(!ispager){
                    if (position == 0) {
                        hashMaps.get(position).put(position, dynCycleItemVos.get(position).getBalance());
                    } else if (position == 1) {
                        hashMaps.get(position).put(position, dynCycleItemVos.get(position).getCycleNo());
                    } else if (position == 2) {
                        hashMaps.get(position).put(position, dynCycleItemVos.get(position).getDepositamount());
                    } else if (position == 3) {
                        hashMaps.get(position).put(position, dynCycleItemVos.get(position).getWithdrawamount());
                    } else {
                        hashMaps.get(position).put(position, dynCycleItemVos.get(position).getValue());
                    }
                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                if (editText!=null) {
                    editText.requestFocus();
                }
            }


            viewHolder.tv_item_edit.setTag(position + "");
            viewHolder.tv_item.setTag(position + "");


            viewHolder.tv_item_edit.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View view, MotionEvent event) {

                    // 在TOUCH的UP事件中，要保存当前的行下标，因为弹出软键盘后，整个画面会被重画

                    // 在getView方法的最后，要根据index和当前的行下标手动为EditText设置焦点

                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        index = Integer.parseInt(view.getTag().toString());
                        editText = (EditText) view;
                        ispager = true;
                    }

                    return false;

                }

            });



            class MyTextWatcher implements TextWatcher {
                public MyTextWatcher(ViewHolder holder) {
                    mHolder = holder;
                }

                private ViewHolder mHolder;

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null && !"".equals(s.toString())) {
                        int position  = Integer.parseInt(mHolder.tv_item_edit.getTag().toString());
                        hashMaps.get(position).put(position, s.toString());
                        if(position == 0){
                            dynCycleItemValueVoList.get(position).setBalance(String.valueOf(s));
                        }else if(position == 1){
                            dynCycleItemValueVoList.get(position).setCycleNo(String.valueOf(s));
                        }else if(position == 2){
                            dynCycleItemValueVoList.get(position).setDepositamount(String.valueOf(s));
                        }else if(position == 3){
                            dynCycleItemValueVoList.get(position).setWithdrawamount(String.valueOf(s));
                        }else {
                            dynCycleItemValueVoList.get(position).setValue(String.valueOf(s));
                        }
                        // 当EditText数据发生改变的时候存到data变量中
                    }
                }
            }
            viewHolder.tv_item.setText(dynCycleItemVos.get(position).getName());
            viewHolder.tv_item_edit.addTextChangedListener(new MyTextWatcher(viewHolder));

            if(hashMaps.get(position).get(position)!=null){
                viewHolder.tv_item_edit.setText(hashMaps.get(position).get(position));
            }



//            viewHolder.tv_item_edit.clearFocus();

//            if(editText!=null) {
//
//                // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
//
//                editText.requestFocus();
//
//            }


//            if (index!=-1&&(index==position)) {
//                viewHolder.tv_item_edit.requestFocus();
//            }

            return convertView;
        }


    }



    private void showTimeConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.check_over));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showList();
                dialog.dismiss();
                finish();
            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    private void showList(){
        if(isFalse){//更新
            if(dynCycleItemValueVoList!=null && dynCycleItemValueVoList.size()>0){
                for(int i = 0 ;i < dynCycleItemValueVoList.size() ; i ++){
                    dynCycleItemValueVoList.get(i).setIsUploaded("N");
                    dynCycleItemValueVoDao.upDate(dynCycleItemValueVoList.get(i));
                }
            }

        }else{
            HashMap<String ,Object> has =new HashMap<>();
            has.put("atmCustomerId" ,atmVo.getAtmcustomerid() );
            has.put("atmid" ,atmVo.getAtmid());
            has.put("branchid", atmVo.getBranchid());
            List<DynCycleItemValueVo> dynCycleItemValueVoList1 = dynCycleItemValueVoDao.quaryForDetail(has);
            if(dynCycleItemValueVoList1!=null && dynCycleItemValueVoList1.size()>0){
                for(int i = 0 ;i < dynCycleItemValueVoList1.size() ; i ++){
                    dynCycleItemValueVoList1.get(i).setIsUploaded("N");
                    dynCycleItemValueVoDao.upDate(dynCycleItemValueVoList1.get(i));
                }
            }else{
                if(dynCycleItemValueVoList!=null && dynCycleItemValueVoList.size()>0){
                    for(int i = 0 ;i < dynCycleItemValueVoList.size() ; i ++){
                        dynCycleItemValueVoList.get(i).setBranchid(atmVo.getBranchid());
                        dynCycleItemValueVoList.get(i).setAtmid(atmVo.getAtmid());
                        dynCycleItemValueVoList.get(i).setUuid(UUID.randomUUID().toString());
                        dynCycleItemValueVoDao.create(dynCycleItemValueVoList.get(i));
                    }
                }
            }
        }

        //更新网点下机具是否登记
        HashMap<String, Object> repair = new HashMap<String, Object>();
        repair.put("branchid", atmVo.getBranchid());
        repair.put("barcode" ,atmVo.getBarcode());
        List<UniqueAtmVo>  uniqueAtmVos = uniqueAtmDao.quaryForDetail(repair);
        if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
            UniqueAtmVo uniqueAtmVo= uniqueAtmVos.get(0);
            uniqueAtmVo.setIsRegister("Y");
            uniqueAtmVo.setIsUploaded("N");
            uniqueAtmDao.upDate(uniqueAtmVo);
        }

        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);

    }


    public class MyKeyTransferAdpater1 extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<DynCycleItemValueVo> dynCycleItemVos;

        public MyKeyTransferAdpater1(Context context, List<DynCycleItemValueVo> dynCycleItemVoList) {

            layoutInflater = LayoutInflater.from(context);
            dynCycleItemVos = dynCycleItemVoList;
        }

        @Override
        public int getCount() {
            return dynCycleItemVos.size();
        }

        @Override
        public Object getItem(int position) {
            return dynCycleItemVos.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        private Integer index = -1;


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder ;

            if (convertView == null) {

//                PDALogger.d("DynCycle ==" + convertView);
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.dyncycleitem, null);
                viewHolder.tv_item= (TextView) convertView.findViewById(R.id.local_name);
                viewHolder.tv_item_edit = (EditText) convertView.findViewById(R.id.Local_UP);
                viewHolder.tv_item_edit.setTag(position);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
//                PDALogger.d("DynCycle == tag" + convertView);
//                if (editText!=null) {
//                    editText.requestFocus();
//                }
            }
            viewHolder.ref = position;
            viewHolder.tv_item_edit.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View view, MotionEvent event) {

                    // 在TOUCH的UP事件中，要保存当前的行下标，因为弹出软键盘后，整个画面会被重画

                    // 在getView方法的最后，要根据index和当前的行下标手动为EditText设置焦点

                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        index = Integer.parseInt(view.getTag().toString());
                        editText = (EditText) view;
                        editText.requestFocus();

//                        PDALogger.d("nnnnnnnnnnnnnnnnaaaaaaaa");

                    }

                    return false;

                }

            });


            class MyTextWatcher implements TextWatcher {
                private ViewHolder mHolder;

                public MyTextWatcher(ViewHolder holder) {
                    mHolder = holder;
                }



                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
//                    PDALogger.d("s======" +s.toString());
                    if (s != null && !"".equals(s.toString())) {
                        arrTemp[viewHolder.ref] = s.toString();
                        if(viewHolder.ref == 0){
                            dynCycleItemValueVoList.get(viewHolder.ref).setBalance(String.valueOf(s));
                        }else if(viewHolder.ref == 1){
                            dynCycleItemValueVoList.get(viewHolder.ref).setCycleNo(String.valueOf(s));
                        }else if(viewHolder.ref == 2){
                            dynCycleItemValueVoList.get(viewHolder.ref).setDepositamount(String.valueOf(s));
                        }else if(viewHolder.ref == 3){
                            dynCycleItemValueVoList.get(viewHolder.ref).setWithdrawamount(String.valueOf(s));
                        }else {
                            dynCycleItemValueVoList.get(viewHolder.ref).setValue(String.valueOf(s));
                        }

                    }else{
                        arrTemp[viewHolder.ref] = "";
                        if(viewHolder.ref == 0){
                            dynCycleItemValueVoList.get(viewHolder.ref).setBalance("");
                        }else if(viewHolder.ref == 1){
                            dynCycleItemValueVoList.get(viewHolder.ref).setCycleNo("");
                        }else if(viewHolder.ref == 2){
                            dynCycleItemValueVoList.get(viewHolder.ref).setDepositamount("");
                        }else if(viewHolder.ref == 3){
                            dynCycleItemValueVoList.get(viewHolder.ref).setWithdrawamount("");
                        }else {
                            dynCycleItemValueVoList.get(viewHolder.ref).setValue("");
                        }
                    }
                }
            }

            viewHolder.tv_item.setText(dynCycleItemVos.get(position).getName());
            if(arrTemp[position]!=null){
                viewHolder.tv_item_edit.setText(arrTemp[position]);
            }

            viewHolder.tv_item_edit.addTextChangedListener(new MyTextWatcher(viewHolder));

//            viewHolder.tv_item_edit.clearFocus();
//            if(editText!=null) {
//
//                // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
//
//                PDALogger.d("cccccccccccccccc");
//                editText.requestFocus();
//
//            }


//            if (index!=-1&&(index==position)) {
//                viewHolder.tv_item_edit.requestFocus();
//                PDALogger.d("xxxxxxxxxxxxxxxxxxxxxxx");
//            }

//            PDALogger.d("aaaaaaaaaaaaaaaaaaa");
            return convertView;
        }


    }




}
