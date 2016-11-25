package com.xuli.Util;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.xuli.vo.LoginVo;
import com.xuli.vo.TruckChildVo;
import com.xuli.vo.TruckGpsVo;
import com.xuli.vo.TruckGroupVo;
import com.xuli.vo.TruckVo;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Administrator on 2016/09/28.
 */
public class MonitorConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[]{
            LoginVo.class, TruckGpsVo.class, TruckVo.class, TruckChildVo.class, TruckGroupVo.class



    };

    public static void main(String[] args) throws IOException, SQLException {
//        writeConfigFile(new File("PATH/TO/ANDROID/PROJECT/src/main/res/raw/ormlite_config.txt"), classes);
        writeConfigFile(new File("ormlite_config.txt"), classes);
    }
}

