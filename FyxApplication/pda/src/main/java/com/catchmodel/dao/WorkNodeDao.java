package com.catchmodel.dao;

import android.content.Context;

import com.catchmodel.been.WorkNode_Vo;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.xvli.dao.DatabaseHelper;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 14:11.
 */
public class WorkNodeDao {
    private Context mContext;
    private RuntimeExceptionDao run_dao = null;

    public WorkNodeDao(DatabaseHelper mHelper) {
        this.run_dao = mHelper.getRuntimeExceptionDao(WorkNode_Vo.class);
    }

    /**
     * 创建一条数据
     *
     * @param bean
     * @return
     */
    public int create(WorkNode_Vo bean) {
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
    public void delete(WorkNode_Vo bean) {
        run_dao.delete(bean);
    }

    /**
     * 按照指定的参数clientid删除对应记录
     *
     * @param clientid
     */
    public void deleteByID(String clientid) {
        DeleteBuilder<WorkNode_Vo, String> builder = run_dao.deleteBuilder();
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
    public void upDate(WorkNode_Vo bean) {
        run_dao.update(bean);
    }

    /**
     * 查找表中所有数据
     *
     * @return
     */
    public List<WorkNode_Vo> queryAll() {
        List<WorkNode_Vo> lists = null;
        lists = run_dao.queryForAll();
        return lists;
    }

    /**
     * 查找表中是否已经有了对应的数据，如果有了返回几条
     *
     * @return
     */


    /**
     * 按照要求查询所有匹配数据
     *
     * @param mDbWhere
     * @return
     */
    public List<WorkNode_Vo> quaryForDetail(Map<String, Object> mDbWhere) {
        List<WorkNode_Vo> list = run_dao.queryForFieldValues(mDbWhere);
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
    public List<WorkNode_Vo> quaryWithOrderByLists(Map<String, Object> mDbWhere) {
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
            List<WorkNode_Vo> list = where.query();
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
    public List<WorkNode_Vo> search(String tableElement, String dirElement) {
        try {
            // 查询的query 返回值是一个列表
            // 类似 select * from User where 'username' = username;
            List<WorkNode_Vo> tasks = run_dao.queryBuilder().where().eq(tableElement, dirElement).query();
            if (tasks.size() > 0) {
                return tasks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //模糊查询  去除网点名称相同的查询结果
    public List<WorkNode_Vo>  getAll(String comlum ,String value){
        QueryBuilder<WorkNode_Vo,String> queryBuilder = run_dao.queryBuilder();
        try{
            queryBuilder.where().like(comlum ,"%"+value+"%");
            queryBuilder.distinct().selectColumns("Name");
            return run_dao.query(queryBuilder.prepare());
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    //模糊查询  去除所属客户相同的查询结果
    public List<WorkNode_Vo>  getAllCustomer(String comlum ,String value){
        QueryBuilder<WorkNode_Vo,String> queryBuilder = run_dao.queryBuilder();
        try{
            queryBuilder.where().like(comlum, "%" + value + "%");
            queryBuilder.distinct().selectColumns("Company");
            return run_dao.query(queryBuilder.prepare());
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }





    //模糊查询  去除地址相同的查询结果
    public List<WorkNode_Vo>  getAllAddress(String comlum ,String value){
        QueryBuilder<WorkNode_Vo,String> queryBuilder = run_dao.queryBuilder();
        try{
            queryBuilder.where().like(comlum ,"%"+value+"%");
            queryBuilder.distinct().selectColumns("Address");
            return run_dao.query(queryBuilder.prepare());
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    /**
     *  重复数据
     *
     * @param
     */

    public List<WorkNode_Vo> getAllCustomerDISTINCT(){
        QueryBuilder<WorkNode_Vo ,String> queryBuilder = run_dao.queryBuilder();
        try {
//            select  * from zt  as a where ID=(select  top 1 ID from zt where Name=a.Name)
//            queryBuilder.where().eq("operatetype", "ON");
            queryBuilder.distinct().selectColumns("Company");
            return  run_dao.query(queryBuilder.prepare());
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    //模糊查询  网点名称带出所属客户并去除相同的查询结果
    public List<WorkNode_Vo> getAllCustomerDISTINCT(String Name){
        QueryBuilder<WorkNode_Vo ,String> queryBuilder = run_dao.queryBuilder();
        try {
//            select  * from zt  as a where ID=(select  top 1 ID from zt where Name=a.Name)
//            queryBuilder.where().eq("operatetype", "ON");
            queryBuilder.where().eq("Name", Name);
            queryBuilder.distinct().selectColumns("Company");
            return  run_dao.query(queryBuilder.prepare());
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<WorkNode_Vo> getNameDISTINCT(){
        QueryBuilder<WorkNode_Vo ,String> queryBuilder = run_dao.queryBuilder();
        try {
//            select  * from zt  as a where ID=(select  top 1 ID from zt where Name=a.Name)
//            queryBuilder.where().eq("operatetype", "ON");
            queryBuilder.distinct().selectColumns("Name");
            return  run_dao.query(queryBuilder.prepare());
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public List<WorkNode_Vo> getAddessDISTINCT(){
        QueryBuilder<WorkNode_Vo ,String> queryBuilder = run_dao.queryBuilder();
        try {
//            select  * from zt  as a where ID=(select  top 1 ID from zt where Name=a.Name)
//            queryBuilder.where().eq("operatetype", "ON");

            queryBuilder.distinct().selectColumns("Address");
            return  run_dao.query(queryBuilder.prepare());
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //模糊查询  网点名称带出网点地址并去除相同的查询结果
    public List<WorkNode_Vo> getAddessDISTINCT(String Name ,String Customer ){
        QueryBuilder<WorkNode_Vo ,String> queryBuilder = run_dao.queryBuilder();
        try {
//            select  * from zt  as a where ID=(select  top 1 ID from zt where Name=a.Name)
//            queryBuilder.where().eq("operatetype", "ON");
            queryBuilder.where().eq("Name",Name).and().eq("Company",Customer);
            queryBuilder.distinct().selectColumns("Address");
            return  run_dao.query(queryBuilder.prepare());
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

}
