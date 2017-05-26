package com.xvli.cit.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.cit.CitApplication;
import com.xvli.cit.R;
import com.xvli.cit.Util.Util;
import com.xvli.cit.activity.LoginActivity;
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

public class UserOneFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.user_job)
    TextView userJob;
    @Bind(R.id.user_depment)
    TextView userDepment;
    @Bind(R.id.btn_pro_captain)
    Button btnProCaptain;
    @Bind(R.id.btn_login_out)
    Button btnLoginOut;

    private String mParam1;
    private String mParam2;

    private LoginDao loginDao;
    private DatabaseHelper databaseHelper;


    public UserOneFragment(DatabaseHelper databaseHelper, LoginDao atm_bean) {
        this.databaseHelper = databaseHelper;
        this.loginDao = atm_bean;
    }

    public UserOneFragment() {
    }

    public static UserOneFragment newInstance(String param1, String param2) {
        UserOneFragment fragment = new UserOneFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_one, container, false);
        ButterKnife.bind(this, view);

        //设置队长信息
        HashMap<String,Object> value = new HashMap<>();
        value.put("iscaptain", true);
        List<LoginVo> loginVos = loginDao.queryAll();
        if(loginVos != null && loginVos.size() > 0){
            userName.setText(loginVos.get(0).getName());
            userJob.setText(loginVos.get(0).getJobnumber());
            userDepment.setText(loginVos.get(0).getDepartment());
        }

        return view;
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

    @OnClick({R.id.btn_pro_captain, R.id.btn_login_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pro_captain://提升队长
                break;
            case R.id.btn_login_out://退出登录
                showloginOut();
                break;
        }
    }

    private void showloginOut() {
        final Dialog dialog = new Dialog(getActivity(), R.style.loading_dialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.usre_login_out));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                loginOut();
                // 关闭上传服务
                Intent intent = new Intent(Config.Broadcast_UPLOAD_CLOSED);
                getActivity().sendBroadcast(intent);

                Intent serviceIntent = new Intent(CitApplication.getInstance(), CitService.class);
                if (Util.isServiceWork("om.xvli.cit.service.CitService")) {
                    CitApplication.getInstance().stopService(serviceIntent);
                    CitApplication.getInstance().setKillService(1);
                }
                //退出时关闭数据库
                if (databaseHelper != null) {
                    OpenHelperManager.releaseHelper();
                    databaseHelper = null;
                }

                startActivity(new Intent(getActivity(), LoginActivity.class));

                getActivity().finish();
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
