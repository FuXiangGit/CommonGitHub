package com.xvli.dao;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.xvli.bean.CarUpDownVo;
import com.xvli.bean.INPutVo;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 10:02.
 */
//入库清单

public class INPutDao {

    private Context mContext;
    private RuntimeExceptionDao run_dao = null;

    public INPutDao(DatabaseHelper mHelper) {
        this.run_dao = mHelper.getRuntimeExceptionDao(INPutVo.class);
    }

    /**
     * 创建一条数据
     *
     * @param bean
     * @return
     */
    public int create(INPutVo bean) {
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
    public void delete(INPutVo bean) {
        run_dao.delete(bean);
    }

    /**
     * 按照指定的参数clientid删除对应记录
     *
     * @param clientid
     */
    public void deleteByID(String clientid) {
        DeleteBuilder<INPutVo, String> builder = run_dao.deleteBuilder();
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
    public void upDate(INPutVo bean) {
        run_dao.update(bean);
    }

    /**
     * 查找表中所有数据
     *
     * @return
     */
    public List<INPutVo> queryAll() {
        List<INPutVo> lists = null;
        lists = run_dao.queryForAll();
        return lists;
    }

    /**
     * 按照要求查询所有匹配数据
     *
     * @param mDbWhere
     * @return
     */
    public List<INPutVo> quaryForDetail(Map<String, Object> mDbWhere) {
        List<INPutVo> list = run_dao.queryForFieldValues(mDbWhere);
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
    public List<INPutVo> quaryWithOrderByLists(Map<String, Object> mDbWhere) {
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

//                QueryBuilder builder = run_dao.queryBuilder().orderBy("", true);
            }
            List<INPutVo> list = where.query();
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<INPutVo> quaryWithOrderAllByLists(Map<String, Object> mDbWhere,String low , String high) {
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

//                QueryBuilder builder = run_dao.queryBuilder().orderBy("", true);
            }
            List<INPutVo> list = where.and().between("operatetime", low, high).query();
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
    public List<INPutVo> search(String tableElement,String dirElement) {
        try {
            // 查询的query 返回值是一个列表
            // 类似 select * from User where 'username' = username;
            List<INPutVo> tasks = run_dao.queryBuilder().where().eq(tableElement, dirElement).query();
            if (tasks.size() > 0) {
                return tasks;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public  List<INPutVo> getDateforvalue(String low , String high){
        try {
            List<INPutVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }



    public  List<INPutVo> getDateforvalue(String low , String high , String columnName ,String value ){
        try {
            List<INPutVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName, value).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    public  List<INPutVo> getDateforvalue(String low , String high , String columnName ,String value,String column ,int v ){
        try {
            List<INPutVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName, value).and().eq(column, v).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    public  List<INPutVo> getDateforvalue(String low , String high , String columnName ,String value,String column ,int v ,String col ,String enable){
        try {
            List<INPutVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and().eq(columnName, value).and().eq(column, v).and().eq(col, enable).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }



    public  List<INPutVo> getDateforvalues(String low , String high , String columnName ,String value,String column
            ,String v){
        try {
            List<INPutVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and()
                    .eq(columnName, value).and().eq(column, v).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }



    public  List<INPutVo> getDateforvalues(String low , String high , String columnName ,String value,String column
            ,String v ,String col ,String enable){
        try {
            List<INPutVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and()
                    .eq(columnName, value).and().eq(column, v).and().eq(col, enable).query();
            return  carUpDownVoList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }


    public  List<INPutVo> getDateforvalues(String low , String high , String columnName ,String value,String column
            ,String v ,String col ,String enable,String barcode,String codeValue){
        try {
            List<INPutVo> carUpDownVoList = run_dao.queryBuilder().where().between("operatetime", low, high).and()
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

        UpdateBuilder<CarUpDownVo, String> updateBuilder = run_dao.updateBuilder();
        try {
            updateBuilder.where().eq(columnName, value).and().eq(column , v);
            updateBuilder.updateColumnValue("isYouXiao", "N");
            updateBuilder.update();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
