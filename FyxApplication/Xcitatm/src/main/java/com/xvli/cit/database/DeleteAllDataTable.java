package com.xvli.cit.database;


import com.xvli.cit.dao.LoginDao;
import com.xvli.cit.dao.OperateLogVo_Dao;
import com.xvli.cit.dao.SpecialOutDao;
import com.xvli.cit.dao.TaskVoDao;
import com.xvli.cit.dao.TruckVo_Dao;

/**
 * 删除整个应用的数据库数据
 */
public class DeleteAllDataTable {

	public static void ClearAllTable(DatabaseHelper db) {
		//基础表 不删除 其他的表都删除

		LoginDao loginDao = new LoginDao(db);
		loginDao.deleteAll();

		OperateLogVo_Dao operateLogVo_dao = new OperateLogVo_Dao(db);
		operateLogVo_dao.deleteAll();

		SpecialOutDao outDao = new SpecialOutDao(db);
		outDao.deleteAll();

		TruckVo_Dao truckVoDao = new TruckVo_Dao(db);
		truckVoDao.deleteAll();

		TaskVoDao taskVoDao = new TaskVoDao(db);
		taskVoDao.deleteAll();
	}
}
