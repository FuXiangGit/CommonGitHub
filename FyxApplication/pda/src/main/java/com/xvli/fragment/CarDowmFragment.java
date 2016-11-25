package com.xvli.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.CarUpDownVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Config;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmUpDownItemVoDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.CarUpDownVoDao;
import com.xvli.dao.DatabaseHelper;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.pda.R;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 19:50.
 */
@SuppressLint("ValidFragment")
public class CarDowmFragment extends Fragment {

    private String weburl;
    private String branchid ,scanResultintent ,branch ,Customer ,atmid ;
    private DatabaseHelper databaseHelper;
    private AtmBoxBagDao atmBoxBagDao;
    private List<AtmBoxBagVo> atmBoxBagVoList = new ArrayList<AtmBoxBagVo>();
    private AtmUpDownItemVoDao atmUpDownItemVoDao;
    private UniqueAtmDao uniqueAtmDao;
    private List<UniqueAtmVo>  uniqueAtmVos = new ArrayList<>();
    private List<AtmUpDownItemVo> atmUpDownItemVoList = new ArrayList<AtmUpDownItemVo>();
    private CarUpDownVoDao carUpDownVoDao;
//    private View view;
    private ListView  listView;
    private TextView test;
    private OperateLogVo_Dao operateLogVoDao ;//操作日志
    private List<OperateLogVo> operateLogVoList = new ArrayList<OperateLogVo>();//操作日志
    private List<CarUpDownVo> carUpDownVoList0;
    private List<CarUpDownVo> carUpDownVoList1;
    private TextView tv_total_number,tv_ok_number;
    private Button bt_again_scan ,btn_key_change;
    private List<BranchVo> branchVoList = new ArrayList<BranchVo>();
    private BranchVoDao branchVoDao;//网点
    private List<ArrayList<CarUpDownVo>> arrayListsCarDown  ;
    private AtmMoneyDao atmMoneyDao;
    private List<AtmmoneyBagVo> atmmoneyBagVos;

    public CarDowmFragment() {
    }

    public CarDowmFragment(DatabaseHelper database) {
        this.databaseHelper = database;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void setArguments(Bundle bundle) {//接收传入的数据

//        bundle.putString("customer","DIEBOLD")
        if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
            scanResultintent = bundle.getString("scanResultintent");
            atmid = bundle.getString("atmid");
            if(atmid != null &&tv_total_number != null && tv_ok_number != null){
                initShowDataTai(atmid,scanResultintent);
            }

        } else { // 押运 迪堡
            Customer = bundle.getString("customer");
            branchid = bundle.getString("branchid");
            scanResultintent = bundle.getString("scanResultintent");
            if (branchid != null && tv_total_number != null && tv_ok_number != null) {
//                PDALogger.d("atmUpDownItemVoDao INIT= " + atmUpDownItemVoDao);
//                PDALogger.d("branchid INIT----- " + branchid);
                if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                    initShowDataDiebold(branchid, scanResultintent);
                } else {
                    initShowData(branchid, scanResultintent);
                }

            }
//            PDALogger.d("branchid INIT= " + branchid);
//            PDALogger.d("listView = " + listView);
//            PDALogger.d("tv_total_number = " + tv_total_number);
//            PDALogger.d("tv_ok_number = " + tv_ok_number);
            /*if (test != null) {
                test.setText(branchid);
            }*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.car_downfragment , container , false);
        bt_again_scan = (Button)view.findViewById(R.id.bt_again_scan);
        bt_again_scan.setVisibility(View.GONE);
        btn_key_change = (Button)view.findViewById(R.id.btn_key_change);
        btn_key_change.setVisibility(View.GONE);
        initView(view);
        PDALogger.d("onCreateView");
        return view;
    }

    private  void initView(View view){
        operateLogVoDao = new OperateLogVo_Dao(databaseHelper);
        atmBoxBagDao = new AtmBoxBagDao(databaseHelper);
        atmUpDownItemVoDao  = new AtmUpDownItemVoDao(databaseHelper);
        carUpDownVoDao = new CarUpDownVoDao(databaseHelper);
        branchVoDao = new BranchVoDao(databaseHelper);
        uniqueAtmDao = new UniqueAtmDao(databaseHelper);
        atmMoneyDao = new AtmMoneyDao(databaseHelper);
        tv_total_number = (TextView)view.findViewById(R.id.tv_total_number);
        tv_ok_number = (TextView)view.findViewById(R.id.tv_ok_number);
        listView = (ListView)view.findViewById(R.id.car_down_fragment);
//        PDALogger.d("branchid = INIT" + branchid);
        if (new Util().setKey().equals(Config.NAME_THAILAND)){ //泰国
            if(atmid!=null) {
                initShowDataTai(atmid,scanResultintent);
            }

        }else{
            if(branchid!=null) {
                if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                    initShowDataDiebold(branchid, scanResultintent);

                } else{//押运
                    initShowData(branchid, scanResultintent);
                }
            }
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        PDALogger.d("onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        PDALogger.d("onPause");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        PDALogger.d("onViewCreated");

    }


    //迪堡 初始化数据
    private void initShowDataDiebold(String branchid ,String scanResultintent){
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);//是否有上车记录

        HashMap<String,Object>  hashMap = new HashMap<>();
        hashMap.put("branchid",branchid);
        atmmoneyBagVos = atmMoneyDao.quaryForDetail(hashMap);//钞包，钞袋
        if(atmmoneyBagVos!=null && atmmoneyBagVos.size()>0){
            for (int i = 0 ; i <atmmoneyBagVos.size();i++ ){
                HashMap<String ,Object> hash = new HashMap<>();
                hash.put("itemtype","1");
                hash.put("barcode",atmmoneyBagVos.get(i).getBarcode());
                hash.put("isYouXiao","Y");
                atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hash);
                if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                    if(atmUpDownItemVoList.get(atmUpDownItemVoList.size()-1).getOperatetype().equals("UP")){
                        atmmoneyBagVos.remove(i);
                        i--;
                    }
                }
            }
        }


        listView.setAdapter(new CarDownAdpaterDiebold(getActivity(), OrderByDiebold(atmmoneyBagVos)));
        tv_total_number.setText(String.valueOf(atmmoneyBagVos==null?0:atmmoneyBagVos.size()));


        uniqueAtmVos = uniqueAtmDao.queryAll();
        arrayListsCarDown = new ArrayList<>();
        if(operateLogVoList != null && operateLogVoList.size() > 0){
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                for(int i  = 0  ; i < uniqueAtmVos.size() ; i++){
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                            "branchid", branchid, "enabled", "Y", "operatetype", "OFF", "moneyBag",uniqueAtmVos.get(i).getMoneyBag());
                    if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        arrayListsCarDown.add((ArrayList)carUpDownVoList);
                    }
                }


            }

            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                    "branchid", branchid, "enabled", "Y", "operatetype", "OFF", "itemtype","1");
            int count = arrayListsCarDown==null?0:arrayListsCarDown.size();
            int countbag = carUpDownVoList==null?0:carUpDownVoList.size();

            tv_ok_number.setText(String.valueOf(count+countbag));

        }else{
//            PDALogger.d("branchid=="+branchid);
            if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                for(int i  = 0  ; i < uniqueAtmVos.size() ; i++){
                    HashMap<String ,Object> hashM = new HashMap<>();
                    hashM.put("branchid", branchid);
                    hashM.put("enabled", "Y");
                    hashM.put("operatetype", "OFF");
                    hashM.put("moneyBag", uniqueAtmVos.get(i).getMoneyBag());
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashM);
                    if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        arrayListsCarDown.add((ArrayList)carUpDownVoList);
                    }
                }

            }

            HashMap<String ,Object> has = new HashMap<>();
            has.put("branchid", branchid);
            has.put("enabled", "Y");
            has.put("operatetype", "OFF");
            has.put("itemtype","1");
            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(has);
            int count = arrayListsCarDown==null?0:arrayListsCarDown.size();
            int countbag = carUpDownVoList==null?0:carUpDownVoList.size();

            tv_ok_number.setText(String.valueOf(count+countbag));

        }

    }




    //初始化数据
    private void initShowData(String branchid ,String scanResultintent ) {
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if (branchid.equals("-1")) {
            if (operateLogVoList != null && operateLogVoList.size() > 0) {
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                        "branchid", "-1", "enabled", "Y", "operatetype", "OFF");
                listView.setAdapter(new MyKeyTransferAdpaterOther(getActivity(), carUpDownVoList));
                tv_total_number.setText(String.valueOf(carUpDownVoList==null?0:carUpDownVoList.size()));
                tv_ok_number.setText(String.valueOf(carUpDownVoList==null?0:carUpDownVoList.size()));
            } else {
                HashMap<String, Object> hash = new HashMap<String, Object>();
                hash.put("branchid", "-1");
                hash.put("enabled", "Y");
                hash.put("operatetype", "OFF");
                carUpDownVoList0 = carUpDownVoDao.quaryForDetail(hash);
                listView.setAdapter(new MyKeyTransferAdpaterOther(getActivity(), carUpDownVoList0));
                tv_total_number.setText(String.valueOf(carUpDownVoList0==null?0:carUpDownVoList0.size()));
                tv_ok_number.setText(String.valueOf(carUpDownVoList0==null?0:carUpDownVoList0.size()));
            }
        } else {
//            PDALogger.d("branchid = branchid" + branchid);
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("branchid", branchid);
            atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
            if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                for (int i = 0; i < atmBoxBagVoList.size(); i++) {
                    HashMap<String, Object> hasM = new HashMap<>();
                    hasM.put("branchid", branchid);
                    hasM.put("barcode", atmBoxBagVoList.get(i).getBarcodeno());
                    List<AtmUpDownItemVo> atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hasM);
                    if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                        if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {
                            atmBoxBagVoList.remove(i);
                            i--;
                        }
                    } else {
                        HashMap<String,Object> hashMap1 = new HashMap<>();
                        hashMap1.put("sendOrRecycle", 1);
                        hashMap1.put("barcodeno", atmBoxBagVoList.get(i).getBarcodeno());
                        List<AtmBoxBagVo> boxBagVos = atmBoxBagDao.quaryForDetail(hashMap1);
                        if(boxBagVos != null && boxBagVos.size() > 0){
                            atmBoxBagVoList.remove(i);
                            i--;
                        }
                    }
                }
                atmBoxBagVoList = OrderByScan(atmBoxBagVoList);
                listView.setAdapter(new MyKeyTransferAdpater(getActivity(), atmBoxBagVoList, scanResultintent));
                tv_total_number.setText(String.valueOf(atmBoxBagVoList==null?0:atmBoxBagVoList.size()));

                if (operateLogVoList != null && operateLogVoList.size() > 0) {
                    String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                            "branchid", branchid, "enabled", "Y", "operatetype", "OFF");
                    tv_ok_number.setText(String.valueOf(carUpDownVoList==null?0:carUpDownVoList.size()));

                } else {
                    HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
                    hashMap1.put("branchid", branchid);
                    hashMap1.put("enabled", "Y");
                    hashMap1.put("operatetype", "OFF");
                    carUpDownVoList0 = carUpDownVoDao.quaryForDetail(hashMap1);
                    tv_ok_number.setText(String.valueOf(carUpDownVoList0==null?0:carUpDownVoList0.size()));
                }


            } else {
                HashMap<String, Object> hash = new HashMap<String, Object>();
                hash.put("branchid", "-1");
                hash.put("enabled", "Y");
                hash.put("operatetype", "OFF");
                carUpDownVoList0 = carUpDownVoDao.quaryForDetail(hash);
                listView.setAdapter(new MyKeyTransferAdpaterOther(getActivity(), carUpDownVoList0));
                tv_total_number.setText(String.valueOf(carUpDownVoList0==null?0:carUpDownVoList0.size()));
                tv_ok_number.setText(String.valueOf(carUpDownVoList0==null?0:carUpDownVoList0.size()));
            }
        }
    }

    //其他
    public class MyKeyTransferAdpaterOther extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<CarUpDownVo> key_scan_transfer;


        public MyKeyTransferAdpaterOther(Context context, List<CarUpDownVo> keyList) {
//            mcontext = context;
            layoutInflater = LayoutInflater.from(context);
            key_scan_transfer = keyList;

        }

        @Override
        public int getCount() {
            return key_scan_transfer == null ? 0:key_scan_transfer.size();
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
                convertView = layoutInflater.inflate(R.layout.item_barcode_scan, null);
                viewHolder.tv_item_code = (TextView) convertView.findViewById(R.id.tv_item_1);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_3);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_2);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getBarCode());
            if(key_scan_transfer.get(position).getItemtype().equals("0")){
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_1));
                viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));

            }else if(key_scan_transfer.get(position).getItemtype().equals("1")){
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_2));
                viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));


            }
            return convertView;
        }


    }

    public final class ViewHolder {
        public TextView tv_item_code;
        public TextView tv_item_status;
        public TextView tv_type;
    }

    public class MyKeyTransferAdpater extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<AtmBoxBagVo> key_scan_transfer;
        private String scanResultintent;

        public MyKeyTransferAdpater(Context context, List<AtmBoxBagVo> keyList ,String scanResult) {
//            mcontext = context;
            layoutInflater = LayoutInflater.from(context);
            key_scan_transfer = keyList;
            scanResultintent = scanResult;
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
                convertView = layoutInflater.inflate(R.layout.item_barcode_scan, null);
                viewHolder.tv_item_code = (TextView) convertView.findViewById(R.id.tv_item_1);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_3);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_2);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getBarcodeno());
            if(operateLogVoList!=null && operateLogVoList.size()>0){
                if(key_scan_transfer.get(position).getBagtype() == 0){
                    viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_1));
                    String time =  operateLogVoList.get(operateLogVoList.size()-1).getOperatetime();
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(), "barCode",
                            key_scan_transfer.get(position).getBarcodeno(), "enabled", "Y", "operatetype","OFF");
                    if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                    }else {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                    }
                }else if(key_scan_transfer.get(position).getBagtype() == 1){
                    viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_2));
                    String time =  operateLogVoList.get(operateLogVoList.size()-1).getOperatetime();
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(), "barCode",
                            key_scan_transfer.get(position).getBarcodeno(), "enabled", "Y", "operatetype","OFF");
                    if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                    }else {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                    }
                }

            }else {
                if (key_scan_transfer.get(position).getBagtype() == 0) {
                    viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_1));
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("barCode", key_scan_transfer.get(position).getBarcodeno());
                    hashMap.put("enabled", "Y");
                    hashMap.put("operatetype","OFF");
                    carUpDownVoList0 = carUpDownVoDao.quaryForDetail(hashMap);
                    if (carUpDownVoList0 != null && carUpDownVoList0.size() > 0) {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                    }
                } else if (key_scan_transfer.get(position).getBagtype() == 1) {
                    viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_2));
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("barCode", key_scan_transfer.get(position).getBarcodeno());
                    hashMap.put("enabled" ,"Y");
                    hashMap.put("operatetype","OFF");
                    carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hashMap);
                    if (carUpDownVoList1!= null && carUpDownVoList1.size() > 0) {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                    }

                }
            }
            return convertView;
        }


    }


    //迪堡招行
    public class CarDownAdpaterDiebold extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<AtmmoneyBagVo> key_scan_transfer;


        public CarDownAdpaterDiebold(Context context, List<AtmmoneyBagVo> keyList) {

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
                convertView = layoutInflater.inflate(R.layout.item_barcode_scan, null);
                viewHolder.tv_item_code = (TextView) convertView.findViewById(R.id.tv_item_1);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_3);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_2);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            if (operateLogVoList != null && operateLogVoList.size() > 0) {
                if(key_scan_transfer.get(position).getBagtype()==6){
                    viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getMoneyBag());
                    viewHolder.tv_type.setText(getResources().getString(R.string.chao_bag));
                    String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(), "moneyBag",
                            key_scan_transfer.get(position).getMoneyBag(), "enabled", "Y", "operatetype", "OFF");
                    if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                    }
                }else{
                    viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getBarcode());
                    viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_2));
                    String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(), "barCode",
                            key_scan_transfer.get(position).getBarcode(), "enabled", "Y", "operatetype", "OFF");
                    if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                    }
                }




            } else {
                if(key_scan_transfer.get(position).getBagtype()==6){
                    viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getMoneyBag());
                    viewHolder.tv_type.setText(getResources().getString(R.string.chao_bag));
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("branchid", branchid);
                    hashMap.put("enabled", "Y");
                    hashMap.put("operatetype", "OFF");
                    hashMap.put("moneyBag", key_scan_transfer.get(position).getMoneyBag());
                    List<CarUpDownVo> carUpDownVoList0 = carUpDownVoDao.quaryForDetail(hashMap);

                    if (carUpDownVoList0 != null && carUpDownVoList0.size() > 0) {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                    }

                }else{
                    viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getBarcode());
                    viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_2));
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("branchid", branchid);
                    hashMap.put("enabled", "Y");
                    hashMap.put("operatetype", "OFF");
                    hashMap.put("barCode", key_scan_transfer.get(position).getBarcode());
                    List<CarUpDownVo> carUpDownVoList0 = carUpDownVoDao.quaryForDetail(hashMap);

                    if (carUpDownVoList0 != null && carUpDownVoList0.size() > 0) {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                        viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                    }
                }


            }
            return convertView;
        }


    }









    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public List<AtmBoxBagVo>  OrderByScan(List<AtmBoxBagVo>  atmBoxBagVos){
        if(atmBoxBagVos!=null && atmBoxBagVos.size()>0) {
            if (operateLogVoList != null && operateLogVoList.size() > 0) {
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                List<AtmBoxBagVo> atmBoxBagVos1 = new ArrayList<>();
                for (int i = 0; i < atmBoxBagVos.size(); i++) {
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(), "barCode",
                            atmBoxBagVos.get(i).getBarcodeno(), "enabled", "Y", "operatetype", "OFF");
                    if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                        atmBoxBagVos1.add(atmBoxBagVos.get(i));
                        atmBoxBagVos.remove(atmBoxBagVos.get(i));
                        i--;
                    }
                }

                if (atmBoxBagVos1 != null && atmBoxBagVos1.size() > 0) {
                    for (int j = 0; j < atmBoxBagVos1.size(); j++) {
                        atmBoxBagVos.add(atmBoxBagVos1.get(j));
                    }
                }

            } else {
                    List<AtmBoxBagVo> atmBoxBagVos1 = new ArrayList<>();
                    for (int i = 0; i < atmBoxBagVos.size(); i++) {
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("barCode", atmBoxBagVos.get(i).getBarcodeno());
                        hashMap.put("enabled", "Y");
                        hashMap.put("operatetype", "OFF");
                        carUpDownVoList0 = carUpDownVoDao.quaryForDetail(hashMap);
                        if (carUpDownVoList0 != null && carUpDownVoList0.size() > 0) {
                            atmBoxBagVos1.add(atmBoxBagVos.get(i));
                            atmBoxBagVos.remove(atmBoxBagVos.get(i));
                            i--;
                        }
                    }
                    if (atmBoxBagVos1 != null && atmBoxBagVos1.size() > 0) {
                        for (int j = 0; j < atmBoxBagVos1.size(); j++) {
                            atmBoxBagVos.add(atmBoxBagVos1.get(j));
                        }
                    }

                }
        }
        return atmBoxBagVos;
    }


    //迪堡 钞包排序
    private List<AtmmoneyBagVo> OrderByDiebold(List<AtmmoneyBagVo> list){
        List<AtmmoneyBagVo>  list1 = new ArrayList<>();
        if(operateLogVoList != null && operateLogVoList.size() > 0){
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            if(list!=null && list.size()>0){
                for(int i  = 0  ; i < list.size() ; i++){

                    if(list.get(i).getBagtype() == 6){
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                                "branchid", branchid, "enabled", "Y", "operatetype", "OFF", "moneyBag",list.get(i).getMoneyBag());
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        }else{
                            list1.add(list.get(i));
                        }
                    }else{
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                                "branchid", branchid, "enabled", "Y", "operatetype", "OFF", "barCode",list.get(i).getBarcode());
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        }else{
                            list1.add(list.get(i));
                        }
                    }

                }

                for(int i  = 0  ; i < list.size() ; i++){

                    if(list.get(i).getBagtype() == 6){
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                                "branchid", branchid, "enabled", "Y", "operatetype", "OFF", "moneyBag",list.get(i).getMoneyBag());
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                            list1.add(list.get(i));
                        }
                    }else{
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                                "branchid", branchid, "enabled", "Y", "operatetype", "OFF", "barCode",list.get(i).getBarcode());
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                            list1.add(list.get(i));
                        }
                    }

                }

            }

        }else{
            if(list!=null && list.size()>0){
                for(int i  = 0  ; i < list.size() ; i++){
                    if(list.get(i).getBagtype() == 6){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("branchid", branchid);
                        hashMap.put("enabled", "Y");
                        hashMap.put("operatetype", "OFF");
                        hashMap.put("moneyBag", list.get(i).getMoneyBag());
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        }else{
                            list1.add(list.get(i));
                        }
                    }else{
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("branchid", branchid);
                        hashMap.put("enabled", "Y");
                        hashMap.put("operatetype", "OFF");
                        hashMap.put("barCode", list.get(i).getBarcode());
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        }else{
                            list1.add(list.get(i));
                        }
                    }


                }

                for(int i  = 0  ; i < list.size() ; i++){

                    if(list.get(i).getBagtype() == 6){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("branchid", branchid);
                        hashMap.put("enabled", "Y");
                        hashMap.put("operatetype", "OFF");
                        hashMap.put("moneyBag", list.get(i).getMoneyBag());
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                            list1.add(list.get(i));
                        }
                    }else{
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("branchid", branchid);
                        hashMap.put("enabled", "Y");
                        hashMap.put("operatetype", "OFF");
                        hashMap.put("barCode", list.get(i).getBarcode());
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                            list1.add(list.get(i));
                        }
                    }

                }

            }

        }

     return  list1;

    }

    /*
    -------------------------------------------泰国--------------------------------------------------
     */
    private  void  initShowDataTai(String atmid , String scanResultintent){
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);//是否有上车记录

        HashMap<String,Object>  hashMap = new HashMap<>();
        hashMap.put("atmid",atmid);
        atmmoneyBagVos = atmMoneyDao.quaryForDetail(hashMap);//扎袋
        if(operateLogVoList!=null && operateLogVoList.size()>0){
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            if(atmmoneyBagVos!=null && atmmoneyBagVos.size()>0){
                for (int i = 0 ; i <atmmoneyBagVos.size() ;i++){
                    HashMap<String ,Object> Hhas = new HashMap<>();
                    Hhas.put("barCode",atmmoneyBagVos.get(i).getBarcode());
                    Hhas.put("itemtype", "5");
                    Hhas.put("enabled","Y");
                    List<CarUpDownVo> carUpDownVos = carUpDownVoDao.quaryForDetail(Hhas);
                    if(carUpDownVos!=null && carUpDownVos.size()>0){
                        if(carUpDownVos.get(carUpDownVos.size()-1).getOperatetype().equals("ON")){
                        }else{
                            atmmoneyBagVos.remove(i);
                            i--;
                        }
                    }
                }
                listView.setAdapter(new CarDownAdpaterTai(getActivity(), OrderByTai(atmmoneyBagVos)));
                tv_total_number.setText(String.valueOf(atmmoneyBagVos==null?0:atmmoneyBagVos.size()));

                List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                        "atmid", atmid, "enabled", "Y", "operatetype", "OFF");

                tv_ok_number.setText(String.valueOf(carUpDownVoList==null?0:carUpDownVoList.size()));
            }




        }else{
            if(atmmoneyBagVos!=null && atmmoneyBagVos.size()>0){
                listView.setAdapter(new CarDownAdpaterTai(getActivity(), OrderByTai(atmmoneyBagVos)));
                tv_total_number.setText(String.valueOf(atmmoneyBagVos.size()));
                //完成物品数
                HashMap<String, Object> has = new HashMap<>();
                has.put("atmid", atmid);
                has.put("enabled", "Y");
                has.put("operatetype", "OFF");
                List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(has);
                tv_ok_number.setText(String.valueOf( carUpDownVoList==null?0:carUpDownVoList.size()));
            }
        }


    }


    private List<AtmmoneyBagVo> OrderByTai(List<AtmmoneyBagVo> list){
        List<AtmmoneyBagVo>  list1 = new ArrayList<>();
        if(operateLogVoList != null && operateLogVoList.size() > 0){
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            if(list!=null && list.size()>0){
                for(int i  = 0  ; i < list.size() ; i++){

                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                                "atmid", atmid, "enabled", "Y", "operatetype", "OFF", "barCode",list.get(i).getBarcode());
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        }else{
                            list1.add(list.get(i));
                        }
                }

                for(int i  = 0  ; i < list.size() ; i++){

                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                                "atmid", atmid, "enabled", "Y", "operatetype", "OFF", "barCode",list.get(i).getBarcode());
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                            list1.add(list.get(i));
                        }
                }

            }

        }else{
            if(list!=null && list.size()>0){
                for(int i  = 0  ; i < list.size() ; i++){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("atmid", atmid);
                        hashMap.put("enabled", "Y");
                        hashMap.put("operatetype", "OFF");
                        hashMap.put("barCode", list.get(i).getBarcode());
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        }else{
                            list1.add(list.get(i));
                        }
                }

                for(int i  = 0  ; i < list.size() ; i++){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("atmid", atmid);
                        hashMap.put("enabled", "Y");
                        hashMap.put("operatetype", "OFF");
                        hashMap.put("barCode", list.get(i).getBarcode());
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                            list1.add(list.get(i));
                        }

                }

            }

        }

        return  list1;

    }



    //迪堡招行
    public class CarDownAdpaterTai extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<AtmmoneyBagVo> key_scan_transfer;


        public CarDownAdpaterTai(Context context, List<AtmmoneyBagVo> keyList) {

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
                convertView = layoutInflater.inflate(R.layout.item_barcode_scan, null);
                viewHolder.tv_item_code = (TextView) convertView.findViewById(R.id.tv_item_1);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_3);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_2);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            if (operateLogVoList != null && operateLogVoList.size() > 0) {
                viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getBarcode());
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_4));
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(), "barCode",
                        key_scan_transfer.get(position).getBarcode(), "enabled", "Y", "operatetype", "OFF");
                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                } else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }

            } else {
                viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getBarcode());
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_4));
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("atmid", atmid);
                hashMap.put("enabled", "Y");
                hashMap.put("operatetype", "OFF");
                hashMap.put("barCode", key_scan_transfer.get(position).getBarcode());
                List<CarUpDownVo> carUpDownVoList0 = carUpDownVoDao.quaryForDetail(hashMap);

                if (carUpDownVoList0 != null && carUpDownVoList0.size() > 0) {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                } else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }

            }
            return convertView;
        }


    }



}
