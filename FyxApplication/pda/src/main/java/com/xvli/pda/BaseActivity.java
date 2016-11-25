package com.xvli.pda;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.application.PdaApplication;
import com.xvli.dao.DatabaseHelper;
import com.xvli.utils.ActivityManager;

import org.xutils.x;

public class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_ACTION     = "android.intent.extra.ACTION";
    DatabaseHelper databaseHelper = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        PdaApplication.getInstance().addActivity(this);
        PdaApplication.getInstance().pushActivity(this);
        x.view().inject(this);
    }
    /**
     * You'll need this in your class to get the helper from the manager once per class.
     */
    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
		 * You'll need this in your class to release the helper when done.
		 */
       /* if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }*/
    }
}
