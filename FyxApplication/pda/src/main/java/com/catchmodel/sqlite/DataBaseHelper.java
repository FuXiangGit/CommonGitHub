package com.catchmodel.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.catchmodel.initdata.FinalData;


public class DataBaseHelper extends SQLiteOpenHelper {

	public DataBaseHelper(Context context){
		 super(context, FinalData.DBNAME, null, FinalData.VERSION);
	}	
	
	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS "+FinalData.TABLE_NAME1+"(_id INTEGER PRIMARY KEY AUTOINCREMENT" +
				",branchNumber INT,branchName TEXT,customerName TEXT,branchType TEXT,adminiStrative TEXT,phoneNumber INT," +
				"atmMessage TEXT,branchState TEXT,branchAddress TEXT,branchposition TEXT,branchLinkman TEXT,monitoringNumber INT,technialNumber INT," +
				"branchCondition TEXT,retainCardAddress TEXT,remark TEXT,gpsMarkOne TEXT,gpsMarkTwo TEXT,gpsMarkThree TEXT,bankNumber INT,bitmapUrl TEXT,store TEXT,branchgps TEXT,custno1 TEXT,custno2 TEXT,isuploading TEXT)");
		db.execSQL("CREATE TABLE IF NOT EXISTS "+FinalData.TABLE_NAME2+"(_id INTEGER PRIMARY KEY AUTOINCREMENT" +
				",number INT,Code TEXT,Name TEXT,CustomerId INTEGER,Type TEXT,Districts TEXT,Telephone TEXT,AtmNo TEXT,State TEXT" +
				",Address TEXT,Contacts TEXT,DefenceTel TEXT,MonitorTel TEXT,BankTel TEXT,Nodesituation TEXT,ReturnAddress TEXT,Remarks TEXT,Gisx TEXT,Gisy TEXT" +
				",AddDate TEXT,AddBy TEXT,ModifyBy TEXT,NodePosition TEXT,CustomerName TEXT,StateName TEXT)");
		db.execSQL("CREATE TABLE IF NOT EXISTS "+FinalData.TABLE_NAME3+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,loginPath TEXT,urlPath TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
