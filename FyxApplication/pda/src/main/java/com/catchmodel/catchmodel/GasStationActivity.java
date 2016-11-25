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

import com.catchmodel.been.GasStation_Vo;
import com.catchmodel.been.SaveAllDataVo;
import com.catchmodel.dao.GasStationDao;
import com.catchmodel.dao.SaveAllDataVoDao;
import com.catchmodel.gps.CatchGPS;
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
 * Created by Administrator on 17:24.
 */
public class GasStationActivity  extends BaseActivity  implements View.OnClickListener {

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
    private CatchGPS catchGPS;
    private ArrayFilter mFilter;
    private ArrayFilterAddress  mAddress;
    private BranchAdpater  branchAdpater;
    private BranchAdpater1  branchAdpater1;
    private boolean  isStart = true;
    private String result ;  //用于记录上次搜索数据
    private File  imageFile;
    private SaveAllDataVoDao saveAllDataVoDao;//保存网点采集信息
    private boolean isNetPhoto = false;//网点位置是否拍照
    private boolean isCarPhoto = false;//停车点是否拍照
    private  SaveAllDataVo saveAllDataVo ;
    private Button address_gasstation , name_gasstation,submit ,save,btn_back ;
    private GasStationDao  gasStationDao;
    private List<GasStation_Vo> gasStation_vos ;
    private AutoCompleteTextView gasstation_name ,gasstation_address;
    private List<LoginVo> users;
    private LoginDao login_dao;
    private String allJobNum;
    private ConfigVoDao configVoDao;
    private List<ConfigVo> configVos;
    private int day;
    private Bitmap bitmap;
//    private Bitmap bitmap1;
//    private Bitmap bitmap2;
    private TextView tv_title,btn_ok;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasstation_catchmodel);
        initImageLoader();
        initView();
    }


    private void initView() {
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.GasStation_catchmodel));
        saveAllDataVo = new SaveAllDataVo();
        saveAllDataVoDao = new SaveAllDataVoDao(getHelper());
        gasStationDao = new GasStationDao(getHelper());
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

        btn_ok = (TextView)findViewById(R.id.btn_ok);
        btn_ok.setVisibility(View.GONE);
        gasstation_name = (AutoCompleteTextView) findViewById(R.id.gasstation_name);
        gasstation_address = (AutoCompleteTextView) findViewById(R.id.gasstation_address);
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
        name_gasstation = (Button) findViewById(R.id.name_gasstation);
        address_gasstation = (Button) findViewById(R.id.address_gasstation);
        save = (Button) findViewById(R.id.save);
        submit = (Button) findViewById(R.id.submit);

//        data();//假数据

        SaveAllDataVo saveAllDataVo = (SaveAllDataVo) getIntent().getSerializableExtra("saveAllDataVo");
        String type = getIntent().getStringExtra("type");
        if (saveAllDataVo != null && type != null) {
            gasstation_name.setFocusable(false);
            gasstation_name.setText(saveAllDataVo.getName());
            gasstation_address.setFocusable(false);
            gasstation_address.setText(saveAllDataVo.getAddress());

            name_gasstation.setVisibility(View.GONE);
            address_gasstation.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            submit.setVisibility(View.INVISIBLE);

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
            init(1);//1 加油站名称
            init(2);//2 加油站地址
        }

    }

    private void  init(int type){
        if(type == 1){
            final List<GasStation_Vo> gasStation_vos = gasStationDao.getNameDISTINCT();
            branchAdpater = new BranchAdpater(this ,gasStation_vos,type);
            gasstation_name.setAdapter(branchAdpater);

            gasstation_name.setThreshold(0);
            gasstation_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GasStation_Vo gasStationVo = (GasStation_Vo) parent.getItemAtPosition(position);
                    if(!TextUtils.isEmpty(gasStationVo.getName())){
                        gasstation_name.setText(gasStationVo.getName().toString());
                        final List<GasStation_Vo> gasStation_vos1 = gasStationDao.getAddessDISTINCT(gasstation_name.getText().toString());
                        if(gasStation_vos1!=null ){
                            if(gasStation_vos1.size() ==1){
                                gasstation_address.setText(gasStation_vos1.get(0).getAddress());
                            }else{
                                gasstation_address.setText("");

                            }
                        }else{
                            gasstation_address.setText("");
                        }

                    }

                    closeInput(gasstation_name);
                }
            });

            name_gasstation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PDALogger.d("show =" + name_gasstation);
                    branchAdpater.setList(gasStation_vos);
                    branchAdpater.notifyDataSetChanged();
                    gasstation_name.showDropDown();
                }
            });
        }

        if(type == 2){
            final List<GasStation_Vo> gasStation_vos = gasStationDao.getAddessDISTINCT();
            branchAdpater1 = new BranchAdpater1(this ,gasStation_vos,type);
            gasstation_address.setAdapter(branchAdpater1);
            gasstation_address.setThreshold(0);
            gasstation_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GasStation_Vo gasStationVo = (GasStation_Vo) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(gasStationVo.getAddress())) {
                        gasstation_address.setText(gasStationVo.getAddress().toString());
                    } else {
                        gasstation_address.setText("");
                    }

                    closeInput(gasstation_address);
                }
            });
            address_gasstation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PDALogger.d("show =" + address_gasstation);
                    if(!TextUtils.isEmpty(address_gasstation.getText().toString())){
                        final List<GasStation_Vo> gasStation_vos1 =	gasStationDao.getAddessDISTINCT();
                        if(gasStation_vos1!=null&& gasStation_vos1.size()>0){
                            branchAdpater1.setList(gasStation_vos1);
                            branchAdpater1.notifyDataSetChanged();
                            gasstation_address.showDropDown();
                        }else{
                            branchAdpater1.setList(gasStation_vos);
                            branchAdpater1.notifyDataSetChanged();
                            gasstation_address.showDropDown();
                        }

                    }else{
                        branchAdpater1.setList(gasStation_vos);
                        branchAdpater1.notifyDataSetChanged();
                        gasstation_address.showDropDown();
                    }

                }
            });

        }

    }


    public void SubMitData() {
        String Net_Name;
        //加油站名称是否匹配
        if (TextUtils.isEmpty(gasstation_name.getText().toString())) {//加油站名称不能为空
            gasstation_name.setGravity(Gravity.CENTER_VERTICAL);
            gasstation_name.setHintTextColor(Color.RED);
            gasstation_name.setHint(R.string.not_isEmpty);
            gasstation_name.requestFocus();
            return;
        } else {
            Net_Name = gasstation_name.getText().toString();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Name", Net_Name);
            gasStation_vos = gasStationDao.quaryForDetail(hashMap);
            if (gasStation_vos != null && gasStation_vos.size() > 0) {
                saveAllDataVo.setIds(gasStation_vos.get(0).getIds());
                saveAllDataVo.setName(gasStation_vos.get(0).getName());
            } else {//加油站名称 需要和接口数据匹配
                CustomToast.getInstance().showShortToast(R.string.not_branch);
                return;
            }
        }


        //加油站地址是否匹配
        if (TextUtils.isEmpty(gasstation_address.getText().toString())) {
            gasstation_address.setGravity(Gravity.CENTER_VERTICAL);
            gasstation_address.setHintTextColor(Color.RED);
            gasstation_address.setHint(R.string.not_isEmpty);
            gasstation_address.requestFocus();
            return;
        } else {
            String Net_address = gasstation_address.getText().toString();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Name", Net_Name);
            hashMap.put("Address", Net_address);
            gasStation_vos = gasStationDao.quaryForDetail(hashMap);
            if (gasStation_vos != null && gasStation_vos.size() > 0) {
                saveAllDataVo.setIds(gasStation_vos.get(0).getIds());
                saveAllDataVo.setName(gasStation_vos.get(0).getName());
                saveAllDataVo.setAddress(Net_address);

            } else {//网点名称 需要和接口数据匹配
                CustomToast.getInstance().showShortToast(R.string.branch_customer_adress);
                return;
            }
        }

        if (get_remark_gpstop.getText().toString().equals("") || get_remark_gpstop.getText().toString() == null) {
            //加油站位置不能为空
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


        saveAllDataVo.setType("GasStation");
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
            PDALogger.d("bitmapG="+bitmap);
            bitmap.recycle();
            bitmap=null;
        }
//
//
//        if(bitmap1 !=null && !bitmap1.isRecycled()){
//            PDALogger.d("bitmapG1="+bitmap1);
//            bitmap1.recycle();
//            bitmap1=null;
//        }
//
//        if(bitmap2 !=null && !bitmap2.isRecycled()){
//            PDALogger.d("bitmapG2="+bitmap2);
//            bitmap2.recycle();
//            bitmap2=null;
//        }

        System.gc();
        this.finish();
    }





    private void data(){
        List<GasStation_Vo>  strings = new ArrayList<>();
        GasStation_Vo branchVo = new GasStation_Vo();
        branchVo.setAllresult("徐家汇01建行上海1");

        branchVo.setIds("11");
        branchVo.setName("徐家汇01");
        branchVo.setAddress("上海1");
        strings.add(branchVo);
        GasStation_Vo branchVo1 = new GasStation_Vo();
        branchVo1.setAllresult("徐家汇012招商上海2");
        branchVo1.setIds("1");
        branchVo1.setName("徐家汇012");
        branchVo1.setAddress("上海2");
        strings.add(branchVo1);
        GasStation_Vo branchVo2 = new GasStation_Vo();
        branchVo2.setAllresult("徐家汇013工商上海3");
        branchVo2.setIds("55");
        branchVo2.setName("徐家汇013");
        branchVo2.setAddress("上海3");
        strings.add(branchVo2);
        GasStation_Vo branchVo3 = new GasStation_Vo();
        branchVo3.setAllresult("徐家汇08中行上海4");
        branchVo3.setIds("18");
        branchVo3.setName("徐家汇08");
        branchVo3.setAddress("上海4");
        strings.add(branchVo3);
        GasStation_Vo branchVo4 = new GasStation_Vo();
        branchVo4.setAllresult("徐家汇06中行上海5");
        branchVo4.setIds("99");
        branchVo4.setName("徐家汇06");
        branchVo4.setAddress("上海5");
        strings.add(branchVo4);
        GasStation_Vo branchVo5 = new GasStation_Vo();
        branchVo5.setAllresult("徐家汇04浦发上海1");
        branchVo5.setIds("66");
        branchVo5.setName("徐家汇04");
        branchVo5.setAddress("上海1");
        strings.add(branchVo5);
        GasStation_Vo branchVo6 = new GasStation_Vo();
        branchVo6.setAllresult("徐家汇11民生上海2");
        branchVo6.setIds("88");
        branchVo6.setName("徐家汇11");
        branchVo6.setAddress("上海2");
        strings.add(branchVo6);

        GasStation_Vo branchVo7 = new GasStation_Vo();
        branchVo7.setAllresult("徐家汇01民生上海1");
        branchVo7.setIds("11");
        branchVo7.setName("徐家汇01");
        branchVo7.setAddress("上海1");
        strings.add(branchVo7);
        for(GasStation_Vo name : strings){
            gasStationDao.create(name);
        }

    }




    //  模糊查询 网点名称
    public class BranchAdpater extends BaseAdapter implements Filterable {

        public Context context;
        private List<GasStation_Vo> gasStation_vos;
        private int type ;

        public BranchAdpater(Context context,List<GasStation_Vo> gasStation_vos,int type){
            this.context =context;
            this.gasStation_vos = gasStation_vos;
            this.type = type;
        }

        @Override
        public int getCount() {
            return gasStation_vos == null?0:gasStation_vos.size();
        }

        @Override
        public Object getItem(int position) {
            return gasStation_vos.get(position);
        }

        public void  setList(List<GasStation_Vo> list){
            gasStation_vos = list;
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

            GasStation_Vo result = gasStation_vos.get(position);
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
                gasStation_vos = gasStationDao.getAll("allresult" , prefix.toString());
                results.values = gasStation_vos;
                results.count = gasStation_vos.size();

            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            gasStation_vos = (List<GasStation_Vo>) results.values;
            if (results.count > 0) {
                branchAdpater.setList(gasStation_vos);
                branchAdpater.notifyDataSetChanged();
            } else {
                branchAdpater.notifyDataSetInvalidated();
            }
        }
    }


    //关闭键盘
    private  void closeInput(View v){
        InputMethodManager inputMethodManager =
                (InputMethodManager)GasStationActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }



    //  模糊查询  所属客户
    public class BranchAdpater1 extends BaseAdapter implements Filterable{

        public Context context;
        private List<GasStation_Vo> gasStation_vos;
        private int type ;

        public BranchAdpater1(Context context,List<GasStation_Vo> gasStation_vos,int type){
            this.context =context;
            this.gasStation_vos = gasStation_vos;
            this.type = type;
        }

        @Override
        public int getCount() {
            return gasStation_vos == null?0:gasStation_vos.size();
        }

        @Override
        public Object getItem(int position) {
            return gasStation_vos.get(position);
        }

        public void  setList(List<GasStation_Vo> list){
            gasStation_vos = list;
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

            GasStation_Vo result = gasStation_vos.get(position);
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
                gasStation_vos = gasStationDao.getAllAddress("allresult", prefix.toString());
                results.values = gasStation_vos;
                results.count = gasStation_vos.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            gasStation_vos = (List<GasStation_Vo>) results.values;

            if (results.count > 0) {
                branchAdpater1.setList(gasStation_vos);
                branchAdpater1.notifyDataSetChanged();
            } else {
                branchAdpater1.notifyDataSetInvalidated();
            }

        }
    }

    @Override
    public void onClick(View v) {

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
