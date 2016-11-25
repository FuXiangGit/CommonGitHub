package com.xuli.map;

import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.xuli.comm.Config;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 10:12.
 */
public class Executor_Service  {
    private Executor_Service executor;
    private ExecutorService executorService;
//    private LatLng startPoint;
//    private LatLng endPoint;
//    private Marker mMoveMarker;
    private HashMap<String,Marker> saveThreadMarker = new HashMap<>(); //string 用车牌 为Key
    private RunListenterResult  listenterResult;
    private String edn="end";
    public  Executor_Service getInstance(){
        if(executor == null){
            executor = new Executor_Service();
        }
        return executor;
    }

    public ExecutorService getExecutor(){
        if(executorService==null){
            executorService =  Executors.newFixedThreadPool(Config.EXECUTORALLSIZE);//不限制线程最大数，UI限制最大数量为10
        }
        return executorService;
    }

    private  void  addHashMap(String key ,Marker value){
        saveThreadMarker.put(key,value);



    }


//    public void run(final LatLng startPoint,final  LatLng endPoint,final String  key){
//        getExecutor().submit(new Runnable() {
//            @Override
//            public void run() {
//                translationListenter(startPoint, endPoint, key);
//            }
//        });
//    }


    public void run(){
        getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("test=", "" + Thread.currentThread().getName() + "---->" + Thread.currentThread().getId());
                    Thread.sleep(2000);
                    listenterResult.runResult(Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


//    public void run1(){
//        Future<String> task = getExecutor().submit(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                Thread.sleep(2000);
//
//                return edn;
//            }
//        });
//
//        listenterResult.runResult(task.toString());
//
//    }







//    public void  setFromPoint(LatLng startPoint){
//        this.startPoint =startPoint;
//    }
//
//    public void setToPoint(LatLng endPoint){
//        this.endPoint =endPoint;
//    }
//
//    public void setmMoveMarker(Marker mMoveMarker){
//        this.mMoveMarker = mMoveMarker;
//    }

    private void translationListenter(LatLng startPoint, LatLng endPoint,String mMoveMarker){





        //运行完成返回结果
        listenterResult.runResult("");

    }



    public interface   RunListenterResult{
        void  runResult(String car_num);
    }

    public  void  setListenterResult(RunListenterResult listenterResult){
        this.listenterResult = listenterResult;

    }

}
