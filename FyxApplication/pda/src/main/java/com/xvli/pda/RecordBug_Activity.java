package com.xvli.pda;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xvli.adapter.CustomAdapter;
import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.SiginPhotoVo;
import com.xvli.bean.TmrBankFaultVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.IsRepairDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.SiginPhotoDao;
import com.xvli.dao.TmrBankFaultVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.WedgrTime_Picker;
import com.xvli.widget.WritePadDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 故障登记界面
 */
public class RecordBug_Activity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

    private Button btn_back, btn_one, btn_two, btn_repair_other,btn_change;// 故障等级选择 1 2
    private CheckBox cbx_bug_ok; // 通讯是否中断
    // 是否打印凭条
    // 是否报修设备商
    private EditText et_unit_why, et_measure, et_reason,  et_remarks; // 修复措施，未修复原因，工程师，异常，备注
    private Spinner spinner_result;

    private TextView tv_title, et_recovertime, tv_enginesite_1, tv_engine_order_site;// 标题栏
    // 时间对话框
    private Dialog dialog_time;
    private WedgrTime_Picker picker_time;
    private Button btn_back_time;
    private TextView tv_title_time,tv_signature, btn_ok, btn_ok_time;
    private ImageView img_sign;

    private TmrBankFaultVo_Dao bank_dao;// 故障登记
    private LoginDao login_dao;
    private AtmVoDao atm_dao;
    private OperateLogVo_Dao oper_dao;
    private int bug_level;

    private siginReceiver broadReceiver;
    private String clientid,errortime,faultmessages,repairTaskid;
    private TmrBankFaultVo bankVo;
    // 双击 事件 计算点击的次数
    private long oneClick;
    private long twoClick;
    private int numb,errorlevel;
    private String today_time, hour_minute, arrayTime/*//维修任务开始时间*/, arrivaltime, selectValue;//到达时间
    private UniqueAtmVo atm_bean;
    private IsRepairDao repair_dao;
    private Action action;
    private boolean isRepair;//是否是是现场维修界面跳转过来  如果是 则该任务不算完成
    private String targetDir,photoName;
    private Bitmap mSignBitmap;
    private List<LoginVo> users;
    private UniqueAtmDao unique_dao;
    private int input  ;
    private RelativeLayout ll_layout_change,ll_error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_bug);

        action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        atm_bean = (UniqueAtmVo) action.getCommObj();
        input = (int)action.getCommObj_1();
        Intent intent = getIntent();
        arrayTime = intent.getExtras().getString("arrayTime");
        isRepair = intent.getExtras().getBoolean("isRepair");
        if(isRepair){
            if(!TextUtils.isEmpty(intent.getExtras().getString("taskid"))){
                repairTaskid = intent.getExtras().getString("taskid");
            }
            faultmessages = intent.getExtras().getString("faultmessages");
            PDALogger.d("---repairTaskid---->"+repairTaskid+"---faultmessages---->"+faultmessages);
        }


        bank_dao = new TmrBankFaultVo_Dao(getHelper());
        login_dao = new LoginDao(getHelper());
        repair_dao = new IsRepairDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        oper_dao = new OperateLogVo_Dao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());

        users = login_dao.queryAll();
        if (users != null && users.size() > 0)
            clientid = users.get(users.size() - 1).getClientid();
        initView();

        HashMap<String, Object> array_item = new HashMap<String, Object>();
        array_item.put("barcode", atm_bean.getBranchbacode());
        List<OperateLogVo> operateLogVos = oper_dao.quaryForDetail(array_item);
        if (operateLogVos != null && operateLogVos.size() > 0) {
            arrivaltime = operateLogVos.get(0).getOperatetime();
        }

        PDALogger.d("-登记--->"+isRepair);
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_one = (Button) findViewById(R.id.btn_level_o);
        btn_two = (Button) findViewById(R.id.btn_level_t);
        cbx_bug_ok = (CheckBox) findViewById(R.id.cbx_bug_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);// 标题栏
        tv_title.setText(getResources().getString(R.string.bug_info_register));

        et_unit_why = (EditText) findViewById(R.id.et_unit_why);
        tv_enginesite_1 = (TextView) findViewById(R.id.tv_engine_to_site_1);// 工程师到场时间
        et_recovertime = (TextView) findViewById(R.id.et_recovertime); // 结束维修时间
        et_measure = (EditText) findViewById(R.id.bug_unit_1); // 修复措施
        et_reason = (EditText) findViewById(R.id.bug_unit_2); // 未修复原因
        et_remarks = (EditText) findViewById(R.id.et_remarks); // 备注
        btn_repair_other = (Button) findViewById(R.id.btn_repair_other);
        spinner_result = (Spinner) findViewById(R.id.spinner_result);
        tv_engine_order_site = (TextView) findViewById(R.id.tv_engine_order_site);//工程师预约时间

        tv_signature = (TextView) findViewById(R.id.tv_re_signature);
        img_sign = (ImageView) findViewById(R.id.img_re_sign);
        ll_layout_change = (RelativeLayout) findViewById(R.id.ll_layout_change);
        ll_error = (RelativeLayout) findViewById(R.id.ll_error);

        btn_change = (Button) findViewById(R.id.btn_change);

        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        tv_signature.setOnClickListener(siginListener());
        img_sign.setOnClickListener(siginListener());

        et_recovertime.setOnClickListener(this);
        tv_enginesite_1.setOnClickListener(this);
        btn_repair_other.setOnClickListener(this);
        tv_engine_order_site.setOnClickListener(this);

        cbx_bug_ok.setOnCheckedChangeListener(this);

        et_recovertime.setOnTouchListener(timeOnTouchListener);
        tv_enginesite_1.setOnTouchListener(engineToListener);
        tv_engine_order_site.setOnTouchListener(engineOrderTime);

        HashMap<String, Object> bank_item = new HashMap<String, Object>();
        bank_item.put("atmid", atm_bean.getAtmid());
        List<TmrBankFaultVo> others = bank_dao.quaryForDetail(bank_item);
        if (others != null && others.size() > 0) {
            bankVo = others.get(others.size() - 1);
        } else {
            bankVo = new TmrBankFaultVo();
        }

        broadReceiver = new siginReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("SIGN_OK");

        registerReceiver(broadReceiver, filter);
        // 获取时间
        String curr_data = Util.getNowDetial_toString();
        today_time = curr_data.substring(0, 10);
        hour_minute = curr_data.substring(11, 16);

        String[] selectitem = {getResources().getString(R.string.is_repair_ok), getResources().getString(R.string.is_repair_no)};
        final ArrayList<String> arrlist = new ArrayList<String>();// String 转化我list
        for (String string : selectitem) {
            arrlist.add(string);
        }

        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), arrlist);
        spinner_result.setAdapter(adapter);

        spinner_result.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectValue = arrlist.get(position).toString();
                if (arrlist.get(position).toString().equals(getResources().getString(R.string.is_repair_ok))) {
                    bankVo.setResult("0");
                } else if (arrlist.get(position).toString().equals(getResources().getString(R.string.is_repair_no))) {
                    bankVo.setResult("1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //如果该任务是维修任务则  有三项可以不填写  fauliMesage Errorlevel  Errortime 直接显示
        if (!isRepair) {
            Map<String, Object> atm_item = new HashMap<String, Object>();
            atm_item.put("clientid", clientid);
            atm_item.put("atmid", atm_bean.getAtmid());
            List<AtmVo> content_info = atm_dao.quaryForDetail(atm_item);
            if (content_info != null && content_info.size() > 0) {
                AtmVo atmVo = content_info.get(content_info.size() - 1);
                errorlevel = atmVo.getErrorlevel();
                errortime = atmVo.getErrortime();
            }
        } else {
               btn_back.setVisibility(View.GONE);
        }

        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            changeSeal();//泰国更换封签
        }
        ShowOperationData();
    }

    private void changeSeal() {
        ll_layout_change.setVisibility(View.VISIBLE);
        ll_error.setVisibility(View.GONE);
        btn_change.setOnClickListener(this);

    }

    /**
     * 根据任务Id 查询 是否操作过 操作过就显示搓足后的数据
     */
    private void ShowOperationData() {
        //如果改任务本身就是维修任务 则  故障登记直接显示
        if(!isRepair) {
            if (errorlevel == 1) {
                btn_one.setBackgroundResource(R.drawable.btn_two);
                btn_one.setTextColor(getResources().getColor(R.color.generic_white));
                bug_level = 1;
            } else if(errorlevel == 2){
                btn_two.setBackgroundResource(R.drawable.btn_two);
                btn_two.setTextColor(getResources().getColor(R.color.generic_white));
                bug_level = 2;
            }
        }
        Map<String, Object> where_bug = new HashMap<String, Object>();
        where_bug.put("clientid", clientid);
        where_bug.put("atmid", atm_bean.getAtmid());
        List<TmrBankFaultVo> bug_info = bank_dao.quaryForDetail(where_bug);
        if (bug_info != null && bug_info.size() > 0) {
            for (TmrBankFaultVo bankVo : bug_info) {
                int level = bankVo.getFaultlevel();
                if (level == 1) { // 故障等级
                    btn_one.setBackgroundResource(R.drawable.btn_two);
                    btn_one.setTextColor(getResources().getColor(R.color.generic_white));
                    bug_level = 1;
                } else if (level == 2) {
                    btn_two.setTextColor(getResources().getColor(R.color.generic_white));
                    btn_two.setBackgroundResource(R.drawable.btn_two);
                    bug_level = 2;
                }
                Boolean isok = bankVo.getIsrepaired();
                if (isok) {
                    cbx_bug_ok.setChecked(true);
                } else {
                    cbx_bug_ok.setChecked(false);
                }
                String result = bankVo.getResult();

                if(!TextUtils.isEmpty(result)){
                    //给Spinner 设置已经选择项
                    if(result.equals(0)){
                        spinner_result.setSelection(0);
                    }
                    if(result.equals(1)){
                        spinner_result.setSelection(1);
                    }

                }

                //设置签名
                if (!TextUtils.isEmpty(bankVo.getEnginephoto())) {
                    img_sign.setVisibility(View.VISIBLE);
                    tv_signature.setVisibility(View.GONE);
                    Bitmap imageBitmap = BitmapFactory.decodeFile(bankVo.getEnginephoto());
                    img_sign.setImageBitmap(imageBitmap);
                }
                et_recovertime.setText(bankVo.getTimetorepair());
                et_measure.setText(bankVo.getRepairmeasures());
                et_reason.setText(bankVo.getNotrepaircause());
                et_remarks.setText(bankVo.getRemarks());
                tv_enginesite_1.setText(bankVo.getEngineersarrivetime());
                tv_engine_order_site.setText(bankVo.getOrderedtime());
                et_unit_why.setText(bankVo.getFailurecause());

            }

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_ok:
                getInputDate();
                break;
            case R.id.btn_level_o:
                btn_one.setBackgroundResource(R.drawable.btn_two);
                btn_one.setTextColor(getResources().getColor(R.color.generic_white));

                btn_two.setBackgroundResource(R.drawable.btn_one);
                btn_two.setTextColor(getResources().getColor(R.color.generic_blue));
                bug_level = 1;
                break;
            case R.id.btn_level_t:

                btn_two.setBackgroundResource(R.drawable.btn_two);
                btn_two.setTextColor(getResources().getColor(R.color.generic_white));

                btn_one.setBackgroundResource(R.drawable.btn_one);
                btn_one.setTextColor(getResources().getColor(R.color.generic_blue));
                bug_level = 2;
                break;
            case R.id.et_recovertime:
                et_recovertime.setText(today_time + " " + hour_minute + ":00");
                break;
            case R.id.tv_engine_to_site_1:
                tv_enginesite_1.setText(today_time + " " + hour_minute + ":00");
                break;
            case R.id.tv_engine_order_site:
                tv_engine_order_site.setText(today_time + " " + hour_minute + ":00");
                break;

            case R.id.btn_repair_other:
                Intent intent = new Intent(this, ATMTroub_Activity.class);
                intent.putExtra(BaseActivity.EXTRA_ACTION, action);
                startActivity(intent);
                break;

            case R.id.btn_change://泰国更换封签
                startActivity(new Intent(this,ChangeSeal_Activity.class).putExtra(BaseActivity.EXTRA_ACTION, action));

            break;
            default:
                break;
        }
    }



    //签名完成 广播  设置数据
    public class siginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("SIGN_OK")) {
                bankVo.setEnginephoto(photoName);
                //保存签名照到签名数据库
                SiginPhotoDao sigin_dao = new SiginPhotoDao(getHelper());
                SiginPhotoVo siginPhotoVo = new SiginPhotoVo();
                siginPhotoVo.setClientid(clientid);
                siginPhotoVo.setAtmid(atm_bean.getAtmid());
                siginPhotoVo.setTaskid(atm_bean.getTaskid());
                siginPhotoVo.setBranchid(atm_bean.getBranchid());
                siginPhotoVo.setBranchidname(atm_bean.getBranchname());
                siginPhotoVo.setOperator(UtilsManager.getOperaterUsers(users));
                siginPhotoVo.setOperatedtime(Util.getNowDetial_toString());
                siginPhotoVo.setSiginpath(photoName);
                if (sigin_dao.contentsNumber(siginPhotoVo) > 0) {
                    sigin_dao.upDate(siginPhotoVo);
                } else {
                    sigin_dao.create(siginPhotoVo);

                }
            }
        }
    }
    //签名点击事件
    public OnClickListener siginListener(){
        OnClickListener signListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WritePadDialog writeTabletDialog = new WritePadDialog(view.getContext(), new WritePadDialog.DialogListener() {
                    @Override
                    public void refreshActivity(Object object) {// 这里是点击对话框里的确定后才调用处理的

                        mSignBitmap = (Bitmap) object;
                        save(mSignBitmap);
                        img_sign.setVisibility(View.VISIBLE);
                        img_sign.setImageBitmap(mSignBitmap);
                        tv_signature.setVisibility(View.GONE);
                    }
                });
                writeTabletDialog.show();
            }
        };
        return  signListener;
    };

    /**
     * 保存图片
     *
     * @param baseBitmap
     */
    public void save(Bitmap baseBitmap) {
        try {
            targetDir = UtilsManager.getPicturePath();

            File file1 = new File(targetDir);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            photoName = targetDir + "/" + "repair"+"_" + Util.getSystemTime() + ".jpg";
            File file = new File(targetDir, "/" +"repair"+"_" + Util.getSystemTime() + ".jpg");
            OutputStream stream = new FileOutputStream(file);
            baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
            intent.setData(Uri.fromFile(new File(photoName)));
            sendBroadcast(intent);
            Toast.makeText(this, getResources().getString(R.string.picture_save_ok), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
//            Toast.makeText(this, getResources().getString(R.string.picture_save_no), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    /**
     * 获取数据的信息保存到数据库 并上传所操作数据 结束维修时间、故障原因为必填项，修复措施、未修复原因为二选一必填项
     * （根据是否修复来判断哪个为必填项），没有填全，不允许做后续操作
     */
    private void getInputDate() {
        Boolean time_over = TextUtils.isEmpty(et_recovertime.getText().toString());
        Boolean unit_why = TextUtils.isEmpty(et_unit_why.getText().toString());
        Boolean measure = TextUtils.isEmpty(et_measure.getText().toString());
        Boolean reson = TextUtils.isEmpty(et_reason.getText().toString());

        //如果维修解雇欧式未去  可以什么都不填
//        if (selectValue.equals(getResources().getString(R.string.repair_not_go))) {
//            setDataToDb();
//        } else {

            if (time_over || unit_why) {
                CustomDialog dialog = new CustomDialog(this, getResources().getString(R.string.atm_repair_tip_1));
                dialog.showConfirmDialog();
            } else {

                // 故障修复 必填项是修复措施
                if (selectValue.equals(getResources().getString(R.string.is_repair_ok))) {
                    if (measure) {
                        CustomDialog dialog = new CustomDialog(this, getResources().getString(R.string.atm_repair_tip_2));
                        dialog.showConfirmDialog();
                    } else {

                        setDataToDb();

                    }
                } else {

                    if (reson) {
                        CustomDialog dialog = new CustomDialog(this, getResources().getString(R.string.atm_repair_tip_3));
                        dialog.showConfirmDialog();
                    } else {
                        setDataToDb();
                    }
                }
            }
//        }


    }

    private void setDataToDb() {
        List<LoginVo> users = login_dao.queryAll();
        if (isRepair) {
            bankVo.setTaskid(repairTaskid);
        } else {
            bankVo.setTaskid(atm_bean.getTaskid());
        }
        bankVo.setClientid(clientid);

        bankVo.setBranchid(atm_bean.getBranchid());
        bankVo.setTimetorepair(et_recovertime.getText().toString());
        bankVo.setFailurecause(et_unit_why.getText().toString());
        bankVo.setRepairmeasures(et_measure.getText().toString());
        bankVo.setNotrepaircause(et_reason.getText().toString());
        bankVo.setRemarks(et_remarks.getText().toString());
        bankVo.setAtmid(atm_bean.getAtmid());
        bankVo.setOperator(UtilsManager.getOperaterUsers(users));
        bankVo.setOperatedtime(Util.getNowDetial_toString());
        bankVo.setEngineersarrivetime(tv_enginesite_1.getText().toString());
        bankVo.setOrderedtime(tv_engine_order_site.getText().toString());
        bankVo.setFaultmessages(faultmessages);
        bankVo.setUuid(UUID.randomUUID().toString());

        if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国项目  故障等级不显示  后台可能为必填字段  默认为1
            if(!isRepair){
                bankVo.setFaulttime(errortime);
                bankVo.setFaultlevel(1);
            } else {
                bankVo.setFaulttime(Util.getNowDetial_toString());
                bankVo.setFaultlevel(1);
            }
        } else {
            if(!isRepair){
                bankVo.setFaulttime(errortime);
                bankVo.setFaultlevel(errorlevel);
            } else {
                bankVo.setFaulttime(Util.getNowDetial_toString());
                bankVo.setFaultlevel(bug_level);
            }
        }
        bankVo.setArrivaltime(arrivaltime);//到达时间和网点到达时间相同
        showSaveDialog();


    }

    private OnTouchListener timeOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
                    if (oneClick != 0 && System.currentTimeMillis() - oneClick > 300) {
                        numb = 0;
                    }
                    numb++;
                    if (numb == 1) {
                        oneClick = System.currentTimeMillis();
                    } else if (numb == 2) {
                        twoClick = System.currentTimeMillis();
                        // 两次点击小于300ms 也就是连续点击
                        if (twoClick - oneClick < 300) {// 判断是否是执行了双击事件
                            Boolean time_over = TextUtils.isEmpty(et_recovertime.getText().toString());
                            if (!time_over) {

                                showTimeConfirmDialog(2);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        }

    };

    private OnTouchListener engineToListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
                    if (oneClick != 0 && System.currentTimeMillis() - oneClick > 300) {
                        numb = 0;
                    }
                    numb++;
                    if (numb == 1) {
                        oneClick = System.currentTimeMillis();
                    } else if (numb == 2) {
                        twoClick = System.currentTimeMillis();
                        // 两次点击小于300ms 也就是连续点击
                        if (twoClick - oneClick < 300) {// 判断是否是执行了双击事件
                            Boolean time_over = TextUtils.isEmpty(tv_enginesite_1.getText().toString());
                            if (!time_over) {

                                showTimeConfirmDialog(3);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        }

    };
    private OnTouchListener engineOrderTime = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
                    if (oneClick != 0 && System.currentTimeMillis() - oneClick > 300) {
                        numb = 0;
                    }
                    numb++;
                    if (numb == 1) {
                        oneClick = System.currentTimeMillis();
                    } else if (numb == 2) {
                        twoClick = System.currentTimeMillis();
                        // 两次点击小于300ms 也就是连续点击
                        if (twoClick - oneClick < 300) {// 判断是否是执行了双击事件
                            Boolean time_over = TextUtils.isEmpty(tv_engine_order_site.getText().toString());
                            if (!time_over) {

                                showTimeConfirmDialog(1);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        }

    };

    /**
     * 打开时间选择对话框
     */
    public void showTimeDialog(final int witch) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_wedge_time, null);// 得到加载view
        picker_time = (WedgrTime_Picker) v.findViewById(R.id.picker);
        btn_back_time = (Button) v.findViewById(R.id.btn_back);
        btn_ok_time = (TextView) v.findViewById(R.id.btn_ok);
        tv_title_time = (TextView) v.findViewById(R.id.tv_title);
        tv_title_time.setText(getResources().getString(R.string.add_wedge_dialog_choose_time));

        btn_back_time.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_time.cancel();
            }
        });
        btn_ok_time.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (witch == 2) {
                    et_recovertime.setText(today_time + " " + picker_time.getresult() + ":00");
                } else if (witch == 3) {
                    tv_enginesite_1.setText(today_time + " " + picker_time.getresult() + ":00");
                } else {
                    tv_engine_order_site.setText(today_time + " " + picker_time.getresult() + ":00");
                }
                dialog_time.dismiss();
            }
        });

        dialog_time = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
        dialog_time.setContentView(v);
        Window dialogWindow = dialog_time.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        dialog_time.show();
    }

    // CheckBox 选择变化
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == cbx_bug_ok) {// 是否修复
            if (isChecked)
                bankVo.setIsrepaired(true);
            else
                bankVo.setIsrepaired(false);

        }
    }

    /**
     * 完成ATM现场操作
     */
    private void showSaveDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.dialog_save_atm_done));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 如果该任务id已经存在 只对数据进行更新
                if(bank_dao.contentsNumber(bankVo) > 0){
                    bank_dao.upDate(bankVo);
                } else {
                    bank_dao.create(bankVo);
                }



                if (!isRepair) {
                    //修改ATMVO类 该atm任务已经完成
                    HashMap<String, Object> repair_item = new HashMap<String, Object>();
                    repair_item.put("atmid", atm_bean.getAtmid());
                    List<AtmVo> atmVoList = atm_dao.quaryForDetail(repair_item);
                    if (atmVoList != null && atmVoList.size() > 0) {
                        AtmVo atmVo = atmVoList.get(atmVoList.size() - 1);
                        atmVo.setIsatmdone("Y");
                        atmVo.setIsRegister("Y");
                        atm_dao.upDate(atmVo);
                    }
                }
                //登记
                if(input == 1){
                    //机具任务表是否登记
                    HashMap<String, Object> repair_item = new HashMap<String, Object>();
                    repair_item.put("atmid", atm_bean.getAtmid());
                    List<AtmVo> atmVoList = atm_dao.quaryForDetail(repair_item);
                    if (atmVoList != null && atmVoList.size() > 0) {
                        AtmVo atmVo = atmVoList.get(atmVoList.size() - 1);
                        atmVo.setIsRegister("Y");
                        atmVo.setIsUploaded("N");
                        atm_dao.upDate(atmVo);
                    }

                    //更新网点下机具是否登记
                    HashMap<String, Object> repair = new HashMap<String, Object>();
                    repair.put("branchid", atm_bean.getBranchid());
                    repair.put("barcode" ,atm_bean.getBarcode());
                    List<UniqueAtmVo>  uniqueAtmVos = unique_dao.quaryForDetail(repair);
                    if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                        UniqueAtmVo uniqueAtmVo= uniqueAtmVos.get(0);
                        uniqueAtmVo.setIsRegister("Y");
                        uniqueAtmVo.setIsUploaded("N");
                        unique_dao.upDate(uniqueAtmVo);
                    }

                    //更新故障信息
                    bankVo.setIsUploaded("N");
                    bankVo.setIsRegister("Y");
                    bank_dao.upDate(bankVo);

                }

                sendBroadcast(new Intent(Config.BROADCAST_UPLOAD));
                sendBroadcast(new Intent(IsRepair_Activity.FAULI_OK));
                dialog.dismiss();
                finish();
            }
        });
        bt_miss.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 时间更新提示
     */
    private void showTimeConfirmDialog(final int witch) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.add_atm_up_dialog_change));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (witch == 2) {
                    showTimeDialog(2);
                    dialog.dismiss();
                } else if (witch == 3) {
                    showTimeDialog(3);
                    dialog.dismiss();
                } else {
                    showTimeDialog(1);
                    dialog.dismiss();
                }
            }
        });
        bt_miss.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if (broadReceiver != null)
            unregisterReceiver(broadReceiver);
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(isRepair) {

            CustomToast.getInstance().getInstance().showLongToast(getResources().getString(R.string.please_repait_done));
            return false ;
        }
        return super.onKeyDown(keyCode, event);
    }
}
