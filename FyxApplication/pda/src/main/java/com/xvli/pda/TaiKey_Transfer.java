package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.bean.LoginVo;
import com.xvli.bean.ThingsVo;
import com.xvli.bean.TruckVo;
import com.xvli.comm.Config;
import com.xvli.dao.LoginDao;
import com.xvli.dao.ThingsDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 14:34.
 */
public class TaiKey_Transfer  extends BaseActivity implements View.OnClickListener {

    private ListView key_transfer_list;
    private TextView key_scan;
    private Button btn_back, bt_updata_key_transfer;
    private long scanTime = -1;
    private String scanResult = "", clientid;
    private TimeCount time;//扫描倒計時
    private List<HashMap<String, String>> key_scan_transfer_list = new ArrayList<HashMap<String, String>>();
    private List<LoginVo> users;
    private LoginDao login_dao;
    private String recvice;
    private TextView  tv_title, btn_ok;
    private int resultCode = 1;
    private String result;
    private ThingsDao  thingsDao;
    private List<ThingsVo> thingsVoList = new ArrayList<>();
    private String  changeflg;
    private String lineid = "" ;
    private TruckVo_Dao  truckVo_dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_transfer);
        time = new TimeCount(500, 1);//构造CountDownTimer对象
        InitView();

    }

    public void InitView() {
        tv_title = (TextView)findViewById(R.id.tv_title);
        Intent intent = getIntent();
        recvice = intent.getExtras().getString("recevice");//接收人
        result= intent.getExtras().getString("result"); //交接人
        changeflg = intent.getExtras().getString("changeflg");//交出 ，接收
        tv_title.setText(getResources().getString(R.string.chenk_article));

//        if(result == 0){
//            tv_title.setText(getResources().getString(R.string.key_transfer));
//            resultCode =1 ;
//        }else if(result == 1){
//            tv_title.setText(getResources().getString(R.string.Paw_transfer));
//            resultCode = 2 ;
//        }

        login_dao = new LoginDao(getHelper());
        truckVo_dao = new TruckVo_Dao(getHelper());

        thingsDao = new ThingsDao(getHelper());
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = UtilsManager.getClientid(users);
        }
        key_transfer_list = (ListView) findViewById(R.id.key_transfer_list);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        bt_updata_key_transfer = (Button) findViewById(R.id.bt_updata_key_transfer);
        key_scan = (TextView) findViewById(R.id.key_scan);
        btn_ok.setVisibility(View.GONE);
        btn_back.setOnClickListener(this);
        bt_updata_key_transfer.setOnClickListener(this);
        HashMap<String ,Object> has = new HashMap<>();
        has.put("operateType",1);
        List<TruckVo>  truckVos = truckVo_dao.quaryForDetail(has);
        if(truckVos!=null && truckVos.size()>0){
            lineid = truckVos.get(0).getLineid();
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
                scanResult = scanResult + event.getCharacters();
            }
        }
        return super.dispatchKeyEvent(event);
    }





    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            PDALogger.d("scanResult = " + scanResult);
            keyScantransfer(scanResult);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //计时过程显示

        }
    }


    public void keyScantransfer(String scanResult) {
        String receiptor = null;
        HashMap<String,Object> has  = new HashMap<>();
        has.put("isTransfer","N");
        List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
        if(thingsVos!=null &&  thingsVos.size()>0){
            for (int i = 0 ; i < thingsVos.size() ; i ++) {
                String tag = thingsVos.get(i).getReceiptor();
                if(!TextUtils.isEmpty(tag)){
                    if (Regex.isTaiKey(scanResult)) {
                        if(scanResult.substring(0, 3).equals(thingsVos.get(i).getBarcode().substring(0, 3))){
                            receiptor = tag;
                            break;
                        }
                    }else if(Regex.isTaiPassWord(scanResult)){
                        if(scanResult.substring(0,3).equals(thingsVos.get(i).getBarcode().substring(0,3))){
                            receiptor = tag;
                            break;
                        }
                    }
                }
            }
        }

        if(changeflg.equals("10")){//交出
            if (Regex.isTaiKey(scanResult)) {
                int flg = Integer.parseInt(scanResult.substring(0, 3));
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("barcode", scanResult);
                hashMap.put("isTransfer","Y");
                hashMap.put("changeflg","10");
                List<ThingsVo> list = thingsDao.quaryForDetail(hashMap);
                if (list != null && list.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.dontreced));
                } else {
                    if (thingsVoList != null && thingsVoList.size() > 0) {
                        for (int i = 0; i < thingsVoList.size(); i++) {
                            if (thingsVoList.get(i).getBarcode().equals(scanResult)) {
                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_key_isScan));
                                return;
                            }
                        }

                        ThingsVo thingsVo = new ThingsVo();
                        thingsVo.setType("40");
                        thingsVo.setBarcode(scanResult);
                        thingsVo.setRecvice(recvice);
                        thingsVo.setResult(result);
                        thingsVo.setIsScan("Y");
                        thingsVo.setClientid(clientid);
                        thingsVo.setIsTransfer("Y");
                        thingsVo.setReceiptor(receiptor);
                        thingsVo.setFlg(flg);
                        thingsVo.setChangeflg(changeflg);
                        thingsVo.setLineid(lineid);
                        thingsVo.setOperators(UtilsManager.getOperaterUsers(users));
                        thingsVo.setOperatedtime(Util.getNowDetial_toString());
                        thingsVoList.add(thingsVo);
                        key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, thingsVoList));
                    } else {
                        ThingsVo thingsVo = new ThingsVo();
                        thingsVo.setType("40");
                        thingsVo.setBarcode(scanResult);
                        thingsVo.setRecvice(recvice);
                        thingsVo.setIsScan("Y");
                        thingsVo.setClientid(clientid);
                        thingsVo.setResult(result);
                        thingsVo.setIsTransfer("Y");
                        thingsVo.setReceiptor(receiptor);
                        thingsVo.setFlg(flg);
                        thingsVo.setChangeflg(changeflg);
                        thingsVo.setLineid(lineid);
                        thingsVo.setOperatedtime(Util.getNowDetial_toString());
                        thingsVo.setOperators(UtilsManager.getOperaterUsers(users));
                        thingsVoList.add(thingsVo);
                        key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, thingsVoList));
                    }


                }

            } else if (Regex.isTaiPassWord(scanResult)) {
                int flg = Integer.parseInt(scanResult.substring(0, 3));
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("barcode", scanResult);
                hashMap.put("isTransfer","Y");
                hashMap.put("changeflg","10");
                List<ThingsVo> list = thingsDao.quaryForDetail(hashMap);
                if (list != null && list.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.dontreced));
                } else {
                    if (thingsVoList != null && thingsVoList.size() > 0) {
                        for (int i = 0; i < thingsVoList.size(); i++) {
                            if (thingsVoList.get(i).getBarcode().equals(scanResult)) {
                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_paw_isScan));
                                return;
                            }
                        }

                        ThingsVo thingsVo = new ThingsVo();
                        thingsVo.setType("50");
                        thingsVo.setBarcode(scanResult);
                        thingsVo.setRecvice(recvice);
                        thingsVo.setIsScan("Y");
                        thingsVo.setClientid(clientid);
                        thingsVo.setResult(result);
                        thingsVo.setIsTransfer("Y");
                        thingsVo.setReceiptor(receiptor);
                        thingsVo.setFlg(flg);
                        thingsVo.setChangeflg(changeflg);
                        thingsVo.setLineid(lineid);
                        thingsVo.setOperatedtime(Util.getNowDetial_toString());
                        thingsVo.setOperators(UtilsManager.getOperaterUsers(users));
                        thingsVoList.add(thingsVo);
                        key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, thingsVoList));
                    } else {
                        ThingsVo thingsVo = new ThingsVo();
                        thingsVo.setType("50");
                        thingsVo.setBarcode(scanResult);
                        thingsVo.setRecvice(recvice);
                        thingsVo.setIsScan("Y");
                        thingsVo.setClientid(clientid);
                        thingsVo.setResult(result);
                        thingsVo.setIsTransfer("Y");
                        thingsVo.setReceiptor(receiptor);
                        thingsVo.setFlg(flg);
                        thingsVo.setChangeflg(changeflg);
                        thingsVo.setLineid(lineid);
                        thingsVo.setOperators(UtilsManager.getOperaterUsers(users));
                        thingsVo.setOperatedtime(Util.getNowDetial_toString());
                        thingsVoList.add(thingsVo);
                        key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, thingsVoList));
                    }

                }
            } else {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_key_paw));
            }

        }else if(changeflg.equals("20")){ //接收
            if (Regex.isTaiKey(scanResult)) {
                int flg = Integer.parseInt(scanResult.substring(0, 3));
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("barcode", scanResult);
                hashMap.put("isTransfer","Y");
                hashMap.put("changeflg","20");
                List<ThingsVo> list = thingsDao.quaryForDetail(hashMap);
                if (list != null && list.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.receviced));
                } else {
                    if (thingsVoList != null && thingsVoList.size() > 0) {
                        for (int i = 0; i < thingsVoList.size(); i++) {
                            if (thingsVoList.get(i).getBarcode().equals(scanResult)) {
                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_key_isScan));
                                return;
                            }
                        }

                        ThingsVo thingsVo = new ThingsVo();
                        thingsVo.setType("40");
                        thingsVo.setBarcode(scanResult);
                        thingsVo.setRecvice(recvice);
                        thingsVo.setResult(result);
                        thingsVo.setIsScan("Y");
                        thingsVo.setClientid(clientid);
                        thingsVo.setIsTransfer("Y");
                        thingsVo.setReceiptor(receiptor);
                        thingsVo.setFlg(flg);
                        thingsVo.setChangeflg(changeflg);
                        thingsVo.setLineid(lineid);
                        thingsVo.setOperators(UtilsManager.getOperaterUsers(users));
                        thingsVo.setOperatedtime(Util.getNowDetial_toString());
                        thingsVoList.add(thingsVo);
                        key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, thingsVoList));
                    } else {
                        ThingsVo thingsVo = new ThingsVo();
                        thingsVo.setType("40");
                        thingsVo.setBarcode(scanResult);
                        thingsVo.setRecvice(recvice);
                        thingsVo.setIsScan("Y");
                        thingsVo.setClientid(clientid);
                        thingsVo.setResult(result);
                        thingsVo.setIsTransfer("Y");
                        thingsVo.setReceiptor(receiptor);
                        thingsVo.setFlg(flg);
                        thingsVo.setChangeflg(changeflg);
                        thingsVo.setLineid(lineid);
                        thingsVo.setOperators(UtilsManager.getOperaterUsers(users));
                        thingsVo.setOperatedtime(Util.getNowDetial_toString());
                        thingsVoList.add(thingsVo);
                        key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, thingsVoList));
                    }


                }

            } else if (Regex.isTaiPassWord(scanResult)) {
                int flg = Integer.parseInt(scanResult.substring(0, 3));
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("barcode", scanResult);
                hashMap.put("isTransfer","Y");
                hashMap.put("changeflg","10");
                List<ThingsVo> list = thingsDao.quaryForDetail(hashMap);
                if (list != null && list.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.dontreced));
                } else {
                    if (thingsVoList != null && thingsVoList.size() > 0) {
                        for (int i = 0; i < thingsVoList.size(); i++) {
                            if (thingsVoList.get(i).getBarcode().equals(scanResult)) {
                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_paw_isScan));
                                return;
                            }
                        }

                        ThingsVo thingsVo = new ThingsVo();
                        thingsVo.setType("50");
                        thingsVo.setBarcode(scanResult);
                        thingsVo.setRecvice(recvice);
                        thingsVo.setIsScan("Y");
                        thingsVo.setClientid(clientid);
                        thingsVo.setResult(result);
                        thingsVo.setIsTransfer("Y");
                        thingsVo.setReceiptor(receiptor);
                        thingsVo.setFlg(flg);
                        thingsVo.setChangeflg(changeflg);
                        thingsVo.setLineid(lineid);
                        thingsVo.setOperators(UtilsManager.getOperaterUsers(users));
                        thingsVo.setOperatedtime(Util.getNowDetial_toString());
                        thingsVoList.add(thingsVo);
                        key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, thingsVoList));
                    } else {
                        ThingsVo thingsVo = new ThingsVo();
                        thingsVo.setType("50");
                        thingsVo.setBarcode(scanResult);
                        thingsVo.setRecvice(recvice);
                        thingsVo.setIsScan("Y");
                        thingsVo.setClientid(clientid);
                        thingsVo.setResult(result);
                        thingsVo.setIsTransfer("Y");
                        thingsVo.setReceiptor(receiptor);
                        thingsVo.setFlg(flg);
                        thingsVo.setChangeflg(changeflg);
                        thingsVo.setLineid(lineid);
                        thingsVo.setOperators(UtilsManager.getOperaterUsers(users));
                        thingsVo.setOperatedtime(Util.getNowDetial_toString());
                        thingsVoList.add(thingsVo);
                        key_transfer_list.setAdapter(new MyKeyTransferAdpater(this, thingsVoList));
                    }

                }
            } else {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_key_paw));
            }
        }




    }

    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            Intent in = new Intent();
            TaiKey_Transfer.this.setResult(resultCode,in);
            finish();
        } else if (v == bt_updata_key_transfer) {
            if (thingsVoList != null && thingsVoList.size() > 0) {
                showSureUpDataDialog();
            } else {
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.chenk_key_paw));
            }

        }
    }

    public final class ViewHolder {
        public TextView tv_item_code;
        public TextView tv_item_status;
        public Button btn_item_delete;
    }

    public class MyKeyTransferAdpater extends BaseAdapter {

        //        private  Context mcontext;
        private LayoutInflater layoutInflater;
        private List<ThingsVo> key_scan_transfer;

        public MyKeyTransferAdpater(Context context, List<ThingsVo> keyList) {
//            mcontext = context;
            layoutInflater = LayoutInflater.from(context);
            key_scan_transfer = keyList;
        }

        @Override
        public int getCount() {
            return key_scan_transfer==null?0:key_scan_transfer.size();
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


            viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getBarcode());
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
        tv_tip.setText(getResources().getString(R.string.btn_delete_item));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                thingsVoList.remove(position);
                key_transfer_list.setAdapter(new MyKeyTransferAdpater(TaiKey_Transfer.this, thingsVoList));
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

    //确认提交
    private void showSureUpDataDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_sure_transfer));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                for (int i = 0; i < thingsVoList.size(); i++) {
                    thingsDao.create(thingsVoList.get(i));

                }


                //提交数据
                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                intent.putExtra("itemtype", "");//泰国没啥用  押运在用
                sendBroadcast(intent);



                Intent in = new Intent();
                TaiKey_Transfer.this.setResult(resultCode, in);
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


}
