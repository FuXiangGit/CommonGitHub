package com.xvli.dao;

import com.catchmodel.dao.GasStationDao;
import com.catchmodel.dao.NetWorkInfoVo_catDao;
import com.catchmodel.dao.ServingStationDao;
import com.catchmodel.dao.WorkNodeDao;
import com.xvli.bean.TmrBankFaultVo;

/**
 * 删除整个应用的数据库数据
 */
public class DeleteAllDataTable {

	public static void ClearAllTable(DatabaseHelper db) {
		//基础表 不删除 其他的表都删除
	    //基础表： 巡检 DynRouteItemVo  故障 DynTroubleItemVo  平台登记 DynCycleItemVo  配置文件ConfigVo  故障选择项DynRepairVo


		LoginDao loginDao = new LoginDao(db);
		loginDao.queryAll();//删除登陆用户账号

		BranchVoDao branchVoDao = new BranchVoDao(db);
		branchVoDao.deleteAll();

		NetWorkVoDao netWorkVoDao = new NetWorkVoDao(db);
		netWorkVoDao.deleteAll();

		OtherTaskVoDao otherTaskVoDao = new OtherTaskVoDao(db);
		otherTaskVoDao.deleteAll();

		ScatteredVoDao scatteredVoDao = new ScatteredVoDao(db);
		scatteredVoDao.deleteAll();

		TempVoDao tempVoDao = new TempVoDao(db);
		tempVoDao.deleteAll();

		AtmBoxBagDao atmBoxBagDao = new AtmBoxBagDao(db);
		atmBoxBagDao.deleteAll();

		AtmUpDownItemVoDao atmUpDownItemVoDao = new AtmUpDownItemVoDao(db);
		atmUpDownItemVoDao.deleteAll();

		CarUpDownVoDao carUpDownVoDao = new CarUpDownVoDao(db);
		carUpDownVoDao.deleteAll();

		AtmVoDao atmVoDao = new AtmVoDao(db);
		atmVoDao.deleteAll();

		KeyPasswordVo_Dao keyPasswordVo_dao = new KeyPasswordVo_Dao(db);
		keyPasswordVo_dao.deleteAll();

		TruckVo_Dao truckVoDao = new TruckVo_Dao(db);
		truckVoDao.deleteAll();

		OtherTaskVoDao other_dao = new OtherTaskVoDao(db);
		other_dao.deleteAll();

		OperateLogVo_Dao operateLogVo_dao = new OperateLogVo_Dao(db);
		operateLogVo_dao.deleteAll();

		NetWorkRoutDao netWorkRoutDao = new NetWorkRoutDao(db);
		netWorkRoutDao.deleteAll();

		TmrPhotoDao photoDao = new TmrPhotoDao(db);
		photoDao.deleteAll();

		NetAtmDoneDao atmDoneDao = new NetAtmDoneDao(db);
		atmDoneDao.deleteAll();

		UniqueAtmDao uniqueAtmDao = new UniqueAtmDao(db);
		uniqueAtmDao.deleteAll();

		IsRepairDao isRepairDao = new IsRepairDao(db);
		isRepairDao.deleteAll();

		MyErrorDao errorDao = new MyErrorDao(db);
		errorDao.deleteAll();


		RepairUpDao repairUpDao = new RepairUpDao(db);
		repairUpDao.deleteAll();


		SiginPhotoDao siginPhotoDao = new SiginPhotoDao(db);
		siginPhotoDao.deleteAll();

		ConfigVoDao configVoDao = new ConfigVoDao(db);
		configVoDao.deleteAll();

		WarnVoDao warnVoDao = new WarnVoDao(db);
		warnVoDao.deleteAll();

		DispatchVoDao dispatchVoDao = new DispatchVoDao(db);
		dispatchVoDao.deleteAll();

		ChangeUserTruckDao truckDao  = new ChangeUserTruckDao(db);
		truckDao.deleteAll();

		FeedBackVoDao feedBackVoDao = new FeedBackVoDao(db);
		feedBackVoDao.deleteAll();

		DispatchMsgVoDao msg = new DispatchMsgVoDao(db);
		msg.deleteAll();

		DynCycleItemValueVoDao dynCycleItemValueVoDao = new DynCycleItemValueVoDao(db);
		dynCycleItemValueVoDao.deleteAll();

		Log_SortingDao log_sortingDao = new Log_SortingDao(db);
		log_sortingDao.deleteAll();

		NetWorkInfoVo_catDao netWorkInfoVo_catDao = new NetWorkInfoVo_catDao(db);
		netWorkInfoVo_catDao.deleteAll();

//		SaveAllDataVoDao saveAllDataVoDao = new SaveAllDataVoDao(db);
//		saveAllDataVoDao.deleteAll();

		INPutDao  inPutDao = new INPutDao(db);
		inPutDao.deleteAll();

		GasStationDao  gasStationDao = new GasStationDao(db);
		gasStationDao.deleteAll();

		ServingStationDao stationDao = new ServingStationDao(db);
		stationDao.deleteAll();

		WorkNodeDao  workNodeDao = new WorkNodeDao(db);
		workNodeDao.deleteAll();

		CarDownDieboldDao  carDownDieboldDao = new CarDownDieboldDao(db);
		carDownDieboldDao.deleteAll();

		BranchLineDao dao = new BranchLineDao(db);
		dao.deleteAll();

		AtmLineDao lineDao = new AtmLineDao(db);
		lineDao.deleteAll();

		ThingsDao thingsDao = new ThingsDao(db);
		thingsDao.deleteAll();

		TmrBankFaultVo_Dao bankFaultVo_dao = new TmrBankFaultVo_Dao(db);
		bankFaultVo_dao.deleteAll();

		TaiAtmLineDao taiAtmLineDao = new TaiAtmLineDao(db);
		taiAtmLineDao.deleteAll();

		TaiRepairDao taiRepairDao = new TaiRepairDao(db);
		taiAtmLineDao.deleteAll();
	}

	//点击扫描出库物品时 删除所有任务相关的数据 重新下载
	public static void deleteTaskData(DatabaseHelper db){
		BranchVoDao branchVoDao = new BranchVoDao(db); //网点 相关
		branchVoDao.deleteAll();

		KeyPasswordVo_Dao keyPasswordVo_dao = new KeyPasswordVo_Dao(db); //钥匙密码
		keyPasswordVo_dao.deleteAll();

		AtmVoDao atmVoDao = new AtmVoDao(db);
		atmVoDao.deleteAll();

		AtmBoxBagDao vo = new AtmBoxBagDao(db);
		vo.deleteAll();

		OtherTaskVoDao other_dao = new OtherTaskVoDao(db);
		other_dao.deleteAll();

		UniqueAtmDao uniqueAtmDao = new UniqueAtmDao(db);
		uniqueAtmDao.deleteAll();

	}
	//出车前点击下载任务  重新下载任务相关数据  保留已经操作过的数据
	public static void deleteAgainLoader(DatabaseHelper db){
		BranchVoDao branchVoDao = new BranchVoDao(db); //网点 相关
		branchVoDao.deleteAll();

		BranchLineDao dao = new BranchLineDao(db);
		dao.deleteAll();

		AtmVoDao atmVoDao = new AtmVoDao(db);
		atmVoDao.deleteAll();

		AtmLineDao lineDao = new AtmLineDao(db);
		lineDao.deleteAll();

		OtherTaskVoDao other_dao = new OtherTaskVoDao(db);
		other_dao.deleteAll();

		UniqueAtmDao uniqueAtmDao = new UniqueAtmDao(db);
		uniqueAtmDao.deleteAll();

		AtmMoneyDao moneyDao = new AtmMoneyDao(db);
		moneyDao.deleteAll();

		TaiAtmLineDao taiAtmLineDao = new TaiAtmLineDao(db);
		taiAtmLineDao.deleteAll();

		TaiRepairDao taiRepairDao = new TaiRepairDao(db);
		taiAtmLineDao.deleteAll();
	}
}
