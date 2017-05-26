package com.xvli.cit.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.xvli.cit.database.DatabaseHelper;
import com.xvli.cit.vo.TruckVo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TruckVo_Dao {

    private DatabaseHelper mHelper;
    private Dao dao;

    @SuppressWarnings("unchecked")
    public TruckVo_Dao(DatabaseHelper helper) {
        this.mHelper = helper;
        try {
            this.dao = mHelper.getDao(TruckVo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int create(TruckVo bean) {
        try {
            return dao.create(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<TruckVo> quaryForDetail(Map<String, Object> mDbWhere) {
        List<TruckVo> list;
        try {
            list = dao.queryForFieldValues(mDbWhere);
            if (list != null && list.size() > 0) {
                return list;
            } else {
                return list = new ArrayList<TruckVo>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按照一条条件查询
     */
    public TruckVo search(String clientid) {
        try {
            // 查询的query 返回值是一个列表
            // 类似 select * from User where 'username' = username;
            List<TruckVo> tasks = dao.queryBuilder().where().eq("clientid", clientid).query();

            if (tasks.size() > 0)
                return tasks.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新
     */
    public void update(TruckVo bean)  {
        try {
            dao.createOrUpdate(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除全部
     */
    public void deleteAll() {
        try {
            dao.delete(queryAll());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int contentsNumber(TruckVo beanVo) {
        Map<String, Object> where = new HashMap<String, Object>();
        where.put("code", beanVo.getBarcode());
        where.put("platenumber", beanVo.getPlatenumber());
        List<TruckVo> lists = null;
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

    /**
     * 查询所有的
     */
    public List<TruckVo> queryAll() {
        List<TruckVo> users = null;
        try {
            users = dao.queryForAll();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return users;
    }

    /**
     * 删除传入的列表
     *
     * @param bean
     */
    public void delete(TruckVo bean) {
        try {
            dao.delete(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteByInput() {
        DeleteBuilder<TruckVo, String> builder = dao.deleteBuilder();
        try {
            builder.where().eq("operateType", "1");
            dao.delete(builder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
