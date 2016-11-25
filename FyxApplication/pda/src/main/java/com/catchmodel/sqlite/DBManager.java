package com.catchmodel.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.catchmodel.been.CatchBeen;
import com.catchmodel.been.NoDeEntity;
import com.catchmodel.initdata.FinalData;

import java.util.List;



public class DBManager {
	private DataBaseHelper hepler;
	private SQLiteDatabase db;
	public DBManager(Context context){
		hepler = new DataBaseHelper(context);
		db = hepler.getWritableDatabase();
	}
	
	public Boolean add(List<CatchBeen> list){
		boolean success = false;
		db.beginTransaction();
		try{
		for(CatchBeen been : list){
			db.execSQL("INSERT INTO CatchTable VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{been.getBranchNumber(),
					been.getBranchName(),been.getCustomerName(),been.getBranchType(),been.getAdminiStrative(),been.getPhoneNumber(),
					been.getAtmMessage(),been.getBranchState(),been.getBranchAddress(),been.getBranchposition(),been.getBranchLinkman(),been.getMonitoringNumber(),
					been.getTechnialNumber(),been.getBranchCondition(),been.getRetainCardAddress(),been.getRemark(),been.getGpsMarkOne(),
					been.getGpsMarkTwo(),been.getGpsMarkThree(),been.getBankNumber(),been.getBitmapUrl(),been.getStoretime(),been.getBranchGps(),been.getCustno1(),been.getCustno2(),been.getIsUploading()});			
		}
		db.setTransactionSuccessful();
			success = true;
		}catch(Exception e){
			success = false;
		}
		finally{
			db.endTransaction();
		}
		return success;
	}
	  
	  public Cursor queryTheCursor() {  
	        Cursor c = db.rawQuery("SELECT * FROM "+ FinalData.TABLE_NAME1, null);
	        return c;  
	    }  
	  public Cursor queryTheCursorisuploading() {  
	        Cursor c = db.rawQuery("SELECT * FROM "+FinalData.TABLE_NAME1+" where isuploading="+"'0'", null);  
	        return c;  
	    }  
	  public void queryTheCursoruploading(int id) {  
	        ContentValues values = new ContentValues();
	        values.put("isuploading", "1");
	        String[] str ={String.valueOf(id)};
	        db.update(FinalData.TABLE_NAME1, values, "_id=?",str);
	    }  
	  public int updatetable(CatchBeen been) {
	        ContentValues values = new ContentValues();
	        values.put("isuploading", "0");
	        values.put("branchgps", been.getBranchGps());
	        values.put("bitmapUrl", been.getBitmapUrl());
	        values.put("gpsMarkOne", been.getGpsMarkOne());
	        values.put("gpsMarkTwo", been.getGpsMarkTwo());
	        values.put("gpsMarkThree", been.getGpsMarkThree());
	        values.put("store", been.getStoretime());
	        String[] str ={String.valueOf(been.getBranchNumber())};
	        int id = db.update(FinalData.TABLE_NAME1, values, "branchNumber=?",str);
	        return id;
	    } 
	  public Boolean isRelpace(long branchnumber){
		  Cursor c = db.rawQuery("SELECT *  FROM "+FinalData.TABLE_NAME1 +" where branchNumber="+"'"+branchnumber+"'", null);
		  boolean is;
		  if(c.getCount() > 0){
			  is = true;
		  }else{
			  is = false;
		  }
		  c.close();
		  return is;
	  }
	  public Cursor queryDistricts() {  
	        Cursor c = db.rawQuery("SELECT distinct Districts  FROM "+FinalData.TABLE_NAME2, null);  
	        return c;  
	    } 
	  /**
	   * CustomerName
	   * @return
	   */
	  public Cursor queryBranchAddress() {  
	        Cursor c = db.rawQuery("SELECT distinct CustomerName  FROM "+FinalData.TABLE_NAME2, null);  
	        return c;  
	    } 
	  /**
	   * distinct
	   * @return
	   */
	  public Cursor queryAllDistricts(String branchName) {  
	        Cursor c = db.rawQuery("SELECT distinct Districts  FROM "+FinalData.TABLE_NAME2+" where CustomerName = '"+branchName+"'", null);  
	        return c;  
	    } 
	  /**
	   * *
	   * @return
	   */
	  public Cursor queryAllTable(String branchName,String Districts) {  
	        Cursor c = db.rawQuery("SELECT *  FROM "+FinalData.TABLE_NAME2+" where CustomerName = '"+branchName+"' and Districts = '"+Districts+"'", null);  
	        return c;  
	    } 
	  /**
	   * Address
	   * @return
	   */
	  public Cursor queryAddress(String Districts) {  
	        Cursor c = db.rawQuery("SELECT distinct Address  FROM "+FinalData.TABLE_NAME2+" where Districts = '"+Districts+"'", null);  
	        return c;  
	    }  
	  public Cursor queryData(String  Address) {  
	        Cursor c = db.rawQuery("SELECT *  FROM "+FinalData.TABLE_NAME2+" where Address = '"+Address+"'", null);  
	        return c;  
	    }  
	  
	  public Boolean Delete(int id){
			boolean success = false;
			db.beginTransaction();
			try{
				 db.execSQL("DELETE FROM "+FinalData.TABLE_NAME1+" WHERE _id="+id);
			db.setTransactionSuccessful();
				success = true;
			}catch(Exception e){
				success = false;
			}
			finally{
				db.endTransaction();
			}
			return success;
		}
	public void closeDB(){
		db.close();
	}
	public Cursor queryUserPath(){
		 Cursor c = db.rawQuery("SELECT * FROM "+FinalData.TABLE_NAME3, null); 
		 return c;
	}
	public long insertUserPath(String loginPath,String content){
		 ContentValues value = new ContentValues();
		 if(loginPath != null){
			 value.put("loginPath", loginPath);
		 }
		 if(content != null){
			 value.put("urlPath", content);
		 }
		 long count = db.insert(FinalData.TABLE_NAME3, null, value);
		 return count;
	}
	public int updataUserPath(int _id,String loginPath,String content){
		 	ContentValues value = new ContentValues();
			 if(loginPath != null){
				 value.put("loginPath", loginPath);
			 }
			 if(content != null){
				 value.put("urlPath", content);
			 }
	        String[] str ={String.valueOf(_id)};
	        int count = db.update(FinalData.TABLE_NAME3, value, "_id=?",str);
	        return count;
	}
	public Boolean addNo(List<NoDeEntity> list){
		boolean success = false;
		db.beginTransaction();
		try{
		for(NoDeEntity been : list){
			db.execSQL("INSERT INTO "+FinalData.TABLE_NAME2+" VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{
					been.getId(),been.getCode(),been.getName(),been.getCustomerId(),been.getType(),been.getDistricts(),been.getTelephone(),
					been.getAtmNo(),been.getState(),been.getAddress(),been.getContacts(),been.getDefenceTel(),been.getMonitorTel(),been.getBankTel(),
					been.getNodePosition(),been.getReturnAddress(),been.getRemarks(),been.getGisx(),been.getGisy(),been.getAddDate(),been.getAddBy(),
					been.getModifyBy(),been.getNodePosition(),been.getCustomerName(),been.getState()
			});			
		}
		db.setTransactionSuccessful();
			success = true;
		}catch(Exception e){
			success = false;
		}
		finally{
			db.endTransaction();
		}
		return success;
	}
	public void deleteAll(){
		db.delete(FinalData.TABLE_NAME2, null, null);
	}
}
