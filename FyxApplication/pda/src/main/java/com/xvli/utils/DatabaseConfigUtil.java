package com.xvli.utils;

import com.catchmodel.been.GasStation_Vo;
import com.catchmodel.been.NetWorkInfo_catVo;
import com.catchmodel.been.SaveAllDataVo;
import com.catchmodel.been.ServingStation_Vo;
import com.catchmodel.been.WorkNode_Vo;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.xvli.bean.ATMTroubleVo;
import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.BankCustomerVo;
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
import com.xvli.dao.BranchLineDao;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/12/9.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[]{
            LoginVo.class, BranchVo.class, NetWorkInfoVo.class, OtherTaskVo.class, ScatteredVo.class, TempVo.class, AtmBoxBagVo.class, AtmUpDownItemVo.class, CarUpDownVo.class,
            AtmVo.class, KeyPasswordVo.class, DynRouteItemVo.class, DynTroubleItemVo.class, DynCycleItemVo.class, TruckVo.class, ConfigVo.class, OperateLogVo.class, NetWorkRouteVo.class,
            TmrPhotoVo.class, NetAtmDoneVo.class, UniqueAtmVo.class, IsRepairVo.class,BranchVo.class, MyAtmError.class, TmrBankFaultVo.class, ATMTroubleVo.class, DynRepairVo.class,
            RepairUpVo.class, SiginPhotoVo.class,DynATMItemVo.class,DynNodeItemVo.class , DynCycleItemValueVo.class,InterfaceEventsVo.class,WarnVo.class, Log_SortingVo.class,
            DispatchVo.class, ChangeUserTruckVo.class, FeedBackVo.class,DispatchMsgVo.class , NetWorkInfo_catVo.class, SaveAllDataVo.class , INPutVo.class , GasStation_Vo.class,
            ServingStation_Vo.class, WorkNode_Vo.class, AtmmoneyBagVo.class, CarDownDieboldVo.class, BranchLineDao.class, BankCustomerVo.class,AtmLineVo.class, ThingsVo.class, TaiLineVo.class,
            TaiRepairSealVo.class



    };

    public static void main(String[] args) throws IOException, SQLException {
//        writeConfigFile(new File("PATH/TO/ANDROID/PROJECT/src/main/res/raw/ormlite_config.txt"), classes);
        writeConfigFile(new File("ormlite_config.txt"), classes);
    }
}
