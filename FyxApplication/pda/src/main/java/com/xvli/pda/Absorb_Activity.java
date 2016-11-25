package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.andoird.mytools.ui.adapterview.DataHolder;
import com.andoird.mytools.ui.adapterview.GenericAdapter;
import com.andoird.mytools.ui.adapterview.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BankCardVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.TmrPhotoVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.dao.BankCardDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.TmrPhotoDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 吞卡界面
 *
 */
public class Absorb_Activity extends BaseActivity implements OnClickListener
{
    private Button btn_back;
    private TextView tv_title,btn_ok;
    private ListView lv_atmcheck;
    private View view_absorb;
    private LinearLayout ll_add;
    private GenericAdapter adapter;
    private ArrayList<DataHolder> holders;
    //添加对话框
    private Dialog dialog;
    private EditText edt_absorb_cardNub;
    private RadioGroup rg_absorb;
    private Button bt_add;
    private RadioButton rbt_mybank,rbt_otherbank;
    
    private LoginDao login_dao;
    private BankCardDao bankcard_dao;

    private UniqueAtmDao check_dao;
    private String clientid;
    private UniqueAtmVo atm_bean;
    private List<LoginVo> users;
    public static final int requestCode = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_check_comm);
        
        login_dao=new LoginDao(getHelper());
        bankcard_dao=new BankCardDao(getHelper());
        check_dao=new UniqueAtmDao(getHelper());
        users = login_dao.queryAll();
        clientid=users.get(users.size()-1).getClientid();
        
        Action action=(Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        atm_bean=(UniqueAtmVo) action.getCommObj();
        
        initeview();
        initListView();
        
    }
    public void initeview()
    {
        btn_back=(Button) findViewById(R.id.btn_back);
        btn_ok=(TextView) findViewById(R.id.btn_ok);
        tv_title=(TextView) findViewById(R.id.tv_title);
        lv_atmcheck=(ListView) findViewById(R.id.lv_atmcheck);
        ll_add=(LinearLayout) findViewById(R.id.ll_add);
        view_absorb=findViewById(R.id.view_absorb);
        
        tv_title.setText(getResources().getString(R.string.add_absorb_title));
        
        ll_add.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_ok.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        if(v==btn_back||v==btn_ok)
        {
            finish();
            //把吞卡图片放入TmrPhoto数据库

            List<BankCardVo> bankCardVos = bankcard_dao.queryAll();
            if (bankCardVos != null && bankCardVos.size() > 0) {
                for (int i = 0; i < bankCardVos.size(); i++) {
                    if (!TextUtils.isEmpty(bankCardVos.get(i).getPhoto())) {

                        TmrPhotoDao photoDao = new TmrPhotoDao(getHelper());
                        TmrPhotoVo photoVo = new TmrPhotoVo();
                        photoVo.setClientid(clientid);
                        photoVo.setUuid(UUID.randomUUID().toString());
                        photoVo.setOperatedtime(Util.getNowDetial_toString());
                        photoVo.setTaskid(bankCardVos.get(i).getTaskid());
                        photoVo.setAtmid(bankCardVos.get(i).getAtmid());
                        photoVo.setOperator(bankCardVos.get(i).getOperator());
                        photoVo.setOperatedtime(bankCardVos.get(i).getOperatetime());
                        photoVo.setPhonepath(bankCardVos.get(i).getPhoto());
                        photoVo.setRemarks(getResources().getString(R.string.add_atmtoolcheck_absorb));
                        if(photoDao.contentsNumber(photoVo) > 0){

                        } else {
                            photoDao.create(photoVo);
                        }
                    }
                }
            }
            }
        else if(v==ll_add)
        {
            showDialog();
        }
        else if(v==bt_add)
        {
            if(!TextUtils.isEmpty(edt_absorb_cardNub.getText()))
            {
                BankCardVo bean=new BankCardVo();
                bean.setBranchid(atm_bean.getBranchid());
                bean.setAtmid(atm_bean.getAtmid());
                bean.setCardno(edt_absorb_cardNub.getText().toString());
                if(rg_absorb.getCheckedRadioButtonId()==rbt_mybank.getId())
                    bean.setIsown("Y");
                else
                    bean.setIsown("N");
                bean.setOperatetime(Util.getNowDetial_toString());
                bean.setTaskid(atm_bean.getTaskid());
                bean.setClientid(clientid);
                
                bean.setOperator(users.get(users.size() - 1).getJobnumber1() + "," + users.get(users.size() - 1).getJobnumber2());
                bean.setUuid(UUID.randomUUID().toString());
                bankcard_dao.create(bean);
                dialog.dismiss();
                initListView();
            }
            else 
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_absorb_dialog_tip));
        }
    }
    
    /**
     * 设置列表
     */
    public void initListView()
    {
        Map<String, Object> where_card=new HashMap<String, Object>();
        where_card.put("clientid", clientid);
        where_card.put("isYouXiao", "Y");
        //只显示当前机具的卡钞废钞
        where_card.put("atmid", atm_bean.getAtmid());
        List<BankCardVo> cards=bankcard_dao.quaryForDetail(where_card);
        if(cards!=null&&cards.size()>0)
        {
            lv_atmcheck.setVisibility(View.VISIBLE);
            view_absorb.setVisibility(View.VISIBLE);
            if(adapter==null||holders==null)
            {
                adapter=new GenericAdapter(this);
                holders=new ArrayList<DataHolder>();
            }
            holders.clear();
            adapter.clearDataHolders();
            for(int i=0;i<cards.size();i++)
            {
                holders.add(new MyAbsorbDataholder(cards.get(i), null));
            }
            adapter.addDataHolders(holders);
            lv_atmcheck.setAdapter(adapter);
        }
        
    }
    
    class MyAbsorbDataholder extends DataHolder
    {

        public MyAbsorbDataholder(Object data, DisplayImageOptions[] options)
        {
            super(data, options);
            // TODO Auto-generated constructor stub
        }

        @Override  
        public View onCreateView(Context arg0, final int arg1, Object arg2)
        {
            final BankCardVo bean=(BankCardVo) arg2;
            View view=LayoutInflater.from(arg0).inflate(R.layout.item_add_absorb, null);
            TextView tv_Absorb_customer=(TextView) view.findViewById(R.id.tv_Absorb_customer);
            TextView tv_Absorb_cardNub=(TextView) view.findViewById(R.id.tv_Absorb_cardNub);
            Button bt_Absorb_photo=(Button) view.findViewById(R.id.bt_Absorb_photo);
            Button bt_Absorb_del=(Button) view.findViewById(R.id.bt_Absorb_del);
            
            tv_Absorb_cardNub.setText(bean.getCardno());
            if("Y".equals(bean.getIsown()))
                tv_Absorb_customer.setText(getResources().getString(R.string.add_absorb_dialog_mybank));
            else
                tv_Absorb_customer.setText(getResources().getString(R.string.add_absorb_dialog_otherbank));
            
            bt_Absorb_photo.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub
                    // 检查SD卡是否存在
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                    {
                        CustomToast.getInstance().showToast(Absorb_Activity.this, getResources().getString(R.string.add_absorb_photo_nosdcard), 1500);
                    } else
                    {
                        Intent i = new Intent();
                        Action action=new Action();
                        action.setCommObj(bean);
                        i.putExtra(BaseActivity.EXTRA_ACTION, action);
                        i.setClass(Absorb_Activity.this, TakePhotoActivity.class); 
                        Absorb_Activity.this.startActivityForResult(i, requestCode);
                    }
                }
            });
            bt_Absorb_del.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    showConfirmDialog(bean, arg1);

                }
            });
            
            
            ViewHolder viewholder=new ViewHolder(tv_Absorb_customer,tv_Absorb_cardNub,bt_Absorb_photo,bt_Absorb_del);
            view.setTag(viewholder);
            
            return view;
        }

        @Override
        public void onUpdateView(Context arg0, final int arg1, View arg2, Object arg3)
        {
            final BankCardVo bean=(BankCardVo) arg3;
            TextView tv_Absorb_customer=(TextView) ((ViewHolder)arg2.getTag()).getParams()[0];
            TextView tv_Absorb_cardNub=(TextView) ((ViewHolder)arg2.getTag()).getParams()[1];
            Button bt_Absorb_photo=(Button) ((ViewHolder)arg2.getTag()).getParams()[2];
            Button bt_Absorb_del=(Button) ((ViewHolder)arg2.getTag()).getParams()[3];
            
            tv_Absorb_cardNub.setText(bean.getCardno());
            if("Y".equals(bean.getIsown()))
                tv_Absorb_customer.setText(getResources().getString(R.string.add_absorb_dialog_mybank));
            else
                tv_Absorb_customer.setText(getResources().getString(R.string.add_absorb_dialog_otherbank));
            
            bt_Absorb_photo.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub
                    // 检查SD卡是否存在
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                    {
                        CustomToast.getInstance().showToast(Absorb_Activity.this, getResources().getString(R.string.add_absorb_photo_nosdcard), 1500);
                    } else
                    {
                        Intent i = new Intent();
                        Action action=new Action();
                        action.setCommObj(bean);
                        i.putExtra(BaseActivity.EXTRA_ACTION, action);
                        i.setClass(Absorb_Activity.this, TakePhotoActivity.class); 
                        Absorb_Activity.this.startActivityForResult(i, requestCode);
                    }
                }
            });
            bt_Absorb_del.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v) {
                    showConfirmDialog(bean, arg1);
                }
            });
        }
    }
    
    /**
	 * 删除前确认
	 */
	private void showConfirmDialog(final BankCardVo bean,final int arg1) {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(this, R.style.loading_dialog);
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_againscan_yon, null);
		Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
		Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
		TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
		tv_tip.setText(getResources().getString(R.string.picture_save_data));
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
                adapter.removeDataHolder(arg1);
                bean.setIsYouXiao("N");
                bean.setIsUploaded("N");
                bankcard_dao.upDate(bean);

                if(adapter.getCount()==0)
                {
                    lv_atmcheck.setVisibility(View.GONE);
                    view_absorb.setVisibility(View.GONE);
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

    public void showDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_absorb_add, null);// 得到加载view
        // main.xml中的ImageView
        edt_absorb_cardNub=(EditText) v.findViewById(R.id.edt_absorb_cardNub);
        rg_absorb=(RadioGroup) v.findViewById(R.id.rg_absorb);
        bt_add=(Button) v.findViewById(R.id.bt_add);
        rbt_mybank=(RadioButton) v.findViewById(R.id.rbt_mybank);
        rbt_otherbank=(RadioButton) v.findViewById(R.id.rbt_otherbank);
        
        dialog = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
        dialog.setContentView(v);
        bt_add.setOnClickListener(this);
        
        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 1000)
        { 
             if(resultCode == android.app.Activity.RESULT_CANCELED)
             { 

             }
             else if(resultCode == android.app.Activity.RESULT_OK)
             { 
                 Bundle extras = data.getExtras(); 
                 if(extras != null)
                 {
                     Action action =(Action) extras.getSerializable("store");
                     BankCardVo bean=(BankCardVo) action.getCommObj();
                     
                     String url=bean.getPhoto();
                     try
                    {
                        bean.setPhoto(bean.getPhoto());
//                        bean.setPhoto(bitmaptoString(convertBitmap(new File(url))));
                        bankcard_dao.upDate(bean);
                    } catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                     
                 } 
             }
        }
            
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    public String bitmaptoString(Bitmap bitmap)
    {
        
      //将Bitmap转换成字符串
      String string=null;
      ByteArrayOutputStream bStream=new ByteArrayOutputStream();
      bitmap.compress(CompressFormat.PNG,100,bStream);
      byte[]bytes=bStream.toByteArray();
      string=Base64.encodeToString(bytes,Base64.DEFAULT);
      return string;
    }
    
    private Bitmap convertBitmap(File file) throws IOException {
        Bitmap bitmap = null;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        BitmapFactory.decodeStream(fis, null, o);
        fis.close();
        final int REQUIRED_SIZE = 100;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                break;
            width_tmp /= 3;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inSampleSize = scale;
        fis = new FileInputStream(file.getAbsolutePath());
        bitmap = BitmapFactory.decodeStream(fis, null, op);
        fis.close();
        // 保存压缩图片 替换临时图片
        FileOutputStream out = new FileOutputStream(file);
        if (bitmap.compress(CompressFormat.JPEG, 100, out)) {
            out.flush();
            out.close();
        }
        return bitmap;
    }
}
