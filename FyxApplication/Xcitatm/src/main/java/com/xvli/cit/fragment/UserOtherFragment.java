package com.xvli.cit.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.cit.CitApplication;
import com.xvli.cit.R;
import com.xvli.cit.Util.CustomToast;
import com.xvli.cit.Util.PDALogger;
import com.xvli.cit.Util.Util;
import com.xvli.cit.activity.BaseActivity;
import com.xvli.cit.activity.LoginActivity;
import com.xvli.cit.activity.TestScanActivity;
import com.xvli.cit.adapter.CommonAdapter;
import com.xvli.cit.adapter.ViewHolder;
import com.xvli.cit.comm.Config;
import com.xvli.cit.dao.LoginDao;
import com.xvli.cit.database.DatabaseHelper;
import com.xvli.cit.service.CitService;
import com.xvli.cit.vo.LoginVo;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserOtherFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.list_user)
    ListView listUser;
    @Bind(R.id.login_out)
    Button btnLoginOut;
    @Bind(R.id.btn_login_in)
    Button btnLoginIn;

    private String mParam1;
    private String mParam2;

    private LoginDao loginDao;
    private DatabaseHelper databaseHelper;
    private int chickWitch;//点击了哪一个  1是签出   2是签入
    private int signOk;//签到成功  1是签到   2是条码错误  3  未签入不可进行签出


    public UserOtherFragment(DatabaseHelper databaseHelper, LoginDao atm_bean) {
        this.databaseHelper = databaseHelper;
        this.loginDao = atm_bean;
    }

    public UserOtherFragment() {
    }

    public static UserOtherFragment newInstance(String param1, String param2) {
        UserOtherFragment fragment = new UserOtherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_other, container, false);
        ButterKnife.bind(this, view);
        InitListView();//设值

        return view;
    }

    //listview  设值
    private void InitListView() {
        HashMap<String, Object> value = new HashMap<>();
        value.put("iscaptain", false);
        List<LoginVo> loginVoList = loginDao.quaryForDetail(value);
        if (loginVoList != null && loginVoList.size() > 0) {
            listUser.setAdapter(new CommonAdapter<LoginVo>(getActivity(), R.layout.item_user_info, loginVoList) {
                @Override
                protected void convert(ViewHolder viewHolder, LoginVo item, int position) {
                    viewHolder.setText(R.id.user_name, item.getName());
                    viewHolder.setText(R.id.user_job, item.getJobnumber());
                    viewHolder.setText(R.id.user_depment, item.getDepartment());
                    int userstate = item.getUserstate();
                    if (userstate == 1) {
                        viewHolder.setText(R.id.user_state, getResources().getString(R.string.state_user_in));
                        viewHolder.setTextColorRes(R.id.user_state, R.color.gray_color);
                    } else if (userstate == 2) {
                        viewHolder.setText(R.id.user_state, getResources().getString(R.string.state_login_in));
                        viewHolder.setTextColorRes(R.id.user_state, R.color.blue_color);
                    } else {
                        viewHolder.setText(R.id.user_state, getResources().getString(R.string.state_login_out));
                        viewHolder.setTextColorRes(R.id.user_state, R.color.red_color);
                    }

                }
            });
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.login_out, R.id.btn_login_in})
    public void onClick(View view) {
        switch (view.getId()) {//签出
            case R.id.login_out:
                chickWitch = 1;
                showUserInOut(chickWitch,"");

                break;
            case R.id.btn_login_in: // 签入
                chickWitch = 2;
                showUserInOut(chickWitch,"");
                break;
        }
    }


    //扫描结果返回值
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.ZBAR_SCANNER_REQUEST:
                if (resultCode == Config.ZBAR_SCANNER_RESULT) {
                    //验证扫描结果
                    String result = data.getStringExtra("result");
                    showUserInOut(chickWitch,result);
                    PDALogger.d("result---->" + data.getStringExtra("result"));
                }
                break;
        }
    }

    //签出 签入 对话框
    private void showUserInOut(int witch,final String code) {
        final Dialog dialog = new Dialog(getActivity(), R.style.loading_dialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user_scan, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_head);
        final EditText edit_jobnum = (EditText) view.findViewById(R.id.edit_jobnum);
        if (witch == 1) {//签出
            tv_tip.setText(getResources().getString(R.string.user_other) + getResources().getString(R.string.btn_login_out));
        } else {
            tv_tip.setText(getResources().getString(R.string.user_other) + getResources().getString(R.string.btn_login_in));
        }
        ImageView img_scan = (ImageView) view.findViewById(R.id.img_scan);
        img_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isCameraAvailable(getActivity())) {
                    startActivityForResult(new Intent(getActivity(), TestScanActivity.class), Config.ZBAR_SCANNER_REQUEST);
                } else {
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.carme_error));
                }
                dialog.dismiss();
            }
        });
        if(!TextUtils.isEmpty(code)){//扫描结果
            edit_jobnum.setText(code);
        }
        bt_ok.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View arg0) {
                                         if (!TextUtils.isEmpty(edit_jobnum.getText())) {
                                             String result = edit_jobnum.getText().toString();
                                             checkData(result, dialog);
                                         } else {
                                             CustomToast.getInstance().showShortToast(getResources().getString(R.string.user_error_job));
                                         }
                                     }
                                 }

        );
        bt_miss.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           dialog.cancel();
                                       }
                                   }

        );
        dialog.setContentView(view);
        dialog.show();
    }
    //验证扫描结果是否正确 除队长之外的
    private void checkData(String result,Dialog dialog) {
        HashMap<String, Object> value = new HashMap<>();
        value.put("jobnumber", result);
        value.put("iscaptain", false);
        List<LoginVo> loginList = loginDao.quaryForDetail(value);
        if (loginList != null && loginList.size() > 0) {
            LoginVo logvo = loginList.get(0);
            int userstate = logvo.getUserstate();
            if (chickWitch == 1) {//签出
                if (userstate == 3) {//已经签出
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.user_out_ok));
                } else if (userstate == 1) { // 未签到 不能签出
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.user_not_in));
                } else {
                    logvo.setUserstate(3);
                    loginDao.upDate(logvo);
                    InitListView();//刷新列表
                    dialog.dismiss();
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.user_out_success));
                }
            } else {
                if (logvo.getUserstate() == 2) {//已经签入
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.user_in_ok));
                } else {
                    logvo.setUserstate(2);
                    loginDao.upDate(logvo);
                    InitListView();//刷新列表
                    dialog.dismiss();
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.user_in_success));
                }
            }
        } else {
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.code_error));
        }

    }


}
