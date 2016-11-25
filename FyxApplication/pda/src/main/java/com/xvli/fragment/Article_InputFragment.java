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

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 14:37.
 */
public class Article_InputFragment  extends  LazyFragment{

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


    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
        HashMap<String ,String > has = new HashMap<>();
        has.put("clientid", clientid);
        has.put("date", Util.getNow_toString());
        DownLoaderData(has);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.car_downfragment, container, false);
        bt_again_scan = (Button) view.findViewById(R.id.bt_again_scan);
        bt_again_scan.setVisibility(View.GONE);
        btn_key_change = (Button) view.findViewById(R.id.btn_key_change);
        btn_key_change.setVisibility(View.GONE);

        tv_total_number = (TextView) view.findViewById(R.id.tv_total_number);
        tv_ok_number = (TextView) view.findViewById(R.id.tv_ok_number);
        car_down_fragment = (ListView) view.findViewById(R.id.car_down_fragment);

        thingsDao = new ThingsDao(getHelper());
        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = UtilsManager.getClientid(users);
        }

        isPrepared = true;

        dialogbinding = new LoadingDialog(getActivity());
        PDALogger.d("lazyLoad");
        if (savedInstanceState != null) {


        } else {
            lazyLoad();
        }


        return view;
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
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


    private  void  DownLoaderData(HashMap<String ,String> hashMap ) {

            XUtilsHttpHelper.getInstance().doPostProgress(Config.ARTICLE_INPUT, hashMap, new HttpProgressLoadCallback() {
                @Override
                public void onStart(Object startMsg) {
                    isLoading();
                }

                @Override
                public void onSuccess(Object result) {

                    //重新下载数据  清除旧数据
                    PDALogger.d("ARTICLE_INPUT = " + result);



                    DownJsonSaveDb(result);

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

    //入库
    private void  intitData(){

        PDALogger.d("save == intitData --------------");

        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("outOrinput","N");
        List<ThingsVo> thingsVos = thingsDao.quaryWithOrderByLists(hashMap);

        articleCheck = new ArticleCheck(getActivity(),thingsVos);
        car_down_fragment.setAdapter(articleCheck);

        tv_total_number.setText(String.valueOf(thingsVos == null ? 0 :thingsVos.size()));
        //完成数
        hashMap.put("isScan" , "Y");
        List<ThingsVo> thingsVoList = thingsDao.quaryForDetail(hashMap);
        tv_ok_number.setText(String.valueOf(thingsVoList==null?0:thingsVoList.size()));

    }

    //数据保存
    private  void  DownJsonSaveDb(Object json ){

        PDALogger.d("Json = " + json);
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(json));
            String res = jsonObject.getString("isfailed");
            String data = jsonObject.getString("logisticsmeisai");
            if(res.equals("0")){
                if(!TextUtils.isEmpty(data)&& !data.equals("null")){
                    thingsDao. deleteByInput();//清除数据
                    JSONArray array =new JSONArray(data);
                    saveDb(array);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 1;
            mHandler.sendMessage(msg);
        }


    }


    private void saveDb( JSONArray array) {
            for (int i = 0; i < array.length(); i++) {
                //初始化
                try {
                    JSONObject object = array.getJSONObject(i);
                    ThingsVo thingsVo = new ThingsVo();
                    thingsVo.setId(object.getString("id"));
                    thingsVo.setIsScan("Y");
                    thingsVo.setBarcode(object.getString("barcode"));
                    thingsVo.setLineid(object.getString("lineid"));
                    thingsVo.setLinename(object.getString("linename"));
                    thingsVo.setName(object.getString("name"));
                    thingsVo.setNotes(object.getString("notes"));
//                    thingsVo.setState(object.getString("state"));
                    thingsVo.setOutOrinput("N");
                    thingsVo.setClientid(clientid);
                    thingsVo.setFlg(Integer.parseInt(object.getString("flg")));
                    thingsVo.setFlgnm(object.getString("flgnm"));
                    thingsDao.create(thingsVo);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                    return;
                }
            }
            intitData();


    }


}
