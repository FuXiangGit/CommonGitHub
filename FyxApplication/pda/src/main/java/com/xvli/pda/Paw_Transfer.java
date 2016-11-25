package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.bean.BranchVo;
import com.xvli.bean.DynNodeItemVo;
import com.xvli.bean.KeyPasswordVo;
import com.xvli.bean.LoginVo;
import com.xvli.comm.Config;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DynNodeDao;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.dao.LoginDao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Paw_Transfer extends BaseActivity implements View.OnClickListener {

    private ListView key_transfer_list;
    //    private EditText  editText_transfer,editText_receive;//交接人，接收人。
    private TextView key_scan;
    private Button btn_back ,bt_updata_key_transfer;
    private long scanTime=-1;
    private String scanResult="",clientid;
    private TimeCount time;//扫描倒計時
    private List<HashMap<String,String>> key_scan_transfer_list = new ArrayList<HashMap<String,String>>();
    private List<LoginVo> users;
    private LoginDao login_dao;
    private List<BranchVo> branchVoList = new ArrayList<BranchVo>();
    private BranchVoDao branchVoDao;
    private KeyPasswordVo_Dao keyPasswordVoDao;
    private List<KeyPasswordVo> keyPasswordVoList  = new ArrayList<KeyPasswordVo>();
    private List<KeyPasswordVo> keyPasswordVoList1  = new ArrayList<KeyPasswordVo>(); ; //确认交接钥匙集合
    private String transfer ;
    private String recvice;
    private boolean isPlan;
    private TextView tv_title,btn_ok;
    private DynNodeDao  dynNodeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paw__transfer);
        time = new TimeCount(500, 1);//构造CountDownTimer对象
        InitView();
    }


    public  void InitView(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.Paw_transfer));

        Intent intent = getIntent();
        transfer = intent.getExtras().getString("transfer");
        recvice =  intent.getExtras().getString("recvice");
        isPlan  =  intent.getExtras().getBoolean("isPlan");
        keyPasswordVoDao = new KeyPasswordVo_Dao(getHelper());
        branchVoDao = new BranchVoDao(getHelper());
        login_dao = new LoginDao(getHelper());
        dynNodeDao = new DynNodeDao(getHelper());
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            clientid = UtilsManager.getClientid(users);
        }
        key_transfer_list = (ListView)findViewById(R.id.key_transfer_list);
//        editText_transfer = (EditText)findViewById(R.id.add_key_tansfer);//交接人
//        editText_receive  = (EditText)findViewById(R.id.add_key_receive);
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_ok  = (TextView)findViewById(R.id.btn_ok);
        bt_updata_key_transfer = (Button)findViewById(R.id.bt_updata_key_transfer);
        key_scan = (TextView)findViewById(R.id.key_scan);
        btn_ok.setVisibility(View.GONE);
        btn_back.setOnClickListener(this);
        bt_updata_key_transfer.setOnClickListener(this);




    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_MULTIPLE){
            if((System.currentTimeMillis()-scanTime)>500)
            {
                time.start();
                scanResult=""+event.getCharacters();
                scanTime=System.currentTimeMillis();
            }
            else
            {
                scanResult=scanResult+event.getCharacters();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {

        if(v ==btn_back ){
            int resultCode = 1;

            Intent in = new Intent();
            in.putExtra("code","");
            Paw_Transfer.this.setResult(resultCode,in);
            finish();
        }else if(v == bt_updata_key_transfer){
            if(keyPasswordVoList1!=null&&keyPasswordVoList1.size()>0){
                PDALogger.d(keyPasswordVoList1.size() + "test");
                showSureUpDataDialog();
            }else{
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.check_paw_transfer));
            }
        }


    }

    //确认提交
    private void showSureUpDataDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_sure_item));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                for (int i  = 0 ; i < keyPasswordVoList1.size(); i ++){
                    keyPasswordVoDao.create(keyPasswordVoList1.get(i));
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("clientid", clientid);
                    hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                    hashMap.put("barcode", keyPasswordVoList1.get(i).getBarcode());
                    hashMap.put("isTransfer", "Y");

                    List<KeyPasswordVo> keyPasswordVoList1 = keyPasswordVoDao.quaryForDetail(hashMap);
                    KeyPasswordVo bean1 = keyPasswordVoList1.get(0);
                    bean1.setRemake(String.valueOf(bean1.getId()));
                    keyPasswordVoDao.upDate(bean1);

                }
                //提交数据
                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                intent.putExtra("itemtype", KeyPasswordVo.PASSWORD);
                sendBroadcast(intent);

                int resultCode = 1;


                Intent in = new Intent();
                in.putExtra("code", keyPasswordVoList1.get(0).getBarcode());
                Paw_Transfer.this.setResult(resultCode, in);

                finish();

                dialog.dismiss();
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


    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer
    {
        public TimeCount(long millisInFuture, long countDownInterval)
        {

            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish()
        {
            PDALogger.d("scanResult = " + scanResult);
            keyScantransfer(scanResult);
        }
        @Override
        public void onTick(long millisUntilFinished)
        {
            //计时过程显示

        }
    }


    public void keyScantransfer(String scanResult) {
        if (Regex.isChaoPaw(scanResult)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("barcode", scanResult);
            List<KeyPasswordVo> list = keyPasswordVoDao.quaryForDetail(hashMap);
            if (list != null && list.size() > 0) {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.dontrecePaw));
            } else {
                String bracchvo_code = scanResult.substring(2, scanResult.length());
                HashMap<String ,Object> has = new HashMap<>();
                has.put("barcode", bracchvo_code);
                List<DynNodeItemVo> dynNodeItemVos = dynNodeDao.quaryForDetail(has);
                if (dynNodeItemVos!=null &&dynNodeItemVos.size()>0) {

                    int count = 0;
                    HashMap<String, Object> branchVo1 = new HashMap<String, Object>();
                    branchVo1.put("clientid", clientid);
                    branchVo1.put("code", bracchvo_code);
                    branchVoList = branchVoDao.quaryForDetail(branchVo1);
                    if (branchVoList != null && branchVoList.size() > 0) {//计划内
                        HashMap<String, String> keyPaw = new HashMap<String, String>();
                        keyPaw.put("itemtype", KeyPasswordVo.PASSWORD);
                        keyPaw.put("barcode", scanResult);
                        keyPaw.put("isTransfer", "Y");
                        keyPasswordVoList = keyPasswordVoDao.quaryForDetail(keyPaw);
                        if (keyPasswordVoList != null && keyPasswordVoList.size() > 0) {
                            CustomToast.getInstance().showShortToast(getResources().getString(R.string.check_paw_transfer));
                        } else {
                            if (key_scan_transfer_list != null && key_scan_transfer_list.size() > 0) {
                                for (int i = 0; i < key_scan_transfer_list.size(); i++) {
                                    if (key_scan_transfer_list.get(i).get("barcode").equals(scanResult)) {
                                        count++;
                                    }
                                }
                                if (count == 0) {
                                    KeyPasswordVo bean = new KeyPasswordVo();
                                    bean.setItemtype(KeyPasswordVo.PASSWORD);
                                    bean.setBarcode(scanResult);
                                    bean.setClientid(clientid);
                                    bean.setBranchid(branchVoList.get(0).getBranchid());
                                    bean.setIsScan("Y");
                                    bean.setBranchname(branchVoList.get(0).getBranchname());
                                    bean.setIsUploaded("N");
                                    bean.setIsTransfer("Y");
                                    bean.setTransfer(transfer);
                                    bean.setRecvice(recvice);
                                    if (isPlan) {
                                        bean.setIsPlan("Y");
                                    } else {
                                        bean.setIsPlan("N");
                                    }
                                    bean.setBranchCode(scanResult.substring(2, scanResult.length()));
                                    bean.setOperator(UtilsManager.getOperaterUsers(users));
                                    bean.setOperatetime(Util.getNowDetial_toString());
                                    keyPasswordVoList1.add(bean);

                                    key_scan_transfer_list.add(keyPaw);
                                    key_scan.setVisibility(View.GONE);
                                    key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, key_scan_transfer_list));
                                } else {
                                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.check_paw_transfer));
                                }

                            } else {
                                KeyPasswordVo bean = new KeyPasswordVo();
                                bean.setItemtype(KeyPasswordVo.PASSWORD);
                                bean.setBarcode(scanResult);
                                bean.setClientid(clientid);
                                bean.setBranchid(branchVoList.get(0).getBranchid());
                                bean.setIsScan("Y");
                                bean.setBranchname(branchVoList.get(0).getBranchname());
                                bean.setIsUploaded("N");
                                bean.setIsTransfer("Y");
                                bean.setTransfer(transfer);
                                bean.setRecvice(recvice);
                                if (isPlan) {
                                    bean.setIsPlan("Y");
                                } else {
                                    bean.setIsPlan("N");
                                }
                                bean.setBranchCode(scanResult.substring(2, scanResult.length()));
                                bean.setOperator(UtilsManager.getOperaterUsers(users));
                                bean.setOperatetime(Util.getNowDetial_toString());
                                keyPasswordVoList1.add(bean);
                                key_scan_transfer_list.add(keyPaw);
                                key_scan.setVisibility(View.GONE);
                                ;
                                key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, key_scan_transfer_list));
                            }
                        }

                    } else {
                        HashMap<String, String> keyPaw = new HashMap<String, String>();
                        keyPaw.put("itemtype", KeyPasswordVo.PASSWORD);
                        keyPaw.put("barcode", scanResult);
                        keyPaw.put("isTransfer", "Y");
                        keyPasswordVoList = keyPasswordVoDao.quaryForDetail(keyPaw);
                        if (keyPasswordVoList != null && keyPasswordVoList.size() > 0) {
                            CustomToast.getInstance().showShortToast(getResources().getString(R.string.check_paw_transfer));
                        } else {
                            if (key_scan_transfer_list != null && key_scan_transfer_list.size() > 0) {
                                for (int i = 0; i < key_scan_transfer_list.size(); i++) {
                                    if (key_scan_transfer_list.get(i).get("barcode").equals(scanResult)) {
                                        count++;
                                    }
                                }
                                if (count == 0) {
                                    KeyPasswordVo bean = new KeyPasswordVo();
                                    bean.setItemtype(KeyPasswordVo.PASSWORD);
                                    bean.setBarcode(scanResult);
                                    bean.setClientid(clientid);
                                    bean.setIsScan("Y");
                                    bean.setIsUploaded("N");
                                    bean.setIsTransfer("Y");
                                    bean.setTransfer(transfer);
                                    bean.setRecvice(recvice);
                                    if (isPlan) {
                                        bean.setIsPlan("Y");
                                    } else {
                                        bean.setIsPlan("N");
                                    }
                                    bean.setBranchCode(scanResult.substring(2, scanResult.length()));
                                    bean.setOperator(UtilsManager.getOperaterUsers(users));
                                    bean.setOperatetime(Util.getNowDetial_toString());
                                    keyPasswordVoList1.add(bean);

                                    key_scan_transfer_list.add(keyPaw);
                                    key_scan.setVisibility(View.GONE);
                                    key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, key_scan_transfer_list));
                                } else {
                                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.check_paw_transfer));
                                }

                            } else {
                                KeyPasswordVo bean = new KeyPasswordVo();
                                bean.setItemtype(KeyPasswordVo.PASSWORD);
                                bean.setBarcode(scanResult);
                                bean.setClientid(clientid);
                                bean.setIsScan("Y");
                                bean.setIsUploaded("N");
                                bean.setIsTransfer("Y");
                                bean.setTransfer(transfer);
                                bean.setRecvice(recvice);
                                if (isPlan) {
                                    bean.setIsPlan("Y");
                                } else {
                                    bean.setIsPlan("N");
                                }
                                bean.setBranchCode(scanResult.substring(2, scanResult.length()));
                                bean.setOperator(UtilsManager.getOperaterUsers(users));
                                bean.setOperatetime(Util.getNowDetial_toString());
                                keyPasswordVoList1.add(bean);
                                key_scan_transfer_list.add(keyPaw);
                                key_scan.setVisibility(View.GONE);
                                key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, key_scan_transfer_list));
                            }
                        }
                    }
                }else{
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_branch));
                }
            }
        } else {
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.please_scan_paw));
        }
    }


    public final class ViewHolder{
        public  TextView tv_item_code;
        public  TextView tv_item_status;
        public  Button  btn_item_delete;
    }

    public class MyKeyTransferAdpater extends BaseAdapter {

        //        private  Context mcontext;
        private LayoutInflater layoutInflater;
        private List<HashMap<String, String>> key_scan_transfer;

        public MyKeyTransferAdpater(Context context, List<HashMap<String, String>> keyList) {
//            mcontext = context;
            layoutInflater = LayoutInflater.from(context);
            key_scan_transfer = keyList;
        }

        @Override
        public int getCount() {
            return key_scan_transfer.size();
        }

        @Override
        public Object getItem(int position) {
            return key_scan_transfer.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.key_transfer_list_item, null);
                viewHolder.tv_item_code = (TextView) convertView.findViewById(R.id.tv_item_code);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_status);
                viewHolder.btn_item_delete = (Button) convertView.findViewById(R.id.tv_item_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            viewHolder.tv_item_code.setText(key_scan_transfer.get(position).get("barcode"));
            viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
            viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
            viewHolder.btn_item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(position);
                }
            });


            return convertView;
        }


    }


    //是否删除
    private void showDeleteDialog(final int position) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_delete_paw));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                key_scan_transfer_list.remove(position);
                key_transfer_list.setAdapter(new MyKeyTransferAdpater(Paw_Transfer.this, key_scan_transfer_list ));
                dialog.dismiss();
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




}
