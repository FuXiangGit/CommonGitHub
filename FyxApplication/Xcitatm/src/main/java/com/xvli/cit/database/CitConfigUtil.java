package com.xvli.cit.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.xvli.cit.vo.LineVo;
import com.xvli.cit.vo.LoginVo;
import com.xvli.cit.vo.OperateLogVo;
import com.xvli.cit.vo.SpecialOutVo;
import com.xvli.cit.vo.TaskVo;
import com.xvli.cit.vo.TruckVo;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Administrator on 2016/09/28.
 */
public class CitConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[]{
            LoginVo.class, OperateLogVo.class,SpecialOutVo.class, TruckVo.class,TaskVo.class, LineVo.class


    };

    public static void main(String[] args) throws IOException, SQLException {
        writeConfigFile(new File("cit_config.txt"), classes);
    }
}

