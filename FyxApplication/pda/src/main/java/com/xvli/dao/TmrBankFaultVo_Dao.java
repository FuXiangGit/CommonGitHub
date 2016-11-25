package com.xvli.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao;
import com.xvli.bean.TmrBankFaultVo;

public class TmrBankFaultVo_Dao {

	private DatabaseHelper mHelper;
	private Dao dao;

	public TmrBankFaultVo_Dao(DatabaseHelper helper) {
		this.mHelper = helper;
		try {
			this.dao = mHelper.getDao(TmrBankFaultVo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public int create(TmrBankFaultVo bean) {
		try {
			return dao.create(bean);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public List<TmrBankFaultVo> quaryForDetail(Map<String, Object> mDbWhere) {
		List<TmrBankFaultVo> list;
		try {
			list = dao.queryForFieldValues(mDbWhere);
			if (list != null && list.size() > 0) {
				return list;
			} else
                return list=new ArrayList<TmrBankFaultVo>();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 删除全部
	 */
	@SuppressWarnings("unchecked")
	public void deleteAll() {
		try {
			dao.delete(queryAll());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 查询所有的
	 */
	public List<TmrBankFaultVo> queryAll() {
		List<TmrBankFaultVo> users = null;
		try {
			users = dao.queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * 更新
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public void upDate(TmrBankFaultVo bean) {
		try {
			dao.update(bean);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public void createOrUpdate(TmrBankFaultVo bean) throws SQLException {
		
		 dao.createOrUpdate(bean);
	}
	/**
	 * 查找表中是否已经有了对应的数据，如果有了返回几条
	 *
	 * @return
	 */
	public int contentsNumber(TmrBankFaultVo beanVo) {
		Map<String, Object> where = new HashMap<String, Object>();
		where.put("clientid", beanVo.getClientid());
//		where.put("taskid", beanVo.getTaskid());
		where.put("atmid", beanVo.getAtmid());
		List<TmrBankFaultVo> lists = null;
		try {
			lists = dao.queryForFieldValues(where);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (lists != null && lists.size() > 0) {
			return lists.size();
		} else {
			return 0;
		}
	}
}
