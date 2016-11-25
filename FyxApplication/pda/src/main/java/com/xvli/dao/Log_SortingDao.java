package com.xvli.dao;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.xvli.bean.Log_SortingVo;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 10:40.
 */
public class Log_SortingDao {
    private Context mContext;
    private RuntimeExceptionDao run_dao = null;

    public Log_SortingDao(DatabaseHelper mHelper) {
        this.run_dao = mHelper.getRuntimeExceptionDao(Log_SortingVo.class);
    }

    /**
     * 创建一条数据
     *
     * @param bean
     * @return
     */
    public int create(Log_SortingVo bean) {
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
    public void delete(Log_SortingVo bean) {
        run_dao.delete(bean);
    }

    /**
     * 按照指定的参数clientid删除对应记录
     *
     * @param clientid
     */
    public void deleteByID(String clientid) {
        DeleteBuilder<Log_SortingVo, String> builder = run_dao.deleteBuilder();
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
    public void upDate(Log_SortingVo bean) {
        run_dao.update(bean);
    }

    /**
     * 查找表中所有数据
     *
     * @return
     */
    public List<Log_SortingVo> queryAll() {
        List<Log_SortingVo> lists = null;
        lists = run_dao.queryForAll();
        return lists;
    }

    /**
     * 按照要求查询所有匹配数据
     *
     * @param mDbWhere
     * @return
     */
    public List<Log_SortingVo> quaryForDetail(Map<String, Object> mDbWhere) {
        List<Log_SortingVo> list = run_dao.queryForFieldValues(mDbWhere);
        if (list != null && list.size() > 0) {
            return list;
        }
        return null;
    }

    /**
     * 按照要求查询所有匹配数据并且排序
     * @param mDbWhere
     * @return
     */
    public List<Log_SortingVo> quaryWithOrderByLists(Map<String, Object> mDbWhere) {
        try {
            Where where = run_dao.queryBuilder().orderBy("orders", true).where();
            Iterator iter = mDbWhere.entrySet().iterator();
            int i = 0;
            while (iter.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter.next();
                String key = entry.getKey();
                Object obj = entry.getValue();
                if(i==0){
                    where.eq(entry.getKey(), entry.getValue());
                }else{
                    where.and().eq(entry.getKey(), entry.getValue());
                }
                i++;

                QueryBuilder builder = run_dao.queryBuilder().orderBy("", true);
            }
            List<Log_SortingVo> list = where.query();
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param tableElement 表中的参数
     * @param dirElement 查询的传入参数
     * @return
     */
    public List<Log_SortingVo> search(String tableElement,String dirElement) {
        try {
            // 查询的query 返回值是一个列表
            // 类似 select * from User where 'username' = username;
            List<Log_SortingVo> tasks = run_dao.queryBuilder().where().eq(tableElement, dirElement).query();
            if (tasks.size() > 0) {
                return tasks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询某个时间段内的数据
     * low 每个类型的最后一条
     * high 当前时间
     * @return
     */
    public  List<Log_SortingVo> getDate(String low , String high,String columnName,String value){
        try {
            List<Log_SortingVo> operatetime =  run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName,value).query();
            return operatetime;
        }catch (SQLException e){
            e.printStackTrace();
        }


        return null;

    }

    public  List<Log_SortingVo> getDateBrankid(String low , String high,String columnName,String value,String brankid ,String values){
        try {
            List<Log_SortingVo> operatetime =  run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName, value).and().eq(brankid,values).query();
            return operatetime;
        }catch (SQLException e){
            e.printStackTrace();
        }


        return null;

    }

    public  List<Log_SortingVo> getDateBrankid(String low , String high,String columnName,String value,String brankid ,String values,String atmCode ,String code){
        try {
            List<Log_SortingVo> operatetime =
                    run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName,value).and().eq(brankid,values).and().eq(atmCode,code).query();
            return operatetime;
        }catch (SQLException e){
            e.printStackTrace();
        }


        return null;

    }
}
