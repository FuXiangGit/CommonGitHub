package com.xuli.dao;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.xuli.database.DatabaseHelper;
import com.xuli.vo.TruckGpsVo;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/7.
 */
public class TruckGpsDao {
    /**
     * 本篇内的所有注释内容都是被RuntimeExceptionDao这个方法替代了，想要学习可以查看源码
     */
    //本身最早版本是这样的，但是通过观察源码会发现可以使用下面的RuntimeExceptionDao来代替dao，比原来多了个异常抛出功能
//    private Dao<LoginVo,Integer> dao = null;
    //这个功能是用来替代上面的dao的，因为这里自动实现了异常获取
    private RuntimeExceptionDao run_dao = null;

    public TruckGpsDao(DatabaseHelper mHelper) {
        this.run_dao = mHelper.getRuntimeExceptionDao(TruckGpsVo.class);
    }

    /**
     * 添加一条数据
     *
     * @param bean
     * @return
     */
    public int create(TruckGpsVo bean) {
        return run_dao.create(bean);
    }

    /**
     * 删除表中所有数据
     */
    public void deleteAll() {
        run_dao.delete(queryAll());
    }

    /**
     * 更新数据
     *
     * @param bean
     */
    public void upDate(TruckGpsVo bean) {
        run_dao.update(bean);
    }

    /**
     * 查找表中所有数据
     *
     * @return
     */
    public List<TruckGpsVo> queryAll() {
        List<TruckGpsVo> lists = null;
        lists = run_dao.queryForAll();
        return lists;
    }

    /**
     * 按照要求查询所有匹配数据
     *
     * @param mDbWhere
     * @return
     */
    public List<TruckGpsVo> quaryForDetail(Map<String, Object> mDbWhere) {
        List<TruckGpsVo> list = run_dao.queryForFieldValues(mDbWhere);
        if (list != null && list.size() > 0) {
            return list;
        }
        return null;
    }
}
