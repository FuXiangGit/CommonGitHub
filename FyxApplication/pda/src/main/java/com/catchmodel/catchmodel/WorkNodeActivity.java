package com.catchmodel.catchmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.catchmodel.been.SaveAllDataVo;
import com.catchmodel.been.WorkNode_Vo;
import com.catchmodel.dao.SaveAllDataVoDao;
import com.catchmodel.dao.WorkNodeDao;
import com.catchmodel.gps.CatchGPS;
import com.catchmodel.gps.GPSManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xvli.application.PdaApplication;
import com.xvli.bean.ConfigVo;
import com.xvli.bean.LoginVo;
import com.xvli.comm.Config;
import com.xvli.dao.ConfigVoDao;
import com.xvli.dao.LoginDao;
import com.xvli.pda.BaseActivity;
import com.xvli.pda.R;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 9:50.
 */
public class WorkNodeActivity  extends BaseActivity {
    private EditText get_remark_gps1;
    private EditText get_remark_gps2;
    private Button takePic;
    private ImageView takePic_n;
    private File photoFile;
    private EditText get_remark_gpstop;
    private Button takePic_2;
    private Button takePic_3;
    private ImageView takePic_n_2;
    private ImageView takePic_n_3;
    private int arg = 0;
    //	private CatchGPS catchGPS;
    private AutoCompleteTextView get_branchname ,branchname_customer ,branchname_address;
    private WorkNodeDao workNodeDao;
    private List<WorkNode_Vo> workNode_vos ;
    private ArrayFilter mFilter;
    private ArrayFilterAtmCustomerName  mCustomer;
    private ArrayFilterAddress  mAddress;
    private BranchAdpater  branchAdpater;
    private BranchAdpater1  branchAdpater1;
    private BranchAdpater2  branchAdpater2;
    private File  imageFile;
    private SaveAllDataVoDao saveAllDataVoDao;//保存网点采集信息
    private boolean isNetPhoto = false;//网点位置是否拍照
    private boolean isCarPhoto = false;//停车点是否拍照
    private SaveAllDataVo saveAllDataVo ;
    private Button search_branch , customer_branch , address_branch ,submit ,save ,btn_back ;
    private GPSManager gps;
    private List<LoginVo> users;
    private LoginDao login_dao;
    private String allJobNum;
    private ConfigVoDao configVoDao;
    private List<ConfigVo> configVos;
    private int day;
    private Bitmap bitmap;
//    private Bitmap bitmap1;
//    private Bitmap bitmap2;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private TextView tv_title,btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worknode_catchmodel);
//		catchGPS = new CatchGPS(this);
        initImageLoader();
        initView();
    }
    private void initView() {
        saveAllDataVo = new SaveAllDataVo();
        workNodeDao = new WorkNodeDao(getHelper());
        saveAllDataVoDao = new SaveAllDataVoDao(getHelper());

        login_dao = new LoginDao(getHelper());
        configVoDao = new ConfigVoDao(getHelper());
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            allJobNum = UtilsManager.getOperaterUsers(users);
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("nametype", Config.PDA_SAVE_PHOTOS);
        configVos = configVoDao.quaryForDetail(hashMap);
        if (configVos != null && configVos.size() > 0) {
            day = Integer.parseInt(configVos.get(0).getValue());
        }
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.WorkNode_catchmodel));
        btn_ok = (TextView)findViewById(R.id.btn_ok);
        btn_ok.setVisibility(View.GONE);
        branchname_address = (AutoCompleteTextView) findViewById(R.id.branchname_address);
        branchname_customer = (AutoCompleteTextView) findViewById(R.id.branchname_customer);
        get_branchname = (AutoCompleteTextView) findViewById(R.id.get_branchname);
        get_remark_gps1 = (EditText) findViewById(R.id.get_remark_gps1);
        get_remark_gps2 = (EditText) findViewById(R.id.get_remark_gps2);
        get_remark_gpstop = (EditText) findViewById(R.id.get_remark_gpstop);
        takePic = (Button) findViewById(R.id.takePic);
        takePic_2 = (Button) findViewById(R.id.takePic_2);
        takePic_3 = (Button) findViewById(R.id.takePic_3);
        takePic_n = (ImageView) findViewById(R.id.takePic_n);
        takePic_n_2 = (ImageView) findViewById(R.id.takePic_n_2);
        takePic_n_3 = (ImageView) findViewById(R.id.takePic_n_3);
        get_remark_gpstop.setHintTextColor(Color.GRAY);
        get_remark_gps1.setHintTextColor(Color.GRAY);
        get_remark_gps2.setHintTextColor(Color.GRAY);
        search_branch = (Button) findViewById(R.id.search_branch);
        customer_branch = (Button) findViewById(R.id.customer_branch);
        address_branch = (Button) findViewById(R.id.address_branch);
        save = (Button) findViewById(R.id.save);
        submit = (Button) findViewById(R.id.submit);
//        data();//假数据

        SaveAllDataVo saveAllDataVo = (SaveAllDataVo) getIntent().getSerializableExtra("saveAllDataVo");
        String type = getIntent().getStringExtra("type");
        PDALogger.d("type=" + type);
        if (type != null && saveAllDataVo != null) {//查看详情
            get_branchname.setFocusable(false);
            get_branchname.setText(saveAllDataVo.getName());
            branchname_customer.setFocusable(false);
            branchname_customer.setText(saveAllDataVo.getCustomer());
            branchname_address.setFocusable(false);
            branchname_address.setText(saveAllDataVo.getAddress());

            save.setVisibility(View.GONE);
            submit.setVisibility(View.INVISIBLE);
            search_branch.setVisibility(View.GONE);
            customer_branch.setVisibility(View.GONE);
            address_branch.setVisibility(View.GONE);
            get_remark_gpstop.setText(saveAllDataVo.getGis());
            get_remark_gpstop.setFocusable(false);
            if (!TextUtils.isEmpty(saveAllDataVo.getGisTruck1())) {
                get_remark_gps1.setText(saveAllDataVo.getGisTruck1());
            } else {
                get_remark_gps1.setHint("");
            }
            get_remark_gps1.setFocusable(false);

            if (!TextUtils.isEmpty(saveAllDataVo.getGisTruck1())) {
                get_remark_gps2.setText(saveAllDataVo.getGisTruck1());
            } else {
                get_remark_gps2.setHint("");
            }

            get_remark_gps2.setFocusable(false);

//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPurgeable = true;
//            options.inSampleSize = 2;
//            bitmap = BitmapFactory.decodeFile(saveAllDataVo.getPrcture_name(), options);
            takePic.setVisibility(View.GONE);
            takePic_n.setVisibility(View.VISIBLE);
//            takePic_n.setImageBitmap(bitmap);
            imageLoader.displayImage("file://" + saveAllDataVo.getPrcture_name(), takePic_n, options);
            if (saveAllDataVo.getPrcture_name_Truck1() != null) {
                takePic_2.setVisibility(View.GONE);
                takePic_n_2.setVisibility(View.VISIBLE);
//                bitmap1 = BitmapFactory.decodeFile(saveAllDataVo.getPrcture_name_Truck1(), options);
//                takePic_n_2.setImageBitmap(bitmap1);
                imageLoader.displayImage("file://" + saveAllDataVo.getPrcture_name_Truck1(), takePic_n_2, options);
            } else {
                takePic_2.setHint("");
            }

            if (saveAllDataVo.getPrcture_name_Truck2() != null) {
                takePic_3.setVisibility(View.GONE);
                takePic_n_3.setVisibility(View.VISIBLE);
//                bitmap2 = BitmapFactory.decodeFile(saveAllDataVo.getPrcture_name_Truck2(), options);
//                takePic_n_3.setImageBitmap(bitmap2);
                imageLoader.displayImage("file://" + saveAllDataVo.getPrcture_name_Truck2(), takePic_n_3, options);
            } else {
                takePic_3.setHint("");
            }

        } else {
            btn_ok.setVisibility(View.VISIBLE);
            btn_ok.setText(getResources().getString(R.string.add_wedge_dialog_save));
            Drawable drawable= getResources().getDrawable(R.mipmap.save_icon);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn_ok.setCompoundDrawables(null, drawable, null, null);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubMitData();
                }
            });

            takePic_n.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    imageFile = initCamre("_1", 1);
                }
            });
            takePic_2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    imageFile = initCamre("_2", 2);
                }
            });
            takePic_3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    imageFile = initCamre("_3", 3);
                }
            });


            takePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imageFile = initCamre("_1", 1);
                    closeInput(takePic);
                }
            });
            takePic_n_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imageFile = initCamre("_2", 2);
                    closeInput(takePic_n_2);
                }
            });
            takePic_n_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeInput(takePic_n_3);
                    imageFile = initCamre("_3", 3);

                }
            });


            get_remark_gpstop.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    closeInput(get_remark_gpstop);

//                    get_remark_gpstop.setGravity(Gravity.CENTER);

                    CatchGPS gps = new CatchGPS(getApplicationContext());
                    if (gps.openGPS()) {
                        get_remark_gpstop.setText(PdaApplication.getInstance().lat + "," + PdaApplication.getInstance().lng + "," + PdaApplication.getInstance().alt);

                        arg = 1;

                    } else {
                        Toast.makeText(getApplicationContext(), "请先检查GPS有没有打开！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });

            get_remark_gps1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    closeInput(get_remark_gps1);
                    CatchGPS gps = new CatchGPS(getApplicationContext());
                    if (gps.openGPS()) {
                        get_remark_gps1.setText(PdaApplication.getInstance().lat + "," + PdaApplication.getInstance().lng + "," + PdaApplication.getInstance().alt);

                        arg = 2;
                    } else {
                        Toast.makeText(getApplicationContext(), "请先检查GPS有没有打开！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });

            get_remark_gps2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    closeInput(get_remark_gps2);
                    CatchGPS gps = new CatchGPS(getApplicationContext());
                    if (gps.openGPS()) {
                        get_remark_gps2.setText(PdaApplication.getInstance().lat + "," + PdaApplication.getInstance().lng + "," + PdaApplication.getInstance().alt);

                        arg = 3;
                    } else {
                        Toast.makeText(getApplicationContext(), "请先检查GPS有没有打开！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
            init(1);//1 网点名称
            init(2);//2 所属客户
            init(3);//3 网点地址
        }
    }

    private void  init(int type){
        if(type == 1){
            final List<WorkNode_Vo> workNode_vos = workNodeDao.getNameDISTINCT();
            branchAdpater = new BranchAdpater(this ,workNode_vos,type);
            get_branchname.setAdapter(branchAdpater);
            get_branchname.setThreshold(0);
            get_branchname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WorkNode_Vo branchVo = (WorkNode_Vo) parent.getItemAtPosition(position);
                    if(!TextUtils.isEmpty(branchVo.getName())){
                        get_branchname.setText(branchVo.getName().toString());
                        final List<WorkNode_Vo> workNode_vos1 = workNodeDao.getAllCustomerDISTINCT(get_branchname.getText().toString());
                        if(workNode_vos1!=null ){
                            if(workNode_vos1.size() ==1){
                                branchname_customer.setText(workNode_vos1.get(0).getCompany());
                            }else{
                                branchname_customer.setText("");
                                branchname_address.setText("");
                            }
                        }else{
                            branchname_customer.setText("");
                        }

                        if(!TextUtils.isEmpty(branchname_customer.getText().toString())){
                            final List<WorkNode_Vo> workNode_vos2 =	workNodeDao.getAddessDISTINCT(
                                    get_branchname.getText().toString(),branchname_customer.getText().toString());
                            if(workNode_vos2!=null){
                                if(workNode_vos2.size() ==1){
                                    branchname_address.setText(workNode_vos2.get(0).getAddress());
                                }else{
                                    branchname_address.setText("");
                                }

                            }else{
                                branchname_address.setText("");
                            }
                        }

                    }

                    closeInput(get_branchname);
                }
            });

            search_branch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PDALogger.d("show =" + search_branch);
                    branchAdpater.setList(workNode_vos);
                    branchAdpater.notifyDataSetChanged();
                    get_branchname.showDropDown();
                }
            });
        }

        if(type == 2){

            final List<WorkNode_Vo> workNode_vos = workNodeDao.getAllCustomerDISTINCT();
            branchAdpater1 = new BranchAdpater1(this ,workNode_vos,type);
            branchname_customer.setAdapter(branchAdpater1);
            branchname_customer.setThreshold(0);
            branchname_customer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WorkNode_Vo branchVo = (WorkNode_Vo) parent.getItemAtPosition(position);
                    if(!TextUtils.isEmpty(branchVo.getCompany())){
                        branchname_customer.setText(branchVo.getCompany().toString());
                    }

                    if(!TextUtils.isEmpty(get_branchname.getText())&&!TextUtils.isEmpty(branchname_customer.getText()) ){
                        final List<WorkNode_Vo> workInfo_catVos =	workNodeDao.getAddessDISTINCT(
                                get_branchname.getText().toString(), branchname_customer.getText().toString()
                        );
                        if(workInfo_catVos!=null){
                            if(workInfo_catVos.size() == 1){
                                branchname_address.setText(workInfo_catVos.get(0).getAddress());
                            }

                        }else{
                            branchname_address.setText("");
                        }
                    }
                    closeInput(branchname_customer);
                }
            });



            customer_branch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PDALogger.d("show =" + customer_branch);
                    if(!TextUtils.isEmpty(get_branchname.getText().toString())){
                            final List<WorkNode_Vo> workNode_vos1 = workNodeDao.getAllCustomerDISTINCT(get_branchname.getText().toString());
                        if(workNode_vos1!=null&&workNode_vos1.size()>0){
                            branchAdpater1.setList(workNode_vos1);
                            branchAdpater1.notifyDataSetChanged();
                            branchname_customer.showDropDown();
                        }else{
                            branchAdpater1.setList(workNode_vos);
                            branchAdpater1.notifyDataSetChanged();
                            branchname_customer.showDropDown();
                        }

                    }else{
                        branchAdpater1.setList(workNode_vos);
                        branchAdpater1.notifyDataSetChanged();
                        branchname_customer.showDropDown();
                    }

                }
            });


        }

        if(type == 3){
            final List<WorkNode_Vo> workNode_vos = workNodeDao.getAddessDISTINCT();
            branchAdpater2 = new BranchAdpater2(this ,workNode_vos,type);

            branchname_address.setAdapter(branchAdpater2);
            branchname_address.setThreshold(0);
            branchname_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WorkNode_Vo branchVo = (WorkNode_Vo) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(branchVo.getAddress())) {
                        branchname_address.setText(branchVo.getAddress().toString());
                    } else {
                        branchname_address.setText("");
                    }

                    closeInput(branchname_address);
                }
            });
            address_branch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PDALogger.d("show =" + address_branch);
                    if(!TextUtils.isEmpty(get_branchname.getText().toString())&&!TextUtils.isEmpty(branchname_customer.getText().toString())){
                        final List<WorkNode_Vo> workNode_vos1 =	workNodeDao.getAddessDISTINCT(
                                get_branchname.getText().toString(), branchname_customer.getText().toString()
                        );
                        if(workNode_vos1!=null&& workNode_vos1.size()>0){
                            branchAdpater2.setList(workNode_vos1);
                            branchAdpater2.notifyDataSetChanged();
                            branchname_address.showDropDown();
                        }else{
                            branchAdpater2.setList(workNode_vos);
                            branchAdpater2.notifyDataSetChanged();
                            branchname_address.showDropDown();
                        }

                    }else{
                        branchAdpater2.setList(workNode_vos);
                        branchAdpater2.notifyDataSetChanged();
                        branchname_address.showDropDown();
                    }

                }
            });

        }

    }


    public void SubMitData() {
        String Net_Name;
        String Net_customer;
        //网点名称是否匹配
        if (TextUtils.isEmpty(get_branchname.getText().toString())) {//网点名称不能为空
            get_branchname.setGravity(Gravity.CENTER_VERTICAL);
            get_branchname.setHintTextColor(Color.RED);
            get_branchname.setHint(R.string.not_isEmpty);
            get_branchname.requestFocus();
            return;
        } else {
            Net_Name = get_branchname.getText().toString();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Name", Net_Name);
            workNode_vos = workNodeDao.quaryForDetail(hashMap);
            if (workNode_vos != null && workNode_vos.size() > 0) {
                saveAllDataVo.setIds(workNode_vos.get(0).getIds());
                saveAllDataVo.setName(workNode_vos.get(0).getName());
            } else {//网点名称 需要和接口数据匹配
                CustomToast.getInstance().showShortToast(R.string.not_branch);
                return;
            }
        }

        //所属客户是否匹配
        if (TextUtils.isEmpty(branchname_customer.getText().toString())) {
            branchname_customer.setGravity(Gravity.CENTER_VERTICAL);
            branchname_customer.setHintTextColor(Color.RED);
            branchname_customer.setHint(R.string.not_isEmpty);
            branchname_customer.requestFocus();
            return;
        } else {
            Net_customer = branchname_customer.getText().toString();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Company", Net_customer);
            hashMap.put("Name", Net_Name);
            workNode_vos = workNodeDao.quaryForDetail(hashMap);
            if (workNode_vos != null && workNode_vos.size() > 0) {
                saveAllDataVo.setIds(workNode_vos.get(0).getIds());
                saveAllDataVo.setName(workNode_vos.get(0).getName());
                saveAllDataVo.setCustomer(Net_customer);

            } else {//网点名称 需要和接口数据匹配
                CustomToast.getInstance().showShortToast(R.string.branch_customer);
                return;
            }

        }

        //网点地址是否匹配
        if (TextUtils.isEmpty(branchname_address.getText().toString())) {
            branchname_address.setGravity(Gravity.CENTER_VERTICAL);
            branchname_address.setHintTextColor(Color.RED);
            branchname_address.setHint(R.string.not_isEmpty);
            branchname_address.requestFocus();
            return;
        } else {
            String Net_address = branchname_address.getText().toString();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Company", Net_customer);
            hashMap.put("Name", Net_Name);
            hashMap.put("Address", Net_address);
            workNode_vos = workNodeDao.quaryForDetail(hashMap);
            if (workNode_vos != null && workNode_vos.size() > 0) {
                saveAllDataVo.setIds(workNode_vos.get(0).getIds());
                saveAllDataVo.setName(workNode_vos.get(0).getName());
                saveAllDataVo.setCustomer(Net_customer);
                saveAllDataVo.setAddress(Net_address);
            } else {//网点名称 需要和接口数据匹配
                CustomToast.getInstance().showShortToast(R.string.branch_customer_adress);
                return;
            }
        }

        if (get_remark_gpstop.getText().toString().equals("") || get_remark_gpstop.getText().toString() == null) {
            //网点位置不能为空
            get_remark_gpstop.setGravity(Gravity.CENTER_VERTICAL);
            get_remark_gpstop.setHintTextColor(Color.RED);
            get_remark_gpstop.setHint(R.string.not_isEmpty);
            get_remark_gpstop.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(get_remark_gps1.getText().toString()) && TextUtils.isEmpty(get_remark_gps2.getText().toString())) {
            //停车点位置信息至少一个
            CustomToast.getInstance().showShortToast(R.string.least_one);
            return;
        }

        //照片
        if (!isNetPhoto) {
            CustomToast.getInstance().showShortToast(R.string.net_photo);
            return;
        }

        if (!isCarPhoto) {
            CustomToast.getInstance().showShortToast(R.string.least_one_car);
            return;
        }


        saveAllDataVo.setType("WorkNode");
        saveAllDataVo.setGis(get_remark_gpstop.getText().toString());
        saveAllDataVo.setGisTruck1(get_remark_gps1.getText().toString());
        saveAllDataVo.setGisTruck2(get_remark_gps2.getText().toString());
        saveAllDataVo.setIsUpLoader("N");
        saveAllDataVo.setImageUpLoader("N");
        saveAllDataVo.setSaveTime(Util.getNowDetial_toString());
        saveAllDataVo.setJobNumber(allJobNum);
        saveAllDataVo.setDay(day);
        saveAllDataVoDao.create(saveAllDataVo);
        //清理没有保存图片数据的图片
        String names = Config.Catchmodel;
        File photoFile = new File(names);
        if (photoFile.exists()) {
            File[] files = photoFile.listFiles();
            if (files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("prcture_name", files[i].getPath());
                    List<SaveAllDataVo> saveAllDataVos = saveAllDataVoDao.quaryForDetail(hashMap);


                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    hashMap1.put("prcture_name_Truck1", files[i].getPath());
                    List<SaveAllDataVo> saveAllDataVos1 = saveAllDataVoDao.quaryForDetail(hashMap1);

                    HashMap<String, Object> hashMap2 = new HashMap<>();
                    hashMap2.put("prcture_name_Truck2", files[i].getPath());
                    List<SaveAllDataVo> saveAllDataVos2 = saveAllDataVoDao.quaryForDetail(hashMap2);

                    if (saveAllDataVos != null && saveAllDataVos.size() > 0) {
                    } else if (saveAllDataVos1 != null && saveAllDataVos1.size() > 0) {
                    } else if (saveAllDataVos2 != null && saveAllDataVos2.size() > 0) {
                    } else {
                        files[i].delete();
                    }
                }
            }
        }


        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);
        CustomToast.getInstance().showShortToast(R.string.picture_save_ok);

        closeActivity();

    }


//    private Bitmap setImage() {
//        File instanceFile=new File(imageFile.getPath());
//
//        PDALogger.d("imageFile == " + imageFile);
//        if (imageFile != null) {
//            if(instanceFile.exists()){
//                BitmapFactory.Options justBoundsOptions = MemoryManager.createJustDecodeBoundsOptions();
//                justBoundsOptions.inSampleSize = 2;
//                BitmapFactory.decodeFile(imageFile.getPath(), justBoundsOptions);
//                bitmap = BitmapFactory.decodeFile(imageFile.getPath(), MemoryManager.createSampleSizeOptions(justBoundsOptions, 300, 200));
//                ByteArrayOutputStream outCache = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outCache);
//                return bitmap;
//            }
//        }
//        return null;
//    }

    private File initCamre(String path,int arg){
        String names = Config.Catchmodel;
        PDALogger.d("names = " +names);
        photoFile = new File(names);
        if (!photoFile.exists()) {
            photoFile.mkdirs();
            PDALogger.d("exists = " + photoFile.getParentFile().exists());
        }

        File imageFile = new File(names, Util.getNowDetial_toString() + ".jpg");
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        imageIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        imageIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 90);
        this.startActivityForResult(imageIntent, arg);
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case 1:
                    isNetPhoto = true;
//                    bitmap = setImage();
                    saveAllDataVo.setPrcture_name(imageFile.getPath());
                    takePic.setVisibility(View.GONE);
                    takePic_n.setVisibility(View.VISIBLE);
//                    takePic_n.setImageBitmap(bitmap);

                    imageLoader.displayImage("file://" + imageFile.getPath(), takePic_n, options);

                    break;
                case 2:
                    isCarPhoto = true;

//                    bitmap = setImage();
                    saveAllDataVo.setPrcture_name_Truck1(imageFile.getPath());
                    takePic_2.setVisibility(View.GONE);
                    takePic_n_2.setVisibility(View.VISIBLE);
//                    takePic_n_2.setImageBitmap(bitmap);

                    imageLoader.displayImage("file://" + imageFile.getPath(), takePic_n_2, options);
                    break;
                case 3:
                    isCarPhoto = true;
//                    bitmap = setImage();
                    saveAllDataVo.setPrcture_name_Truck2(imageFile.getPath());
                    takePic_3.setVisibility(View.GONE);
                    takePic_n_3.setVisibility(View.VISIBLE);
//                    takePic_n_3.setImageBitmap(bitmap);

                    imageLoader.displayImage("file://" + imageFile.getPath(), takePic_n_3, options);
                    break;

                default:
                    bitmap.recycle();
                    System.gc();

                    break;
            }

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("d", "destory");
    }


    private void closeActivity(){
        if(bitmap !=null && !bitmap.isRecycled()){
            PDALogger.d("bitmapW="+bitmap);
            bitmap.recycle();
            bitmap=null;
        }


//        if(bitmap1 !=null && !bitmap1.isRecycled()){
//            PDALogger.d("bitmapW1="+bitmap1);
//            bitmap1.recycle();
//            bitmap1=null;
//        }
//
//        if(bitmap2 !=null && !bitmap2.isRecycled()){
//            PDALogger.d("bitmapW2="+bitmap2);
//            bitmap2.recycle();
//            bitmap2=null;
//        }
        System.gc();
        this.finish();
    }

    private void data(){
        List<WorkNode_Vo>  strings = new ArrayList<>();
        WorkNode_Vo branchVo = new WorkNode_Vo();
        branchVo.setAllresult("徐家汇01建行上海1");
        branchVo.setCompany("建行");
        branchVo.setIds("11");
        branchVo.setName("徐家汇01");
        branchVo.setAddress("上海1");
        strings.add(branchVo);
        WorkNode_Vo branchVo1 = new WorkNode_Vo();
        branchVo1.setAllresult("徐家汇012招商上海2");
        branchVo1.setIds("1");
        branchVo1.setName("徐家汇012");
        branchVo1.setCompany("招商");
        branchVo1.setAddress("上海2");
        strings.add(branchVo1);
        WorkNode_Vo branchVo2 = new WorkNode_Vo();
        branchVo2.setAllresult("徐家汇013工商上海3");
        branchVo2.setIds("55");
        branchVo2.setName("徐家汇013");
        branchVo2.setCompany("工商");
        branchVo2.setAddress("上海3");
        strings.add(branchVo2);
        WorkNode_Vo branchVo3 = new WorkNode_Vo();
        branchVo3.setAllresult("徐家汇08中行上海4");
        branchVo3.setIds("18");
        branchVo3.setName("徐家汇08");
        branchVo3.setCompany("中行");
        branchVo3.setAddress("上海4");
        strings.add(branchVo3);
        WorkNode_Vo branchVo4 = new WorkNode_Vo();
        branchVo4.setAllresult("徐家汇06中行上海5");
        branchVo4.setIds("99");
        branchVo4.setName("徐家汇06");
        branchVo4.setCompany("中行");
        branchVo4.setAddress("上海5");
        strings.add(branchVo4);
        WorkNode_Vo branchVo5 = new WorkNode_Vo();
        branchVo5.setAllresult("徐家汇04浦发上海1");
        branchVo5.setIds("66");
        branchVo5.setName("徐家汇04");
        branchVo5.setCompany("浦发");
        branchVo5.setAddress("上海1");
        strings.add(branchVo5);
        WorkNode_Vo branchVo6 = new WorkNode_Vo();
        branchVo6.setAllresult("徐家汇11民生上海2");
        branchVo6.setIds("88");
        branchVo6.setName("徐家汇11");
        branchVo6.setCompany("民生");
        branchVo6.setAddress("上海2");
        strings.add(branchVo6);

        WorkNode_Vo branchVo7 = new WorkNode_Vo();
        branchVo7.setAllresult("徐家汇01民生上海1");
        branchVo7.setCompany("民生");
        branchVo7.setIds("11");
        branchVo7.setName("徐家汇01");
        branchVo7.setAddress("上海1");
        strings.add(branchVo7);
        for(WorkNode_Vo name : strings){
            workNodeDao.create(name);
        }

    }

    //  模糊查询 网点名称
    public class BranchAdpater extends BaseAdapter implements Filterable {

        public Context context;
        private List<WorkNode_Vo> workNode_vos;
        private int type ;

        public BranchAdpater(Context context,List<WorkNode_Vo> workNode_vos,int type){
            this.context =context;
            this.workNode_vos = workNode_vos;
            this.type = type;
        }

        @Override
        public int getCount() {
            return workNode_vos == null?0:workNode_vos.size();
        }

        @Override
        public Object getItem(int position) {
            return workNode_vos.get(position);
        }

        public void  setList(List<WorkNode_Vo> list){
            workNode_vos = list;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView r = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.net_catchmodel, null);
                r= (TextView)convertView.findViewById(R.id.net_catchmodel);
                convertView.setTag(r);
            }else{
                r = (TextView)convertView.getTag();
            }

            WorkNode_Vo result = workNode_vos.get(position);
            r.setText(result.getName());
            return convertView;
        }

        @Override
        public android.widget.Filter getFilter() {
            if (mFilter == null) {
                mFilter = new ArrayFilter(type);
            }
            return mFilter;
        }
    }

    public  class ArrayFilter extends Filter {
        public int type ;

        public ArrayFilter (int type){
            this.type = type;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (prefix == null || prefix.length() == 0) {
            } else {
                workNode_vos = workNodeDao.getAll("allresult" , prefix.toString());
                results.values = workNode_vos;
                results.count = workNode_vos.size();
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            workNode_vos = (List<WorkNode_Vo>) results.values;
            if (results.count > 0) {
                branchAdpater.setList(workNode_vos);
                branchAdpater.notifyDataSetChanged();
            } else {
                branchAdpater.notifyDataSetInvalidated();
            }
        }
    }


    //关闭键盘
    private  void closeInput(View v){
        InputMethodManager inputMethodManager =
                (InputMethodManager)WorkNodeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }



    //  模糊查询  所属客户
    public class BranchAdpater1 extends BaseAdapter implements Filterable{

        public Context context;
        private List<WorkNode_Vo> workNode_vos;
        private int type ;

        public BranchAdpater1(Context context,List<WorkNode_Vo> workNode_vos,int type){
            this.context =context;
            this.workNode_vos = workNode_vos;
            this.type = type;
        }

        @Override
        public int getCount() {
            return workNode_vos == null?0:workNode_vos.size();
        }

        @Override
        public Object getItem(int position) {
            return workNode_vos.get(position);
        }

        public void  setList(List<WorkNode_Vo> list){
            workNode_vos = list;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView  r = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.net_catchmodel, null);
                r= (TextView)convertView.findViewById(R.id.net_catchmodel);
                convertView.setTag(r);
            }else{
                r = (TextView)convertView.getTag();
            }

            WorkNode_Vo result = workNode_vos.get(position);
            r.setText(result.getCompany());



            return convertView;
        }

        @Override
        public android.widget.Filter getFilter() {
            if (mCustomer == null) {
                mCustomer = new ArrayFilterAtmCustomerName(type);
            }
            return mCustomer;
        }
    }

    public class ArrayFilterAtmCustomerName extends Filter {
        public int type;

        public ArrayFilterAtmCustomerName(int type) {
            this.type = type;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (prefix == null || prefix.length() == 0) {
            } else {
                workNode_vos = workNodeDao.getAllCustomer("allresult", prefix.toString());
                results.values = workNode_vos;
                results.count = workNode_vos.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            workNode_vos = (List<WorkNode_Vo>) results.values;

            if (results.count > 0) {
                branchAdpater1.setList(workNode_vos);
                branchAdpater1.notifyDataSetChanged();
            } else {
                branchAdpater1.notifyDataSetInvalidated();
            }

        }
    }




    //  模糊查询 网点地址
    public class BranchAdpater2 extends BaseAdapter implements Filterable{

        public Context context;
        private List<WorkNode_Vo> workNode_vos;
        private int type ;

        public BranchAdpater2(Context context,List<WorkNode_Vo> workNode_vos,int type){
            this.context =context;
            this.workNode_vos = workNode_vos;
            this.type = type;
        }

        @Override
        public int getCount() {
            return workNode_vos == null?0:workNode_vos.size();
        }

        @Override
        public Object getItem(int position) {
            return workNode_vos.get(position);
        }

        public void  setList(List<WorkNode_Vo> list){
            workNode_vos = list;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView  r = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.net_catchmodel, null);
                r= (TextView)convertView.findViewById(R.id.net_catchmodel);
                convertView.setTag(r);
            }else{
                r = (TextView)convertView.getTag();
            }

            WorkNode_Vo result = workNode_vos.get(position);
            r.setText(result.getAddress());



            return convertView;
        }

        @Override
        public android.widget.Filter getFilter() {
            if (mAddress == null) {
                mAddress = new ArrayFilterAddress(type);
            }
            return mAddress;
        }
    }

    public class ArrayFilterAddress extends Filter {
        public int type;

        public ArrayFilterAddress(int type) {
            this.type = type;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (prefix == null || prefix.length() == 0) {
            } else {
                workNode_vos = workNodeDao.getAllAddress("allresult", prefix.toString());
                results.values = workNode_vos;
                results.count = workNode_vos.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            workNode_vos = (List<WorkNode_Vo>) results.values;

            if (results.count > 0) {
                branchAdpater2.setList(workNode_vos);
                branchAdpater2.notifyDataSetChanged();
            } else {
                branchAdpater2.notifyDataSetInvalidated();
            }

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(KeyEvent.KEYCODE_BACK==keyCode) {
            closeActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initImageLoader() {
        options = new DisplayImageOptions.Builder().cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565).showImageForEmptyUri(R.mipmap.pictures_no)
                .showImageOnFail(R.mipmap.pictures_no).showImageOnLoading(R.mipmap.pictures_no).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

    }
}
