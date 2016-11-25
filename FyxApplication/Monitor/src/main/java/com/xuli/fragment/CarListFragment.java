package com.xuli.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xuli.adapter.CarListAdpater;
import com.xuli.adapter.DividerItemDecoration;
import com.xuli.dao.TruckDao;
import com.xuli.database.DatabaseHelper;
import com.xuli.monitor.R;
import com.xuli.vo.TruckVo;

import java.util.List;

/**
 * Created by Administrator on 16:08.
 */
public class CarListFragment  extends Fragment implements  CarListAdpater.OnRecyclerViewListener{
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private TruckDao truck_dao;
    private List<TruckVo>  truckVos;
    private CarListAdpater  carListAdpater;
    public CarListFragment(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    //创建数据库控制器
    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("jack","CarListFragment第2个");
        View view  = inflater.inflate(R.layout.allcar_list , container , false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        truck_dao = new TruckDao(getHelper());
        return  view;
    }


    @Override
    public void onStart() {
        super.onStart();
        truckVos = truck_dao.queryAll();
        carListAdpater = new CarListAdpater(truckVos,getActivity());
        carListAdpater.setOnRecyclerViewListener(CarListFragment.this);
        recyclerView.setAdapter(carListAdpater);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onItemClick(String num) {
        Toast.makeText(getActivity(),num,Toast.LENGTH_SHORT).show();
    }
}
