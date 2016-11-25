package com.xuli.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xuli.Bean.ChildEntity;
import com.xuli.Bean.ParentEntity;
import com.xuli.Util.CustomToast;
import com.xuli.Util.PDALogger;
import com.xuli.Util.Util;
import com.xuli.adapter.ChildAdapter;
import com.xuli.adapter.ParentAdapter;
import com.xuli.comm.Config;
import com.xuli.comm.ControlDialog;
import com.xuli.dao.TruckChildDao;
import com.xuli.dao.TruckDao;
import com.xuli.dao.TruckGroupDao;
import com.xuli.database.DatabaseHelper;
import com.xuli.monitor.R;
import com.xuli.vo.TruckChildVo;
import com.xuli.vo.TruckGroupVo;
import com.xuli.vo.TruckVo;
import com.xuli.widget.Wed_Picker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 16:05.
 */
@SuppressLint("ValidFragment")
public class DepartmentSelectorFragment extends Fragment implements ExpandableListView.OnGroupExpandListener,
        ParentAdapter.OnChildTreeViewClickListener ,View.OnClickListener,ParentAdapter.OnChildGroutpViewClickListener {
    private ExpandableListView eList;
    private ArrayList<ParentEntity> parents;
    private ParentAdapter adapter;
    private ChildAdapter childAdapter;
    private DatabaseHelper databaseHelper;
    private TruckDao truck_dao;
    private TruckChildDao child_dao;
    private TruckGroupDao group_dao;
    private List<TruckGroupVo> groupVoList;
    private List<TruckChildVo> childVoList;
    private Wed_Picker picker_loc;
    private Button btn_back_loc,bt_select_ok;
    private TextView tv_title_loc,btn_ok_loc;
    private Dialog dialog_loc;
    private MyBroadcastReceiver  broadcastReceiver;

    public DepartmentSelectorFragment(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("jack","DepartmentSelectorFragment第一个");
        View view  = inflater.inflate(R.layout.department_fragment , container , false);
        eList = (ExpandableListView) view.findViewById(R.id.eList);
        bt_select_ok = (Button) view.findViewById(R.id.bt_select_ok);
        eList.setOnGroupExpandListener(DepartmentSelectorFragment.this);
        bt_select_ok.setOnClickListener(this);




        if(broadcastReceiver == null){
            broadcastReceiver = new MyBroadcastReceiver();
            registerReceiver();
        }
        truck_dao = new TruckDao(getHelper());
        child_dao = new TruckChildDao(getHelper());
        group_dao = new TruckGroupDao(getHelper());

//        new OperAsyncTask().execute();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setDate();
            }
        });
        return view;

    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.WEBGPS);
        getContext().registerReceiver(broadcastReceiver, filter);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }




    @Override
    public void onStart() {
//        setDate();
        adapter = new ParentAdapter(getActivity(), parents,truck_dao);
        childAdapter = new ChildAdapter(getActivity());
        eList.setAdapter(adapter);
        adapter.setOnChildTreeViewClickListener(DepartmentSelectorFragment.this);
        adapter.setOnChildGroutpViewClickListener(DepartmentSelectorFragment.this);
        super.onStart();
    }


    /**
     * 初始化菜单数据源
     */
    private void setDate() {
        parents = new ArrayList<>();
        groupVoList = group_dao.queryAll();
        //一级菜单
        if (groupVoList != null && groupVoList.size() > 0) {
            for (int i = 0; i < groupVoList.size(); i++) {
                ArrayList<ChildEntity> childs = new ArrayList<>();
                ParentEntity parent = new ParentEntity();

         /*       //押运公司下边直接所有车辆总数
                HashMap<String, Object> value1 = new HashMap<>();
                value1.put("depid", groupVoList.get(i).getId());
                List<TruckVo> listVo = truck_dao.quaryForDetail(value1);
                if (listVo != null && listVo.size() > 0) {
                    parent.setGroupName(groupVoList.get(i).getName() + "(" + groupVoList.get(i).getVehionline() + "/" + listVo.size()  + ")");
                } else {*/
                parent.setGroupName(groupVoList.get(i).getName() + "(" + groupVoList.get(i).getVehionline() + "/" + groupVoList.get(i).getVehicount() + ")");
//                }

                //一级菜单下的车辆
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("depid", groupVoList.get(i).getId());
                List<TruckVo> truckList1 = truck_dao.quaryForDetail(hashMap1);
                if (truckList1 != null && truckList1.size() > 0) {
                    for (int k = 0; k < truckList1.size(); k++) {
                        ChildEntity child1 = new ChildEntity();
                        child1.setGroupName(truckList1.get(k).getPlatenumber());
                        child1.setTruchid(truckList1.get(k).getId());
                        child1.setIstruck(true);
                        child1.setIscheck(truckList1.get(k).ischeck());
                        child1.setIsonLine(truckList1.get(k).isOnline());
                        childs.add(child1);//一级菜单下的车辆详细信息  车牌号 车辆id 是否在线  是否选中
                    }
                }

                //二级部门下的车辆
                HashMap<String, Object> value = new HashMap<>();
                value.put("pid", groupVoList.get(i).getId());
                childVoList = child_dao.quaryForDetail(value);
                if (childVoList != null && childVoList.size() > 0) {
                    for (int j = 0; j < childVoList.size(); j++) {
                        ChildEntity child = new ChildEntity();
                        //二级车辆在线数量  和 总数量
                        HashMap<String, Object> value_num = new HashMap<>();
                        value_num.put("depid", childVoList.get(j).getId());
                        value_num.put("online", true);
                        List<TruckVo> truckVos = truck_dao.quaryForDetail(value_num);
                        if (truckVos != null && truckVos.size() > 0) {
//                            child.setGroupName(childVoList.get(j).getName() + "(" + truckVos.size() + "/" + childVoList.get(j).getVehicount() + ")");

                            //更新二级部门下车辆在线数量
                            HashMap<String,Object> value_online = new HashMap<>();
                            value_online.put("id",childVoList.get(j).getId());
                            List<TruckChildVo> truckChildVos = child_dao.quaryForDetail(value_online);
                            if (truckChildVos != null && truckChildVos.size() > 0) {
                                PDALogger.d("size--->"+truckChildVos.size());
                                PDALogger.d("online--->"+truckVos.size());
                                TruckChildVo childVo = truckChildVos.get(0);
                                childVo.setVehionline(truckVos.size());
                                child_dao.upDate(childVo);
                            }

                            child.setGroupName(childVoList.get(j).getName() + "(" + childVoList.get(j).getVehionline() + "/" + childVoList.get(j).getVehicount() + ")");
                        } else {
                            child.setGroupName(childVoList.get(j).getName() + "(" + childVoList.get(j).getVehionline() + "/" + childVoList.get(j).getVehicount() + ")");
                        }

                        child.setIstruck(false);
                        //三级菜单的详细信息  车牌号 车辆id 是否在线  是否选中
                        ArrayList<String> childNames = new ArrayList<>();
                        ArrayList<String> truchId = new ArrayList<>();
                        ArrayList<Boolean> truchIsOnline = new ArrayList<>();
                        ArrayList<Boolean> truchIsCheck = new ArrayList<>();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("depid", childVoList.get(j).getId());
                        List<TruckVo> truckList = truck_dao.quaryForDetail(hashMap);
                        if (truckList != null && truckList.size() > 0) {
                            for (int k = 0; k < truckList.size(); k++) {
                                childNames.add(truckList.get(k).getPlatenumber());
                                truchId.add(truckList.get(k).getId());
                                truchIsOnline.add(truckList.get(k).isOnline());
                                truchIsCheck.add(truckList.get(k).ischeck());
                            }
                        }
                        child.setChildNames(childNames);
                        child.setCarId(truchId);
                        child.setCarIsOnline(truchIsOnline);
                        child.setCarIsCheck(truchIsCheck);
                        childs.add(child);
                    }
                }
                parent.setChilds(childs);
                parents.add(parent);
            }
        }
    }




    /**
     * 点击子ExpandableListView的子项时，回调本方法，根据下标获取值来做相应的操作//只有车辆 在线 并且选中 才可执行车辆小功能
     */
    @Override
    public void onClickPosition(int parentPosition, int groupPosition, int childPosition,boolean isCheck,boolean isOnline) {
        String childName = parents.get(parentPosition).getChilds()
                .get(groupPosition).getChildNames().get(childPosition)
                .toString();
//        Toast.makeText(
//                getActivity(),
//                "点击的下标为： parentPosition=" + parentPosition
//                        + "   groupPosition=" + groupPosition
//                        + "   childPosition=" + childPosition + "\n点击的是："
//                        + childName, Toast.LENGTH_SHORT).show();

        PDALogger.d("1111isCheck---->" + isCheck + "===========isOnline--->" + isOnline);
        if (isCheck && isOnline) {
            showLocDialog();
        }
    }

    /**
     * 展开一项，关闭其他项，保证每次只能展开一项
     */
    @Override
    public void onGroupExpand(int groupPosition) {
        for (int i = 0; i < parents.size(); i++) {
            if (i != groupPosition) {
                eList.collapseGroup(i);
            }
        }
    }



    @Override
    public void onClick(View v) {
        if(v == btn_back_loc){  //功能选择返回按钮
            dialog_loc.cancel();
        }else if(v == btn_ok_loc){//功能选择确定按钮
            String result =  picker_loc.getresult();
            Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
            dialog_loc.cancel();
            Util.copyDB();
        } else if(v == bt_select_ok){//是否监控
            HashMap<String, Object> value = new HashMap<>();
            value.put("ischeck", true);
            value.put("online", true);
            List<TruckVo> truckVoList = truck_dao.quaryForDetail(value);
            if (truckVoList != null && truckVoList.size() > 0) {
                PDALogger.d("truckVoList.size()--->" + truckVoList.size());
                if (truckVoList.size() > 5) {
                    CustomToast.getInstance().showShortToast(getString(R.string.toast_select_no));
                } else {
                    new ControlDialog(getActivity(), truckVoList, truckVoList.size()).show();
                }
            } else {
                CustomToast.getInstance().showShortToast(getString(R.string.toast_select_tip));
            }

        }

    }

    //创建数据库控制器
    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }
    public void showLocDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.dialog_wed_picker, null);// 得到加载view
        picker_loc=(Wed_Picker) v.findViewById(R.id.picker);
        btn_back_loc=(Button) v.findViewById(R.id.btn_back);
        btn_ok_loc=(TextView) v.findViewById(R.id.btn_ok);
        tv_title_loc=(TextView) v.findViewById(R.id.tv_title);
        tv_title_loc.setText(getResources().getString(R.string.add_wedge_dialog_choose_result));
        btn_back_loc.setOnClickListener(this);
        btn_ok_loc.setOnClickListener(this);

        dialog_loc = new Dialog(getActivity(), R.style.loading_dialog);// 创建自定义样式dialog
        dialog_loc.setContentView(v);
        //底部宽度为MATCH_PARENT
        Window dialogWindow = dialog_loc.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);

        dialog_loc.show();
    }

    //只有车辆 在线 并且选中 才可执行车辆小功能
    @Override
    public void onChlidClickPosition(int parentPosition, int groupPosition, String plantName,boolean isCheck,boolean isOnline) {

        PDALogger.d("2222isCheck---->" + isCheck + "=========isOnline--->" + isOnline);
        if (isCheck && isOnline) {
            showLocDialog();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(broadcastReceiver);
    }

   class OperAsyncTask extends AsyncTask<String, Void, Void>{

       @Override
       protected Void doInBackground(String... params) {
           setDate();
           return null;
       }
   }
}
