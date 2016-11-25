package com.xuli.map;

import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import java.util.List;

/**
 * Created by Administrator on 15:38.
 */
public class CarThread extends Thread implements Runnable {

    private List<LatLng> LatLngList ;
    private Marker mMoveMarker;
    private boolean isPause = false;
    public  boolean isClose = false;
    private static  CarThread carThread;
    private static  int TIME_INTERVAL;
    private static  double DISTANCE ;
    private int count = 0;//记录移动到具体的位置
    public static int pause = 0;//保存暂停位置记录
    public static  boolean  isDestroy;//线程是否存在；
    public static  int statusTyppe ;// 1为暂停状态 ，0 为运行状态
    private EndListener  endListener;//轨迹回放完成 监听


    public synchronized CarThread getInstance() {
        if (carThread == null) {
            carThread = new CarThread();
            Log.i("CarThread是否重建", "是");
            //重新创建线程
            isDestroy(true);

        } else {
            //线程已近存在
            isDestroy(false);
        }


        return carThread;
    }


    public boolean isDestroy(boolean destroy){
        isDestroy = destroy;
        return isDestroy;
    }

    public  static void setTime(int time){
        TIME_INTERVAL = time;
    }

    public  static  void  setDistance(double distance){
        DISTANCE = distance;
    }


    public  void setList(List<LatLng> latLngs){
        this.LatLngList = latLngs;
    }


    public  void setMarker(Marker marker){
        this.mMoveMarker = marker;
        mMoveMarker.setRotateAngle((float) getAngle(pause));
    }

    /**
     * 暂停线程
     */
    public synchronized void onThreadPause() {
        pause += count;
        isPause = true;
        statusTyppe = 1;
        Log.i("isPause_onThreadPause-", isPause + "");

    }

    /**
     * 线程等待,不提供给外部调用
     */
    private void onThreadWait() {
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 线程继续运行
     */
    public synchronized void onThreadResume() {
        isPause = false;
        statusTyppe = 0;
        this.notify();
    }


    public void  setProgress(int progress){
        if(statusTyppe == 0) {
            pause = progress - count;
        }else{
            pause = progress ;
        }

    }


    /**
     * 关闭线程
     */
    public synchronized void closeThread() {
        try {
            notify();
            setClose(true);
            interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setClose(boolean Close) {
        this.isClose = Close;

    }

    /**
     * 循环进行移动逻辑
     */
    @Override
    public void run() {
        while (!isClose && !isInterrupted()) {
            if (LatLngList.size() > 0 && !isPause) {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    if (!isPause) {
                        count = i;
                        if ((i + pause) < (LatLngList.size() - 1)) {
                            Log.i("i == i", i + "");
                            Log.i("pause == i", pause + "");
                            endListener.progress((int) (((i + pause) / (LatLngList.size() - 2.0)) * 100));
                            LatLng startPoint = LatLngList.get((i + pause));
                            LatLng endPoint = LatLngList.get((i + pause) + 1);
                            mMoveMarker
                                    .setPosition(startPoint);
                            // 设置Marker图片旋转角度，正北开始，逆时针计算。
                            mMoveMarker.setRotateAngle((float) getAngle(startPoint,
                                    endPoint));

                            double slope = getSlope(startPoint, endPoint);
                            boolean isReverse = isReverse(startPoint, endPoint, slope);
                            double moveDistance = isReverse ? getMoveDistance(slope) : -1 * getMoveDistance(slope);
                            double intercept = getInterception(slope, startPoint);
                            for (double j = getStart(startPoint, slope); (j > getEnd(endPoint, slope)) == isReverse; j = j
                                    - moveDistance) {
                                LatLng latLng = null;
                                if(!isPause){
                                    if (slope == 0) {
                                        latLng = new LatLng(startPoint.latitude, j);
                                    } else if (slope == Double.MAX_VALUE) {
                                        latLng = new LatLng(j, startPoint.longitude);
                                    } else {

                                        latLng = new LatLng(j, (j - intercept) / slope);
                                    }
                                    mMoveMarker.setPosition(latLng);
                                    try {
                                        Thread.sleep(TIME_INTERVAL);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }

                        if ((i + pause) == (LatLngList.size() - 2)) {
                            isPause = true;
                            statusTyppe = 1;
                            pause =0;
                            endListener.end();

                        }

                    }else{
                        break;
                    }
                }
            } else {
                onThreadWait();
            }
        }
    }



    /**
     * 计算每次移动的距离
     */
    private double getMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE||slope==0) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 判断是否为反序
     * */
    private boolean isReverse(LatLng startPoint,LatLng endPoint,double slope){
        if(slope==0){
            return	startPoint.longitude>endPoint.longitude;
        }
        return (startPoint.latitude > endPoint.latitude);

    }

    /**
     * 获取循环初始值大小
     * */
    private double getStart(LatLng startPoint,double slope){
        if(slope==0){
            return	startPoint.longitude;
        }
        return  startPoint.latitude;
    }

    /**
     * 获取循环结束大小
     * */
    private double getEnd(LatLng endPoint,double slope){
        if(slope==0){
            return	endPoint.longitude;
        }
        return  endPoint.latitude;
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;

    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {

        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }


    /**
     * 根据点获取图标转的角度
     */
    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= LatLngList.size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = LatLngList.get(startIndex);
        LatLng endPoint = LatLngList.get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }



    //轨迹完成回调接口
    public interface  EndListener{

         boolean  end();

         void progress(int progerss);
    }

    //进度条回调接口
    public  void setListener(EndListener  listener){

        this.endListener = listener;
    }



}
