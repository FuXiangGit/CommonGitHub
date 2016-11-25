package com.xvli.dao;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.xvli.bean.MyAtmError;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/8.
 */
public class MyErrorDao {
    private Context mContext;
    private RuntimeExceptionDao run_dao = null;

    public MyErrorDao(DatabaseHelper mHelper) {
        this.run_dao = mHelper.getRuntimeExceptionDao(MyAtmError.class);
    }

    /**
     * 创建一条数据
     *
     * @param bean
     * @return
     */
    public int create(MyAtmError bean) {
        return run_dao.create(bean);
    }

    /**
     * 删除表中所有数据
     */
    public void deleteAll() {
        run_dao.delete(queryAll());
    }

    /**
     * 删除传入的列表
     *
     * @param bean
     */
    public void delete(MyAtmError bean) {
        run_dao.delete(bean);
    }

    /**
     * 按照指定的参数clientid删除对应记录
     *
     * @param clientid
     */
    public void deleteByID(String clientid) {
        DeleteBuilder<MyAtmError, String> builder = run_dao.deleteBuilder();
        try {
            builder.where().eq("clientid", clientid);
            run_dao.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新数据
     *
     * @param bean
     */
    public void upDate(MyAtmError bean) {
        run_dao.update(bean);
    }

    /**
     * 查找表中所有数据
     *
     * @return
     */
    public List<MyAtmError> queryAll() {
        List<MyAtmError> lists = null;
        lists = run_dao.queryForAll();
        return lists;
    }

    /**
     * 查找表中是否已经有了对应的数据，如果有了返回几条
     *
     * @return
     */
    public int contentsNumber(MyAtmError beanVo) {
        Map<String, Object> where = new HashMap<String, Object>();
        where.put("clientid", beanVo.getClientid());
        where.put("branchid", beanVo.getBranchid());
        List<MyAtmError> lists = run_dao.queryForFieldValues(where);
        if (lists != null && lists.size() > 0) {
            return lists.size();
        } else {
            return 0;
        }
    }

    /**
     * 按照要求查询所有匹配数据
     *
     * @param mDbWhere
     * @return
     */
    public List<MyAtmError> quaryForDetail(Map<String, Object> mDbWhere) {
        List<MyAtmError> list = run_dao.queryForFieldValues(mDbWhere);
        if (list != null && list.size() > 0) {
            return list;
        }
        return null;
    }

    /**
     * 按照要求查询所有匹配数据并且排序
     *
     * @param mDbWhere
     * @return
     */
    public List<MyAtmError> quaryWithOrderByLists(Map<String, Object> mDbWhere) {
        try {
            Where where = run_dao.queryBuilder().orderBy("orders", true).where();
            Iterator iter = mDbWhere.entrySet().iterator();
            int i = 0;
            while (iter.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter.next();
                String key = entry.getKey();
                Object obj = entry.getValue();
                if (i == 0) {
                    where.eq(entry.getKey(), entry.getValue());
                } else {
                    where.and().eq(entry.getKey(), entry.getValue());
                }
                i++;

                QueryBuilder builder = run_dao.queryBuilder().orderBy("", true);
            }
            List<MyAtmError> list = where.query();
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param tableElement 表中的参数
     * @param dirElement   查询的传入参数
     * @return
     */
    public List<MyAtmError> search(String tableElement, String dirElement) {
        try {
            // 查询的query 返回值是一个列表
            // 类似 select * from User where 'username' = username;
            List<MyAtmError> tasks = run_dao.queryBuilder().where().eq(tableElement, dirElement).query();
            if (tasks.size() > 0) {
                return tasks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  时间段内有效数据
     * @return
     */
    public List<MyAtmError> isEnable(String low, String high ,String column ,String value,String column1 ,String value1) {
        try {

            QueryBuilder<MyAtmError, String> queryBuilder = run_dao.queryBuilder();
            queryBuilder.where().between("operatetime",low,high).and().eq(column,value).and().eq(column1,value1);
            List<MyAtmError> tasks  = queryBuilder.query() ;
            if (tasks.size() > 0) {
                return tasks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<MyAtmError> isEnable(String low, String high ,String column ,String value,String column1 ,String value1,String type,String values) {
        try {

            QueryBuilder<MyAtmError, String> queryBuilder = run_dao.queryBuilder();
            queryBuilder.where().between("operatetime",low,high).and().eq(column, value).and().eq(column1,value1).and().eq(type,values);
            List<MyAtmError> tasks  = queryBuilder.query() ;
            if (tasks.size() > 0) {
                return tasks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public List<MyAtmError> isEnable(String low, String high ,String column ,String value,String column1 ,
                                     String value1,String type,String values,String isback,String val) {
        try {

            QueryBuilder<MyAtmError, String> queryBuilder = run_dao.queryBuilder();
            queryBuilder.where().between("operatetime",low,high).and().eq(column, value).and().eq(column1,value1)
                    .and().eq(type,values).and().eq(isback,val);
            List<MyAtmError> tasks  = queryBuilder.query() ;
            if (tasks.size() > 0) {
                return tasks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
