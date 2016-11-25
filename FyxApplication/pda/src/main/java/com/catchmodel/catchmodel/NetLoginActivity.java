package com.catchmodel.catchmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.catchmodel.been.LoginBeen;
import com.catchmodel.gps.CatchGPS;
import com.catchmodel.initdata.FinalData;
import com.catchmodel.sqlite.DBManager;
import com.catchmodel.sqlite.SharedProHelper;
import com.xvli.pda.R;

/**
 * Created by Administrator on 16:15.
 */
public class NetLoginActivity extends Activity{
    private EditText edt_username;
    private EditText edt_usePass;
    private EditText edt_username2;
    private EditText edt_usePass2;
    private ProgressBar pro_bar;
    private Button btn_getpost;
    static int SUCCESS = 1;
    static int CANLE = 2;
    static int USERCANLE = 3;
    static int NOTROUP = 4;
    Handler hander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    pro_bar.setVisibility(View.VISIBLE);
                    btn_getpost.setVisibility(View.GONE);
                    break;
                case 2:
                    pro_bar.setVisibility(View.GONE);
                    btn_getpost.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "网络不通",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    pro_bar.setVisibility(View.GONE);
                    btn_getpost.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "账号密码错误",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    pro_bar.setVisibility(View.GONE);
                    btn_getpost.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "不在同一组不能登陆",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    private Button btn_editurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUrl();
        initView();
    }
    private void initUrl(){
        DBManager db = new DBManager(this);
        Cursor c = db.queryTheCursor();
        int count = c.getCount();
        if(count == 0){
            String login = "http://121.40.174.38:8080";
            String content = "http://116.236.240.252:8086";
            db.insertUserPath(login,content);
        }
        c.close();
        db.closeDB();
    }
    private void initView() {
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_usePass = (EditText) findViewById(R.id.edt_userpass);
        edt_username2 = (EditText) findViewById(R.id.edt_username2);
        edt_usePass2 = (EditText) findViewById(R.id.edt_userpass2);
//		edt_username.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_username2.setInputType(InputType.TYPE_CLASS_NUMBER);
        pro_bar = (ProgressBar) findViewById(R.id.pro_bar);
        btn_getpost = (Button) findViewById(R.id.btn_getpost);
        btn_editurl = (Button) findViewById(R.id.btn_editurl);
        pro_bar.setVisibility(View.GONE);
        btn_getpost.setVisibility(View.VISIBLE);
        SharedProHelper sp = new SharedProHelper(this);
        sp.clearData("itemid", "int");
    }
    public void GoEdit(View v){
        Intent i = new Intent(this, EnditActivity.class);
        startActivity(i);
    }
    public void PostHttp(View v) {
        if(edt_username.getText().toString().equals("admin") &&
                edt_usePass.getText().toString().equals("admin123")){
            Intent i = new Intent(this, EnditActivity.class);
            startActivity(i);
            return;
        }

        if (!CatchGPS.isConn(this)) {
            Toast.makeText(getApplicationContext(), "请打开网络", Toast.LENGTH_SHORT).show();
            return;
        }
        pro_bar.setVisibility(View.VISIBLE);
        btn_getpost.setVisibility(View.GONE);
        DBManager db = new DBManager(this);
        Cursor c = db.queryUserPath();
        String content = "";
        if(c.moveToFirst()){
            content = c.getString(1);
        }
        c.close();
        db.closeDB();
        if(content.equals("")){
            return;
        }
        //http://121.40.174.38:8080/pda-server/pda/user/iglogin.json?jobnumber1=39303&password1=123&jobnumber2=39298&password2=123
        final String url = content + "/pda-server/pda/user/iglogin.json?jobnumber1="
                + edt_username.getText().toString()
                + "&password1="
                + edt_usePass.getText().toString()
                + "&jobnumber2="
                + edt_username2.getText().toString()
                + "&password2="
                + edt_usePass2.getText().toString();

        //测试
        LoginBeen been =new LoginBeen();
        been.setName1(edt_username.getText().toString());
        been.setName2(edt_username2.getText().toString());
        been.setJobnumber1(edt_usePass.getText().toString());
        been.setJobnumber2(edt_usePass2.getText().toString());

        StartNext(been);





//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				Message msg = new Message();
//				HttpPost post = new HttpPost(url);
//				HttpClient http = new DefaultHttpClient();
//				try {
//					HttpResponse response = http.execute(post);
//					int code = response.getStatusLine().getStatusCode();
//					String json = EntityUtils.toString(response.getEntity());
//					if (code == 200) {
//						LoginBeen been = GsonHelper.jsonTolist(json);
//						if (been != null) {
//							StartNext(been);
//						}
//					} else if (code == 300) {
//						ErrorBeen error = GsonHelper.jsonTolisterror(json);
//						if(error.getException()!=null){
//						if (error.getException().equals(
//								"error.login.user.notInOneGroup")) {
//							msg.what = NOTROUP;
//							hander.sendMessage(msg);
//						} else {
//							msg.what = USERCANLE;
//							hander.sendMessage(msg);
//						}
//					} else {
//						msg.what = CANLE;
//						hander.sendMessage(msg);
//					}
//					}else{
//						msg.what = USERCANLE;
//						hander.sendMessage(msg);
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					msg.what = CANLE;
//					hander.sendMessage(msg);
//					e.printStackTrace();
//				}
//			}
//		}).start();

    }

    private void StartNext(LoginBeen been) {
        Intent i = new Intent();
        Bundle bl = new Bundle();
        bl.putSerializable(FinalData.LOGIN_KEY, been);
        i.putExtras(bl);
        i.setClass(this, BeginActivity.class);
        startActivity(i);
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        edt_username.requestFocus();
        edt_username.setText("");
        edt_usePass.setText("");
        edt_username2.setText("");
        edt_usePass2.setText("");
        pro_bar.setVisibility(View.GONE);
        btn_getpost.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("是否退出？")
                    .setNegativeButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    SharedProHelper sp = new SharedProHelper(getApplicationContext());
                                    sp.clearData("upload", "String");
                                    closeActivity();
                                }
                            }).setPositiveButton("关闭", null).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void closeActivity() {
        this.finish();
    }
}
