package com.xvli.dao;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.xvli.bean.AtmUpDownItemVo;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/8.
 */
public class AtmUpDownItemVoDao {
    private Context mContext;
    private RuntimeExceptionDao run_dao = null;

    public AtmUpDownItemVoDao(DatabaseHelper mHelper) {
        this.run_dao = mHelper.getRuntimeExceptionDao(AtmUpDownItemVo.class);
    }

    /**
     * 创建一条数据
     *
     * @param bean
     * @return
     */
    public int create(AtmUpDownItemVo bean) {
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
    public void delete(AtmUpDownItemVo bean) {
        run_dao.delete(bean);
    }

    /**
     * 按照指定的参数clientid删除对应记录
     *
     * @param clientid
     */
    public void deleteByID(String clientid) {
        DeleteBuilder<AtmUpDownItemVo, String> builder = run_dao.deleteBuilder();
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
    public void upDate(AtmUpDownItemVo bean) {

        run_dao.update(bean);
    }

    /**
     * 查找表中所有数据
     *
     * @return
     */
    public List<AtmUpDownItemVo> queryAll() {
        List<AtmUpDownItemVo> lists = null;
        lists = run_dao.queryForAll();
        return lists;
    }

    /**
     * 按照要求查询所有匹配数据
     *
     * @param mDbWhere
     * @return
     */
    public List<AtmUpDownItemVo> quaryForDetail(Map<String, Object> mDbWhere) {
        List<AtmUpDownItemVo> list = run_dao.queryForFieldValues(mDbWhere);
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
    public List<AtmUpDownItemVo> quaryWithOrderByLists(Map<String, Object> mDbWhere) {
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
            List<AtmUpDownItemVo> list = where.query();
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
    public List<AtmUpDownItemVo> search(String tableElement,String dirElement) {
        try {
            // 查询的query 返回值是一个列表
            // 类似 select * from User where 'username' = username;
            List<AtmUpDownItemVo> tasks = run_dao.queryBuilder().where().eq(tableElement, dirElement).query();
            if (tasks.size() > 0) {
                return tasks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单个网点上下机具
     */

    public  List<AtmUpDownItemVo> getDateforvalue(String low , String high , String columnName ,String value ){
        try {
            List<AtmUpDownItemVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName, value).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    public  List<AtmUpDownItemVo> getDateforvalue(String low , String high , String columnName ,String value,String column ,int v ){
        try {
            List<AtmUpDownItemVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName, value).and().eq(column, v).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }


    public  List<AtmUpDownItemVo> getDateforvalueS(String low , String high , String columnName ,String value,String column ,String v ){
        try {
            List<AtmUpDownItemVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName, value).and().eq(column, v).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }



    public  List<AtmUpDownItemVo> getDateforvalue(String low , String high , String columnName ,String value,String column ,int v ,String col ,String enable){
        try {
            List<AtmUpDownItemVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName, value).and().eq(column, v).and().eq(col, enable).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }
    public  List<AtmUpDownItemVo> getDateforvalue(String low , String high , String columnName ,String value,String column ,String v ){
        try {
            List<AtmUpDownItemVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName, value).and().eq(column, v).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }


    public  List<AtmUpDownItemVo> getDateforvalues(String low , String high , String columnName ,String value,String column
            ,String v ,String col ,String enable){
        try {
            List<AtmUpDownItemVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and()
                    .eq(columnName, value).and().eq(column, v).and().eq(col, enable).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }


    public  List<AtmUpDownItemVo> getDateforvalues(String low , String high , String columnName ,String value,String column
            ,String v ,String col ,String enable,String barcode,String codeValue){
        try {
            List<AtmUpDownItemVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and()
                    .eq(columnName, value).and().eq(column, v).and().eq(col, enable).and().eq(barcode,codeValue).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 按要求更新数据失效
     *
     * @param
     */
    public void upDateResInit( String columnName ,String value ,String column ,String v) {

        UpdateBuilder<AtmUpDownItemVo, String> updateBuilder = run_dao.updateBuilder();
        try {
            updateBuilder.where().eq(columnName, value).and().eq(column, v);
            updateBuilder.updateColumnValue("isYouXiao", "N");
            updateBuilder.updateColumnValue("isUploaded", "N");
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
    public void upDateResInit( String low ,String high , String columnName ,String value ,String column ,String v) {

        UpdateBuilder<AtmUpDownItemVo, String> updateBuilder = run_dao.updateBuilder();
        try {
            updateBuilder.where().between("operatetime", low, high).and().eq(columnName, value).and().eq(column , v);
            updateBuilder.updateColumnValue("isYouXiao", "N");
            updateBuilder.update();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }



}
