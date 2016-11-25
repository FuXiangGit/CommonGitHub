package com.xvli.dao;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.xvli.bean.CarDownDieboldVo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 11:04.
 */
public class CarDownDieboldDao {

    private Context mContext;
    private RuntimeExceptionDao run_dao = null;

    public CarDownDieboldDao(DatabaseHelper mHelper) {
        this.run_dao = mHelper.getRuntimeExceptionDao(CarDownDieboldVo.class);
    }

    /**
     * 创建一条数据
     * @param bean
     * @return
     */
    public int create(CarDownDieboldVo bean) {
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
    public void delete(CarDownDieboldVo bean) {
        run_dao.delete(bean);
    }

    /**
     * 按照指定的参数clientid删除对应记录
     *
     * @param clientid
     */
    public void deleteByID(String clientid) {
        DeleteBuilder<CarDownDieboldVo, String> builder = run_dao.deleteBuilder();
        try {
            builder.where().eq("clientid", clientid);
            run_dao.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除状态为UP 记录
     */

    public  void deleteByUp(String  status){
        DeleteBuilder<CarDownDieboldVo, String> builder = run_dao.deleteBuilder();
        try {
            builder.where().eq("operatetype", status);
            run_dao.delete(builder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除时间段内对应的数据
     */
    public  void deleteByUpOrTime(String low , String high ,String  status){
        DeleteBuilder<CarDownDieboldVo, String> builder = run_dao.deleteBuilder();
        try {
            builder.where().between("operatetime",low ,high).and().eq("operatetype", status);
//            builder.delete();
            run_dao.delete(builder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    /**
     * 更新数据
     *
     * @param bean
     */
    public void upDate(CarDownDieboldVo bean) {
        run_dao.update(bean);
    }

    /**
     * 查找表中所有数据
     *
     * @return
     */
    public List<CarDownDieboldVo> queryAll() {
        List<CarDownDieboldVo> lists = null;
        lists = run_dao.queryForAll();
        return lists;
    }

    /**
     * 按照要求查询所有匹配数据
     *
     * @param mDbWhere
     * @return
     */
    public List<CarDownDieboldVo> quaryForDetail(Map<String, Object> mDbWhere) {
        List<CarDownDieboldVo> list = run_dao.queryForFieldValues(mDbWhere);
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
    public List<CarDownDieboldVo> quaryWithOrderByLists(Map<String, Object> mDbWhere) {
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
            List<CarDownDieboldVo> list = where.query();
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
    public List<CarDownDieboldVo> search(String tableElement,String dirElement) {
        try {
            // 查询的query 返回值是一个列表
            // 类似 select * from User where 'username' = username;
            List<CarDownDieboldVo> tasks = run_dao.queryBuilder().where().eq(tableElement, dirElement).query();
            if (tasks.size() > 0) {
                return tasks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //查询某一时间段的数据
    public  List<CarDownDieboldVo> getDate(String low , String high ){
        try {
            List<CarDownDieboldVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    //查询
    public  List<CarDownDieboldVo> getDateforvalue(String low , String high , String columnName ,String value,String name ,String enabled){
        try {
            List<CarDownDieboldVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime",low ,high).and().eq(columnName, value).and().eq(name, enabled).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    public  List<CarDownDieboldVo> getDateEable(String low , String high , String columnName ,String value ){
        try {
            List<CarDownDieboldVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName, value).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }


    public  List<CarDownDieboldVo> getDateforvalueDown(String low , String high , String columnName ,String value,String name ,String enabled , String column ,String down){
        try {
//            QueryBuilder<CarUpDownVo, String>  queryBuilder = run_dao.queryBuilder();
//            queryBuilder.where().between("operatetime", low, high).and().eq(columnName, value).and().eq(name, enabled).and().eq(column, down);
////            queryBuilder.query();
            List<CarDownDieboldVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and()
                    .eq(columnName, value).and().eq(name, enabled).and().eq(column,down).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    public  List<CarDownDieboldVo> getDateforvalueDowns(String low , String high , String columnName ,String value,String name
            ,String enabled , String column ,String down,String barcode ,String values){
        try {
//            QueryBuilder<CarUpDownVo, String>  queryBuilder = run_dao.queryBuilder();
//            queryBuilder.where().between("operatetime", low, high).and().eq(columnName, value).and().eq(name, enabled).and().eq(column, down);
////            queryBuilder.query();
            List<CarDownDieboldVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and()
                    .eq(columnName, value).and().eq(name, enabled).and().eq(column, down).and().eq(barcode,values).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 按要求更新数据失效 (时间段内)
     *
     * @param
     */
    public void upDateRes(String low , String high , String columnName ,String value) {

        UpdateBuilder<CarDownDieboldVo, String> updateBuilder = run_dao.updateBuilder();
        try {
            updateBuilder.where().between("operatetime", low, high).and().eq(columnName, value);
            updateBuilder.updateColumnValue("enabled", "N");
            updateBuilder.update();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 按要求更新数据失效
     *
     * @param
     */
    public void upDateResInit( String columnName ,String value) {

        UpdateBuilder<CarDownDieboldVo, String> updateBuilder = run_dao.updateBuilder();
        try {
            updateBuilder.where().eq(columnName, value);
            updateBuilder.updateColumnValue("enabled", "N");
            updateBuilder.update();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 去除所有以上车 重复数据
     *
     * @param
     */

    public List<CarDownDieboldVo> getAllUpAndDISTINCT(String low ,String high){
        QueryBuilder<CarDownDieboldVo ,String> queryBuilder = run_dao.queryBuilder();
        try {
//            select  * from zt  as a where ID=(select  top 1 ID from zt where Name=a.Name)
            queryBuilder.where().between("operatetime", low, high).and().eq("operatetype", "ON");
            queryBuilder.distinct().selectColumns("barCode").selectColumns("itemtype");
            return  run_dao.query(queryBuilder.prepare());
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    //排序
    public List<CarDownDieboldVo> quaryWithOrderByLists(HashMap<String, Object> mDbWhere) {
        try {
            Where where = run_dao.queryBuilder().orderByRaw("operatetime").where();
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
            }
            List<CarDownDieboldVo> list = where.query();
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //时间段排序
    public List<CarDownDieboldVo> quaryWithOrderAllByLists(Map<String, Object> mDbWhere,String low , String high) {
        try {
            Where where = run_dao.queryBuilder().orderByRaw("operatetime").where();
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
            }
            List<CarDownDieboldVo> list = where.and().between("operatetime", low, high).query();
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



}
