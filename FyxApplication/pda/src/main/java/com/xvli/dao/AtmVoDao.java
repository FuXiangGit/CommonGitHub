package com.xvli.dao;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchVo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 机具数据库Dao
 */
public class AtmVoDao {
    private Context mContext;
    private RuntimeExceptionDao run_dao = null;

    public AtmVoDao(DatabaseHelper mHelper) {
        this.run_dao = mHelper.getRuntimeExceptionDao(AtmVo.class);
    }

    /**
     * 创建一条数据
     *
     * @param bean
     * @return
     */
    public int create(AtmVo bean) {
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
    public void delete(AtmVo bean) {
        run_dao.delete(bean);
    }

    /**
     * 按照指定的参数clientid删除对应记录
     *
     * @param clientid
     */
    public void deleteByID(String clientid) {
        DeleteBuilder<AtmVo, String> builder = run_dao.deleteBuilder();
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
    public void upDate(AtmVo bean) {
        run_dao.update(bean);
    }

    /**
     * 查找表中所有数据
     *
     * @return
     */
    public List<AtmVo> queryAll() {
        List<AtmVo> lists = null;
        lists = run_dao.queryForAll();
        return lists;
    }

    /**
     * 按照要求查询所有匹配数据
     *
     * @param mDbWhere
     * @return
     */
    public List<AtmVo> quaryForDetail(Map<String, Object> mDbWhere) {
        List<AtmVo> list = run_dao.queryForFieldValues(mDbWhere);
        if (list != null && list.size() > 0) {
            return list;
        }
        return null;
    }

    /**
     * 查找表中是否已经有了对应的数据，如果有了返回几条
     *
     * @return
     */
    public int contentsNumber(AtmVo beanVo) {
        Map<String, Object> where = new HashMap<String, Object>();
        where.put("taskid", beanVo.getTaskid());
        List<AtmVo> lists = run_dao.queryForFieldValues(where);
        if (lists != null && lists.size() > 0) {
            return lists.size();
        } else {
            return 0;
        }
    }

    /**
     * 按照要求查询所有匹配数据并且排序
     * @param mDbWhere
     * @return
     */
    public List<AtmVo> quaryWithOrderByLists(Map<String, Object> mDbWhere) {
        try {
            Where where = run_dao.queryBuilder().orderBy("order", true).where();
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
            List<AtmVo> list = where.query();
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
    public List<AtmVo> search(String tableElement,String dirElement) {
        try {
            // 查询的query 返回值是一个列表
            // 类似 select * from User where 'username' = username;
            List<AtmVo> tasks = run_dao.queryBuilder().where().eq(tableElement, dirElement).query();
            if (tasks.size() > 0) {
                return tasks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //按条件查询
    public List<AtmVo> getAllMatch (String barcode,String branchid){
        QueryBuilder<AtmVo ,String> queryBuilder = run_dao.queryBuilder();
        try {
            queryBuilder.where().eq("tasktype", "0").or().eq("tasktype", 2).and().eq("barcode",barcode).and().eq("branchid",branchid);
            return  queryBuilder.query();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    //模糊查询
    public List<AtmVo>  getAll(String comlum ,String value){
        QueryBuilder<AtmVo ,String> queryBuilder = run_dao.queryBuilder();
        try{
            queryBuilder.where().like(comlum, "%" + value + "%");
            return run_dao.query(queryBuilder.prepare());
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    //模糊查询
    public List<AtmVo>  getAllAtm(String comlum ,String value,String branchid ,String branchvaule){
        QueryBuilder<AtmVo ,String> queryBuilder = run_dao.queryBuilder();
        try{
            queryBuilder.where().eq(branchid,branchvaule).and().like(comlum, "%" + value + "%");
            return run_dao.query(queryBuilder.prepare());
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
