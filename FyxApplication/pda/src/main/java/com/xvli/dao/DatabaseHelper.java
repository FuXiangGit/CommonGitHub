package com.xvli.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.catchmodel.been.GasStation_Vo;
import com.catchmodel.been.NetWorkInfo_catVo;
import com.catchmodel.been.SaveAllDataVo;
import com.catchmodel.been.ServingStation_Vo;
import com.catchmodel.been.WorkNode_Vo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xvli.bean.ATMRouteVo;
import com.xvli.bean.ATMTroubleVo;
import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.BankCardVo;
import com.xvli.bean.BankCustomerVo;
import com.xvli.bean.BranchLineVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.CarDownDieboldVo;
import com.xvli.bean.CarUpDownVo;
import com.xvli.bean.ChangeUserTruckVo;
import com.xvli.bean.ConfigVo;
import com.xvli.bean.DispatchMsgVo;
import com.xvli.bean.DispatchVo;
import com.xvli.bean.DynATMItemVo;
import com.xvli.bean.DynCycleItemValueVo;
import com.xvli.bean.DynCycleItemVo;
import com.xvli.bean.DynNodeItemVo;
import com.xvli.bean.DynRepairVo;
import com.xvli.bean.DynRouteItemVo;
import com.xvli.bean.DynTroubleItemVo;
import com.xvli.bean.FeedBackVo;
import com.xvli.bean.INPutVo;
import com.xvli.bean.InterfaceEventsVo;
import com.xvli.bean.IsRepairVo;
import com.xvli.bean.KeyPasswordVo;
import com.xvli.bean.Log_SortingVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.MyAtmError;
import com.xvli.bean.NetAtmDoneVo;
import com.xvli.bean.NetWorkInfoVo;
import com.xvli.bean.NetWorkRouteVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.OtherTaskVo;
import com.xvli.bean.RepairUpVo;
import com.xvli.bean.ScatteredVo;
import com.xvli.bean.SiginPhotoVo;
import com.xvli.bean.TaiLineVo;
import com.xvli.bean.TaiRepairSealVo;
import com.xvli.bean.TempVo;
import com.xvli.bean.ThingsVo;
import com.xvli.bean.TmrBankFaultVo;
import com.xvli.bean.TmrPhotoVo;
import com.xvli.bean.TruckVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.bean.WarnVo;
import com.xvli.pda.R;
import com.xvli.utils.PDALogger;

import java.sql.SQLException;

/**
 * Created by Administrator on 2015/12/7.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    /**
     * 数据库名
     */
    private static final String TABLE_NAME = "sqlite-test.db";
    /**
     * 数据库版本
     */
    private static final int DATABASE_VERSION = 14;
    private static DatabaseHelper instance;

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public DatabaseHelper(Context context) {
//        super(context, TABLE_NAME, null, DATABASE_VERSION);
        super(context, TABLE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * 创建数据库
     * 只有没有对应表的时候才会创建
     *
     * @param database
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            PDALogger.d("是否进来创建了");
            TableUtils.createTable(connectionSource, LoginVo.class);
            TableUtils.createTable(connectionSource, BranchVo.class);
            TableUtils.createTable(connectionSource, NetWorkInfoVo.class);//网点信息表
            TableUtils.createTable(connectionSource, OtherTaskVo.class);
            TableUtils.createTable(connectionSource, ScatteredVo.class);
            TableUtils.createTable(connectionSource,TempVo.class);
            TableUtils.createTable(connectionSource,AtmBoxBagVo.class);
            TableUtils.createTable(connectionSource,AtmUpDownItemVo.class);
            TableUtils.createTable(connectionSource,CarUpDownVo.class);
            TableUtils.createTable(connectionSource,AtmVo.class);
            TableUtils.createTable(connectionSource,KeyPasswordVo.class);
            TableUtils.createTable(connectionSource,DynRouteItemVo.class);
            TableUtils.createTable(connectionSource,DynTroubleItemVo.class);
            TableUtils.createTable(connectionSource,DynCycleItemVo.class);
            TableUtils.createTable(connectionSource,TruckVo.class);

            TableUtils.createTable(connectionSource, ConfigVo.class);
            TableUtils.createTable(connectionSource, OperateLogVo.class);
            TableUtils.createTable(connectionSource, NetWorkRouteVo.class);
            TableUtils.createTable(connectionSource,TmrPhotoVo.class);
            TableUtils.createTable(connectionSource,NetAtmDoneVo.class);

            TableUtils.createTable(connectionSource,ATMRouteVo.class);
            TableUtils.createTable(connectionSource,UniqueAtmVo.class);
            TableUtils.createTable(connectionSource,DynRepairVo.class);
            TableUtils.createTable(connectionSource,MyAtmError.class);
            TableUtils.createTable(connectionSource,BankCardVo.class);
            TableUtils.createTable(connectionSource,IsRepairVo.class);
            TableUtils.createTable(connectionSource,TmrBankFaultVo.class);
            TableUtils.createTable(connectionSource,ATMTroubleVo.class);
            TableUtils.createTable(connectionSource,RepairUpVo.class);
            TableUtils.createTable(connectionSource,SiginPhotoVo.class);
            TableUtils.createTable(connectionSource,DynATMItemVo.class);
            TableUtils.createTable(connectionSource,DynNodeItemVo.class);
            TableUtils.createTable(connectionSource,DynCycleItemValueVo.class);
            TableUtils.createTable(connectionSource,WarnVo.class);
            TableUtils.createTable(connectionSource,InterfaceEventsVo.class);
            TableUtils.createTable(connectionSource,Log_SortingVo.class);
            TableUtils.createTable(connectionSource,DispatchVo.class);
            TableUtils.createTable(connectionSource,ChangeUserTruckVo.class);
            TableUtils.createTable(connectionSource,FeedBackVo.class);
            TableUtils.createTable(connectionSource,DispatchMsgVo.class);
            TableUtils.createTable(connectionSource, NetWorkInfo_catVo.class);
            TableUtils.createTable(connectionSource, SaveAllDataVo.class);
            TableUtils.createTable(connectionSource, INPutVo.class);
            TableUtils.createTable(connectionSource, GasStation_Vo.class);
            TableUtils.createTable(connectionSource, ServingStation_Vo.class);
            TableUtils.createTable(connectionSource, WorkNode_Vo.class);
            TableUtils.createTable(connectionSource, AtmmoneyBagVo.class);
            TableUtils.createTable(connectionSource, CarDownDieboldVo.class);
            TableUtils.createTable(connectionSource, BranchLineVo.class);
            TableUtils.createTable(connectionSource, BankCustomerVo.class);
            TableUtils.createTable(connectionSource, AtmLineVo.class);
            TableUtils.createTable(connectionSource, ThingsVo.class);
            TableUtils.createTable(connectionSource, TaiLineVo.class);
            TableUtils.createTable(connectionSource, TaiRepairSealVo.class);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            PDALogger.d("是否进来更新了");
            TableUtils.dropTable(connectionSource, LoginVo.class, true);
            TableUtils.dropTable(connectionSource, BranchVo.class, true);
            TableUtils.dropTable(connectionSource, NetWorkInfoVo.class, true);
            TableUtils.dropTable(connectionSource, OtherTaskVo.class, true);
            TableUtils.dropTable(connectionSource, ScatteredVo.class, true);
            TableUtils.dropTable(connectionSource,TempVo.class,true);
            TableUtils.dropTable(connectionSource,AtmBoxBagVo.class,true);
            TableUtils.dropTable(connectionSource,AtmUpDownItemVo.class,true);
            TableUtils.dropTable(connectionSource,CarUpDownVo.class,true);
            TableUtils.dropTable(connectionSource,AtmVo.class,true);
            TableUtils.dropTable(connectionSource,KeyPasswordVo.class,true);
            TableUtils.dropTable(connectionSource,DynRouteItemVo.class,true);
            TableUtils.dropTable(connectionSource,DynTroubleItemVo.class,true);
            TableUtils.dropTable(connectionSource,DynCycleItemVo.class,true);
            TableUtils.dropTable(connectionSource,TruckVo.class,true);
            TableUtils.dropTable(connectionSource,ConfigVo.class,true);
            TableUtils.dropTable(connectionSource,OperateLogVo.class,true);
            TableUtils.dropTable(connectionSource,NetWorkRouteVo.class,true);
            TableUtils.dropTable(connectionSource,TmrPhotoVo.class,true);
            TableUtils.dropTable(connectionSource,NetAtmDoneVo.class,true);
            TableUtils.dropTable(connectionSource,UniqueAtmVo.class,true);
            TableUtils.dropTable(connectionSource,ATMRouteVo.class,true);
            TableUtils.dropTable(connectionSource,MyAtmError.class,true);
            TableUtils.dropTable(connectionSource,BankCardVo.class,true);
            TableUtils.dropTable(connectionSource,IsRepairVo.class,true);
            TableUtils.dropTable(connectionSource,TmrBankFaultVo.class,true);

            TableUtils.dropTable(connectionSource,ATMTroubleVo.class,true);
            TableUtils.dropTable(connectionSource,DynRepairVo.class,true);
            TableUtils.dropTable(connectionSource,RepairUpVo.class,true);
            TableUtils.dropTable(connectionSource,SiginPhotoVo.class,true);
            TableUtils.dropTable(connectionSource,DynATMItemVo.class,true);
            TableUtils.dropTable(connectionSource,DynNodeItemVo.class,true);
            TableUtils.dropTable(connectionSource,DynCycleItemValueVo.class ,true);
            TableUtils.dropTable(connectionSource,WarnVo.class ,true);
            TableUtils.dropTable(connectionSource, InterfaceEventsVo.class, true);
            TableUtils.dropTable(connectionSource, Log_SortingVo.class, true);
            TableUtils.dropTable(connectionSource, DispatchVo.class, true);
            TableUtils.dropTable(connectionSource, ChangeUserTruckVo.class, true);
            TableUtils.dropTable(connectionSource, FeedBackVo.class, true);
            TableUtils.dropTable(connectionSource, DispatchMsgVo.class, true);
            TableUtils.dropTable(connectionSource, NetWorkInfo_catVo.class,true);
            TableUtils.dropTable(connectionSource, SaveAllDataVo.class, true);
            TableUtils.dropTable(connectionSource, INPutVo.class, true);
            TableUtils.dropTable(connectionSource, GasStation_Vo.class, true);
            TableUtils.dropTable(connectionSource, WorkNode_Vo.class, true);
            TableUtils.dropTable(connectionSource, ServingStation_Vo.class, true);
            TableUtils.dropTable(connectionSource, AtmmoneyBagVo.class, true);
            TableUtils.dropTable(connectionSource, CarDownDieboldVo.class, true);
            TableUtils.dropTable(connectionSource, BranchLineVo.class, true);
            TableUtils.dropTable(connectionSource, BankCustomerVo.class, true);
            TableUtils.dropTable(connectionSource, AtmLineVo.class, true);
            TableUtils.dropTable(connectionSource, ThingsVo.class, true);
            TableUtils.dropTable(connectionSource, TaiLineVo.class, true);
            TableUtils.dropTable(connectionSource, TaiRepairSealVo.class, true);

            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }


    /* @Override
    public SQLiteDatabase getReadableDatabase() {
//        return super.getReadableDatabase();
        return SQLiteDatabase.openDatabase(Config.DATABASE_PATH, null,
                SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
//        return super.getWritableDatabase();
        return SQLiteDatabase.openDatabase(Config.DATABASE_PATH, null,
                SQLiteDatabase.OPEN_READWRITE);
    }*/

    @Override
    public void close() {
        super.close();
    }
}
