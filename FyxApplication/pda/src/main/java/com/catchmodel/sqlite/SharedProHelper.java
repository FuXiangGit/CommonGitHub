package com.catchmodel.sqlite;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedProHelper {
	SharedPreferences sp;
	private Editor e;
	public SharedProHelper(Context context){
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		e = sp.edit();
	}
	public void SaveData(String name,String str,String is){
		if(is.equals("String")){
			e.putString(name, str);
		}
		if(is.equals("int")){
			e.putInt(name,Integer.parseInt(str));
		}
		e.commit();
	}
	public String getString(String name,String is){
		if(is.equals("String")){
			return sp.getString(name, null);
		}
		if(is.equals("int")){
			return String.valueOf(sp.getInt(name, -1));
		}
		return "";
	}
	public void clearData(String name,String is){
		if(is.equals("String")){
			e.putString(name, "no");
		}
		if(is.equals("int")){
			e.putInt(name,-1);
		}
		e.commit();
	}
}
