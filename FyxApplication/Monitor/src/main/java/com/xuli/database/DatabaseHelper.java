package com.xuli.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xuli.Util.PDALogger;
import com.xuli.monitor.R;
import com.xuli.vo.LoginVo;
import com.xuli.vo.TruckChildVo;
import com.xuli.vo.TruckGpsVo;
import com.xuli.vo.TruckGroupVo;
import com.xuli.vo.TruckVo;

import java.sql.SQLException;

/**
 * Created by Administrator on 2015/12/7.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    /**
     * 数据库名
     */
    private static final String TABLE_NAME = "monitor_test.db";
    /**
     * 数据库版本
     */
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper instance;

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public DatabaseHelper(Context context) {
//        super(context, TABLE_NAME, null, DATABASE_VERSION);
        super(context, TABLE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * 创建数据库
     * 只有没有对应表的时候才会创建
     *
     * @param database
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            PDALogger.d("是否进来创建了");
            TableUtils.createTable(connectionSource, LoginVo.class);
            TableUtils.createTable(connectionSource, TruckGpsVo.class);
            TableUtils.createTable(connectionSource, TruckVo.class);
            TableUtils.createTable(connectionSource, TruckChildVo.class);
            TableUtils.createTable(connectionSource, TruckGroupVo.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            PDALogger.d("是否进来更新了");
            TableUtils.dropTable(connectionSource, LoginVo.class, true);
            TableUtils.dropTable(connectionSource, TruckGpsVo.class, true);
            TableUtils.dropTable(connectionSource, TruckVo.class, true);
            TableUtils.createTable(connectionSource, TruckChildVo.class);
            TableUtils.createTable(connectionSource, TruckGroupVo.class);

            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public void close() {
        super.close();
    }
}
