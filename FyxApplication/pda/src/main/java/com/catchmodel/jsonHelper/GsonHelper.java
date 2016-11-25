package com.catchmodel.jsonHelper;


import com.catchmodel.been.ErrorBeen;
import com.catchmodel.been.LoginBeen;
import com.catchmodel.been.NoDeEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;



public class GsonHelper {
	
	public static LoginBeen jsonTolist(String json){
		Gson gson = new Gson();
		LoginBeen been = gson.fromJson(json, LoginBeen.class);
		return been;
	}
	public static ErrorBeen jsonTolisterror(String json){
		Gson gson = new Gson();
		ErrorBeen been = gson.fromJson(json, ErrorBeen.class);
		return been;
	}
	public static List<NoDeEntity> jsontoNoEntity(String json){
		Gson gson = new Gson();
		List<NoDeEntity> list = new ArrayList<NoDeEntity>();
		list = gson.fromJson(json,new TypeToken<List<NoDeEntity>>(){}.getType());
		return list;
	}
}
