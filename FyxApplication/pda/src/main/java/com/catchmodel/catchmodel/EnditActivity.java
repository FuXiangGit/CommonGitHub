package com.catchmodel.catchmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.catchmodel.sqlite.DBManager;
import com.xvli.pda.R;


public class EnditActivity extends Activity {
	private EditText edt_login;
	private EditText edt_path;
	private String login;
	private String content;
	private int state = 1;
	private Button btn_editsaves;
	private Button btn_editsave;
	private int countpath = 1;
	private int countlogin = 1;
	public interface getLogin{
		int LOGIN = 1;
		int CONTENT = 2;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editpath);
		initView();
		initData();
	}
	private void initView(){
		edt_login = (EditText) findViewById(R.id.edt_login);
		edt_path = (EditText) findViewById(R.id.edt_path);
		edt_login.setFocusableInTouchMode(false);
		edt_path.setFocusableInTouchMode(false);
		btn_editsaves = (Button) findViewById(R.id.btn_editsaves);
		btn_editsave = (Button) findViewById(R.id.btn_editsave);	
	}
	private void initData(){
		btn_editsaves.setText("点击编辑");
		btn_editsave.setText("点击编辑");
		DBManager db = new DBManager(this);
		Cursor c =  db.queryUserPath();
		if(c.moveToFirst()){
			edt_login.setText(c.getString(1));
			edt_path.setText(c.getString(2));
		}
		c.close();
		db.closeDB();
	}
	public void GoEditss(View v){	
		btn_editsaves.setText("配置");
		if(countlogin == 1){			
			edt_login.setText("");
			edt_login.setFocusableInTouchMode(true);
			edt_login.requestFocus();
			countlogin++;
		}else{		
			login = edt_login.getText().toString();
			if(login.equals("")){
				return;
			}
			state = getLogin.LOGIN;
			showLog();
		}
	}
	
	public void GoEdits(View v){
		btn_editsave.setText("配置");
		if(countpath == 1){			
			edt_path.setText("");
			edt_path.setFocusableInTouchMode(true);
			edt_path.requestFocus();
			countpath++;
		}else{			
			content = edt_path.getText().toString();
			if(content.equals("")){
				return;
			}
			state = getLogin.CONTENT;
			showLog();
		}
	}
	private void showLog(){
		new AlertDialog.Builder(this)
		.setTitle("警告")
		.setMessage("配置不正确可能引起程序错误！是否配置？")
		.setNegativeButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						if(state == getLogin.LOGIN){
							updateUrllogin(login,null);
						}
						if(state == getLogin.CONTENT){
							updateUrllogin(null,content);
						}
					}
				}).setPositiveButton("取消", null).show();
	}
	
	private void updateUrllogin(String login,String content){
		DBManager db = new DBManager(this);
		int count = db.updataUserPath(1, login,content);
		if(count > 0){
			Toast.makeText(this, "配置成功", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "配置失败", Toast.LENGTH_SHORT).show();
		}
		db.closeDB();
	}
}
