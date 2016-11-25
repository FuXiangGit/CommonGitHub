package com.xvli.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.bean.LoginVo;
import com.xvli.bean.ThingsVo;
import com.xvli.comm.Config;
import com.xvli.dao.DatabaseHelper;
import com.xvli.dao.LoginDao;
import com.xvli.dao.ThingsDao;
import com.xvli.http.HttpProgressLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.pda.R;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 14:20.
 */
public class Article_CheckFragment extends LazyFragment {
    private LoginDao login_dao;
    private int type ,input,out;
    private TextView tv_total_number ,tv_ok_number;
    private Button bt_again_scan ,btn_key_change;
    private ListView car_down_fragment;
    private boolean isPrepared;
    private LoadingDialog dialogbinding;
    private DatabaseHelper databaseHelper;
    private List<LoginVo> users;
    private String  clientid;
    private Timer timer;
    private ThingsDao  thingsDao;
    private ArticleCheck articleCheck;

    public Article_CheckFragment(){

    }



    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
        PDALogger.d("save ===initData ");
        PDALogger.d("type ===initData "+type);
        PDALogger.d("input ===initData "+input);
        PDALogger.d("out ===initData "+out);
        initData(type, input, out);

    }

    @Override
    public void setArguments(Bundle args) {
        type = args.getInt("type");
        input = args.getInt("input");
        out = args.getInt("out");

        PDALogger.d("typeinputout==" +type + input +  out);
        //钥匙
        if(type == 0 && out ==1){//出库
            PDALogger.d("save === ");
            if(tv_total_number!=null){
                PDALogger.d("save === tv_total_number");
                PDALogger.d("type == "+type);
                PDALogger.d("out == " + out);
                PDALogger.d("input == "+input);
                intitData(type);
            }
        }else
        if(type == 0 && input ==2){
            PDALogger.d("tv_total_number ===--- " + input);
            if(tv_total_number!=null){
                PDALogger.d("tv_total_number === " + input);
                tv_total_number.setText(input + "input");//测试
                lazyLoad();
            }
        }

//        //密码
//        if(type == 1 && out ==1){//出库
//            if(tv_total_number!=null){
//                intitData(type);
//            }
//        }else
//        if(type == 1 && input ==2){
//            if(tv_total_number!=null){
//                tv_total_number.setText(input + "input");//测试
//                lazyLoad();
//            }
//        }
//
//        //工作手机
//        if(type == 2 && out ==1){//出库
//            if(tv_total_number!=null){
//                intitData(type);
//            }
//        }else
//        if(type == 2 && input ==2){
//            if(tv_total_number!=null){
//                tv_total_number.setText(input + "input");//测试
//                lazyLoad();
//            }
//        }
//
//        //枪支
//        if(type == 3 && out ==1){//出库
//            if(tv_total_number!=null){
//                intitData(type);
//            }
//        }else
//        if(type == 3 && input ==2){
//            if(tv_total_number!=null){
//                tv_total_number.setText(input + "input");//测试
//                lazyLoad();
//            }
//        }
//
//        //车辆钥匙
//        if(type == 4 && out ==1){//出库
//            if(tv_total_number!=null){
//                intitData(type);
//            }
//        }else
//        if(type == 4 && input ==2){
//            if(tv_total_number!=null){
//                tv_total_number.setText(input + "input");//测试
//                lazyLoad();
//            }
//        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.car_downfragment , container , false);
        bt_again_scan = (Button)view.findViewById(R.id.bt_again_scan);
        bt_again_scan.setVisibility(View.GONE);
        btn_key_change = (Button)view.findViewById(R.id.btn_key_change);
        btn_key_change.setVisibility(View.GONE);

        tv_total_number = (TextView)view.findViewById(R.id.tv_total_number);
        tv_ok_number = (TextView)view.findViewById(R.id.tv_ok_number);
        car_down_fragment = (ListView)view.findViewById(R.id.car_down_fragment);

        thingsDao= new ThingsDao( getHelper());
        login_dao = new LoginDao( getHelper());
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = UtilsManager.getClientid(users);
        }

        isPrepared = true;

        dialogbinding = new LoadingDialog(getActivity());
        PDALogger.d("lazyLoad");
        if(savedInstanceState!=null) {
            type = savedInstanceState.getInt("type");
            input = savedInstanceState.getInt("input");
            out = savedInstanceState.getInt("out");
            isPrepared = savedInstanceState.getBoolean("isPrepared");
            isVisible = savedInstanceState.getBoolean("isVisible");

            intitData(type);
        }else{
            lazyLoad();
        }


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        PDALogger.d("onStart");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("type", type);
        outState.putInt("input",input);
        outState.putInt("out",out);
        outState.putBoolean("isPrepared", isPrepared);
        outState.putBoolean("isVisible",isVisible);
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }



    //初始化下载数据
    private void  initData(int type ,int input,int out){
        PDALogger.d("downloader");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("clientid", clientid);
        hashMap.put("date", Util.getNow_toString());
        switch (type){
            case 0:
                //钥匙核对
                hashMap.put("flag", "40");
                DownLoaderData(input,out,hashMap,type);
                break;
            case 1:
                //密码核对
                hashMap.put("flag", "50");
                DownLoaderData(input, out, hashMap,type);
                break;
            case 2:
                //工作手机核对
                hashMap.put("flag", "20");
                DownLoaderData(input, out, hashMap,type);
                break;
            case 3:
                //枪支核对
                hashMap.put("flag", "10");
                DownLoaderData(input, out, hashMap,type);
                break;
            case 4:
                //车辆钥匙核对
                hashMap.put("flag", "30");
                DownLoaderData(input, out, hashMap,type);
                break;
        }


    }




    private  void  DownLoaderData(int input,int out ,HashMap<String ,String> hashMap ,final int type) {
        if(isFirstResume){//出库加载一次
            if (out == 1) { //出库
                XUtilsHttpHelper.getInstance().doPostProgress(Config.ARTICLE_CHECK, hashMap, new HttpProgressLoadCallback() {
                    @Override
                    public void onStart(Object startMsg) {
                        isLoading();
                    }

                    @Override
                    public void onSuccess(Object result) {
                        DownJsonSaveDb(result,type);
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isDownloading) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                        intitData(type);
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFinished(Object finishMsg) {
                        intitData(type);

                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }
                });

            }
        }


        if (input == 2){// 入库
            XUtilsHttpHelper.getInstance().doPostProgress(Config.ARTICLE_INPUT, hashMap, new HttpProgressLoadCallback() {
                @Override
                public void onStart(Object startMsg) {
                    isLoading();
                }

                @Override
                public void onSuccess(Object result) {

                    //重新下载数据  清除旧数据
                    PDALogger.d("ARTICLE_INPUT = " + result);



                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);

                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onFinished(Object finishMsg) {
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            });

        }


    }




    public class LoadingDialog extends Dialog {
        private TextView tv;

        public LoadingDialog(Context context) {
            super(context, R.style.loadingDialogStyle);
        }

        private LoadingDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_loading);
            tv = (TextView)this.findViewById(R.id.tv);
            tv.setText(getResources().getString(R.string.keypassword_load));
            LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.LinearLayout);
            linearLayout.getBackground().setAlpha(210);
        }
    }


    private void  isLoading(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what =0 ;
                mHandler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask ,0);

    }


    private boolean isFirstResume = true;
    @Override
    public void onResume()
    {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            PDALogger.d("getUserVisibleHint");
            lazyLoad();
        }

    }
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if(dialogbinding!= null){
                        dialogbinding.show();
                    }
                    break;
                case 1:
                    if(dialogbinding!=null){
                        dialogbinding.dismiss();
                    }
                    timer.cancel();
                    break;
            }
        }
    };


    //数据保存
    private  void  DownJsonSaveDb(Object json ,int type){

        PDALogger.d("Json = " + json);
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(json));
            String res = jsonObject.getString("isfailed");
            String data = jsonObject.getString("logisticsmeisai");
            if(res.equals("0")){
                if(!TextUtils.isEmpty(data)&& !data.equals("null")){
                    JSONArray array =new JSONArray(data);
                    saveDb(type, array);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 1;
            mHandler.sendMessage(msg);
        }


    }

    private void saveDb(int type , JSONArray array) {
        HashMap<String ,Object>  hsas= new HashMap<>();
        hsas.put("isTransfer","N");
        List<ThingsVo> thingsVos = thingsDao.quaryForDetail(hsas);
        JSONArray  jsonArray = new JSONArray();
        if (thingsVos != null && thingsVos.size() > 0) {
            //接口数据 如果表存在 保存状态 ，不存在新增
            for (int i = 0 ; i < array.length() ; i ++){
                try {
                    JSONObject object = array.getJSONObject(i);
                    String barcode = object.getString("barcode");
                    HashMap<String ,Object>  has = new HashMap<>();
                    has.put("barcode",barcode);
                    has.put("isTransfer","N");
                    has.put("outOrinput","Y");
                    switch (type) {
                        case 0:
                            //钥匙
                            has.put("type", "40");
                            break;
                        case 1:
                            //密码
                            has.put("type","50");
                            break;
                        case 2:
                            //工作手机
                            has.put("type", "20");
                            break;
                        case 3:
                            //枪支
                            has.put("type", "10");
                            break;
                        case 4:
                            //车辆钥匙
                            has.put("type", "30");
                            break;
                    }
                    List<ThingsVo> thingsVoList = thingsDao.quaryForDetail(has);
                    if(thingsVoList!=null && thingsVoList.size()>0 ){
                        if(thingsVoList.get(0).getIsScan().equals("Y")){
                            object.put("isScan" , "Y");

                        }else{
                            object.put("isScan" , "N");
                        }

                        if(thingsVoList.get(0).getIsUploaded().equals("Y")){
                            object.put("IsUploaded" , "Y");
                        }else{
                            object.put("IsUploaded" , "N");
                        }

                    }else{
                        object.put("isScan" , "N");
                    }

                    jsonArray.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                    return;
                }

            }
            //删除
            switch (type) {
                case 0:
                    //钥匙
                    thingsDao.deleteByType();
                    break;
                case 1:
                    //密码
                    thingsDao.deleteByType();
                    break;
                case 2:
                    //工作手机
                    thingsDao.deleteByType();
                    break;
                case 3:
                    //枪支
                    thingsDao.deleteByType();
                    break;
                case 4:
                    //车辆钥匙
                    thingsDao.deleteByType();
                    break;
            }

            //保存
            for(int i = 0 ; i < jsonArray.length() ; i++){
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ThingsVo thingsVo = new ThingsVo();
                    thingsVo.setId(object.getString("id"));
                    thingsVo.setIsScan(object.getString("isScan"));
                    thingsVo.setBarcode(object.getString("barcode"));
                    thingsVo.setLineid(object.getString("lineid"));
                    thingsVo.setLinename(object.getString("linename"));
                    thingsVo.setName(object.getString("name"));
                    thingsVo.setNotes(object.getString("notes"));
                    thingsVo.setState(object.getString("state"));
                    thingsVo.setOutOrinput("Y");
                    thingsVo.setIsTransfer("N");
                    thingsVo.setFlg(Integer.parseInt(object.getString("flg")));
                    thingsVo.setFlgnm(object.getString("flgnm"));
                    thingsVo.setClientid(clientid);
                    thingsVo.setReceiptor(object.getString("receiptor"));
                    thingsVo.setIsUploaded(object.getString("IsUploaded"));
                    switch (type) {
                        case 0:
                            //钥匙
                            thingsVo.setType("40");
                            break;
                        case 1:
                            //密码
                            thingsVo.setType("50");
                            break;
                        case 2:
                            //工作手机
                            thingsVo.setType("20");
                            break;
                        case 3:
                            //枪支
                            thingsVo.setType("10");
                            break;
                        case 4:
                            //车辆钥匙
                            thingsVo.setType("30");
                            break;
                    }
                    thingsDao.create(thingsVo);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                    return;
                }
            }
            intitData(type);

        } else {
            for (int i = 0; i < array.length(); i++) {
                //初始化
                try {
                    JSONObject object = array.getJSONObject(i);
                    ThingsVo thingsVo = new ThingsVo();
                    thingsVo.setId(object.getString("id"));
                    thingsVo.setIsScan("N");
                    thingsVo.setBarcode(object.getString("barcode"));
                    thingsVo.setLineid(object.getString("lineid"));
                    thingsVo.setLinename(object.getString("linename"));
                    thingsVo.setName(object.getString("name"));
                    thingsVo.setNotes(object.getString("notes"));
                    thingsVo.setState(object.getString("state"));
                    thingsVo.setOutOrinput("Y");
                    thingsVo.setClientid(clientid);
                    thingsVo.setIsTransfer("N");
                    thingsVo.setIsUploaded("N");
                    thingsVo.setFlg(Integer.parseInt(object.getString("flg")));
                    thingsVo.setReceiptor(object.getString("receiptor"));
                    thingsVo.setFlgnm(object.getString("flgnm"));
                    switch (type) {
                        case 0:
                            //钥匙
                            thingsVo.setType("40");
                            break;
                        case 1:
                            //密码
                            thingsVo.setType("50");
                            break;
                        case 2:
                            //工作手机
                            thingsVo.setType("20");
                            break;
                        case 3:
                            //枪支
                            thingsVo.setType("10");
                            break;
                        case 4:
                            //车辆钥匙
                            thingsVo.setType("30");
                            break;
                    }
                    thingsDao.create(thingsVo);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                    return;
                }
            }
            intitData(type);
        }

    }


    public class ArticleCheck extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<ThingsVo> key_scan_transfer;


        public ArticleCheck(Context context, List<ThingsVo> keyList) {
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
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_2);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_3);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            if (key_scan_transfer.get(position).getIsTransfer().equals("Y")) {//交接物品
                viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getBarcode());
                viewHolder.tv_type.setText("");
                if (key_scan_transfer.get(position).getChangeflg().equals("10")) {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.Have_surrendered));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                } else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.received));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }

            }else{
                viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getLinename());
                viewHolder.tv_type.setText(key_scan_transfer.get(position).getName());
                if (key_scan_transfer.get(position).getIsScan().equals("Y")) {
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


    public final class ViewHolder {
        public TextView tv_item_code;
        public TextView tv_item_status;
        public TextView tv_type;
    }

    //Tab 切换 获取出库数据
    private void  intitData(int type){

        PDALogger.d("save == intitData --------------");

        HashMap<String ,Object> hashMap = new HashMap<>();
//        switch (type){
//            case 0:
//                hashMap.put("type","40");
//                break;
//            case 1:
//                hashMap.put("type","50");
//                break;
//            case 2:
//                hashMap.put("type","20");
//                break;
//            case 3:
//                hashMap.put("type","10");
//                break;
//            case 4:
//                hashMap.put("type","30");
//                break;
//
//        }

        hashMap.put("isTransfer","N");
        hashMap.put("outOrinput","Y");
        List<ThingsVo> thingsVos = thingsDao.quaryWithOrderByLists(hashMap);
        //获取交接钥匙 密码
        HashMap<String,Object> has =new HashMap<>();
        has.put("isTransfer", "Y");
        List<ThingsVo> thingsVosRec = thingsDao.quaryForDetail(has);
        if(thingsVosRec!=null && thingsVosRec.size()>0){
            if(thingsVos!=null && thingsVos.size()>0){
            }else{
                thingsVos = new ArrayList<>();
            }

            for(ThingsVo thingsVo : thingsVosRec){
                thingsVos.add(thingsVo);
            }

        }else{
            if(thingsVos!=null && thingsVos.size()>0){
            }else{
                thingsVos = new ArrayList<>();
            }
        }




        articleCheck = new ArticleCheck(getActivity(),thingsVos);
        car_down_fragment.setAdapter(articleCheck);

        tv_total_number.setText(String.valueOf(thingsVos == null ? 0 :thingsVos.size()));
        //完成数
        hashMap.put("isScan", "Y");
        List<ThingsVo> thingsVoList = thingsDao.quaryForDetail(hashMap);
        tv_ok_number.setText(String.valueOf((thingsVoList==null?0:thingsVoList.size())+(thingsVosRec == null?0:thingsVosRec.size())));


    }




}
