package com.xvli.comm;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 获取出入库物品清单
 * Created by Administrator on 2016/5/12.
 */
public class loaderOutIn {
    private Context mContext;
    private String clientid;
    private AtmBoxBagDao box_dao;
    //出入库id
    private String inventoryid;
    //调度id
    private String dispatchid;
    private AtmMoneyDao money_dao;

    public loaderOutIn() {
    }

    public loaderOutIn(Context mContext,AtmMoneyDao money_dao,AtmBoxBagDao box_dao, String clientid, String dispatchid, String inventoryid) {
        this.mContext = mContext;
        this.money_dao = money_dao;
        this.box_dao = box_dao;
        this.clientid = clientid;
        this.dispatchid = dispatchid;
        this.inventoryid = inventoryid;
    }

    public void getInOUt() {
        HashMap<String, String> value_in = new HashMap<String, String>();
        value_in.put("clientid", clientid);
        value_in.put("inventoryid", inventoryid);
        XUtilsHttpHelper.getInstance().doPost(Config.GET_IN_OUT, value_in, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
                PDALogger.d("-轮询事件出入库---->" + result);
                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常

                            if (new Util().setKey().equals(Config.CUSTOM_NAME)) { // 迪堡
                                JSONArray outData = jsonTotal.optJSONArray("itemoutlist");
                                AtmmoneyBagVo moneyVo;
                                if( outData.length() > 0){
                                    //如果接口有数据 就把任务中运送物品 isOut 设置为N  直接以接口数据为准
                                    HashMap<String, Object> is_out = new HashMap<String, Object>();
                                    is_out.put("isOut", "Y");
                                    is_out.put("sendOrRecycle", 0);
                                    is_out.put("isScan", "N");
                                    List<AtmmoneyBagVo> outList = money_dao.quaryForDetail(is_out);
                                    if (outList != null && outList.size() > 0) {
                                        for (int k = 0; k < outList.size(); k++) {
                                            AtmmoneyBagVo boxBagVo = outList.get(k);
                                            boxBagVo.setIsOut("N");
                                            money_dao.upDate(boxBagVo);
                                        }
                                    }
                                }

                                for (int i = 0; i < outData.length(); i++) {

                                    JSONObject goodinfo = outData.getJSONObject(i);
                                    String barcoe = goodinfo.optString("barcode");//编码
                                    String line = goodinfo.optString("bizinfo");//所属线路名字

                                    HashMap<String, Object> in_box = new HashMap<String, Object>();
                                    in_box.put("barcodeno", barcoe);
                                    List<AtmmoneyBagVo> boxBagVoList = money_dao.quaryForDetail(in_box);
                                    if (boxBagVoList != null && boxBagVoList.size() > 0) {
                                        moneyVo = boxBagVoList.get(boxBagVoList.size() - 1);
                                        HashMap<String, Object> is_scan = new HashMap<String, Object>();
                                        is_scan.put("isScan", "Y");
                                        List<AtmmoneyBagVo> scanList = money_dao.quaryForDetail(is_scan);
                                        if (scanList != null && scanList.size() > 0) {//已经扫描过的不做处理
                                            if (Regex.isBag(barcoe)) { // 钞包
                                                moneyVo.setBagtype(6);
                                                moneyVo.setIsOut("Y");
                                                moneyVo.setInPda("Y");//如果一个物品多次出库 回库 则出库状态为 Y 并且在Pda 上
                                                moneyVo.setBarcode(barcoe);
                                                moneyVo.setClientid(clientid);
                                                moneyVo.setSendOrRecycle(0);
                                                money_dao.upDate(moneyVo);
                                            }
                                            if (Regex.isDiChaoBag(barcoe)) { //抄袋
                                                moneyVo.setBagtype(1);
                                                moneyVo.setIsOut("Y");
                                                moneyVo.setInPda("Y");
                                                moneyVo.setBarcode(barcoe);
                                                moneyVo.setClientid(clientid);
                                                moneyVo.setSendOrRecycle(0);
                                                money_dao.upDate(moneyVo);
                                            }
                                        } else {
                                            if (Regex.isBag(barcoe)) { // 钞包
                                                moneyVo.setBagtype(6);
                                                moneyVo.setIsOut("Y");
                                                moneyVo.setInPda("Y");//如果一个物品多次出库 回库 则出库状态为 Y 并且在Pda 上
                                                moneyVo.setIsScan("N");//一个物品多次出库回库  则出库时  物品为 未扫描
                                                moneyVo.setBarcode(barcoe);
                                                moneyVo.setClientid(clientid);
                                                moneyVo.setSendOrRecycle(0);
                                                money_dao.upDate(moneyVo);
                                            }
                                        }
                                        if (Regex.isDiChaoBag(barcoe)) { //抄袋
                                            moneyVo.setBagtype(1);
                                            moneyVo.setIsOut("Y");
                                            moneyVo.setInPda("Y");
                                            moneyVo.setBarcode(barcoe);
                                            moneyVo.setClientid(clientid);
                                            moneyVo.setSendOrRecycle(0);
                                            money_dao.upDate(moneyVo);
                                        }

                                    } else {//2.不存在就创建
                                        moneyVo = new AtmmoneyBagVo();
                                        if (Regex.isBag(barcoe)) { // 钞包
                                            moneyVo.setBagtype(6);
                                            moneyVo.setIsOut("Y");
                                            moneyVo.setInPda("Y");
                                            moneyVo.setIsScan("N");
                                            moneyVo.setBarcode(barcoe);
                                            moneyVo.setClientid(clientid);
                                            moneyVo.setSendOrRecycle(0);
                                            money_dao.create(moneyVo);
                                        }
                                        if (Regex.isDiChaoBag(barcoe)) { //抄袋
                                            moneyVo.setBagtype(1);
                                            moneyVo.setIsOut("Y");
                                            moneyVo.setInPda("Y");
                                            moneyVo.setBarcode(barcoe);
                                            moneyVo.setClientid(clientid);
                                            moneyVo.setSendOrRecycle(0);
                                            money_dao.create(moneyVo);
                                        }
                                    }
                                }

                                //入库  已经入库的物品 即不在pda上 不需要出库
                                JSONArray inData = jsonTotal.optJSONArray("iteminlist");
                                AtmmoneyBagVo inbagVo;
                                for (int i = 0; i < inData.length(); i++) {

                                    JSONObject goodinfo = inData.getJSONObject(i);
                                    String barcoe = goodinfo.optString("barcode");//编码
                                    String ilne = goodinfo.optString("bizinfo");//所属线路名字

                                    HashMap<String, Object> in_box = new HashMap<String, Object>();
                                    in_box.put("barcodeno", barcoe);
                                    List<AtmmoneyBagVo> boxBagVoList = money_dao.quaryForDetail(in_box);
                                    if (boxBagVoList != null && boxBagVoList.size() > 0) {
                                        inbagVo = boxBagVoList.get(boxBagVoList.size() - 1);
                                        if (Regex.isBag(barcoe)) { // 钞包
                                            inbagVo.setBagtype(6);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcode(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            money_dao.upDate(inbagVo);
                                        }
                                        if (Regex.isDiChaoBag(barcoe)) { //抄袋
                                            inbagVo.setBagtype(1);
                                            inbagVo.setIsOut("Y");
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcode(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(0);
                                            money_dao.upDate(inbagVo);
                                        }
                                    } else {//2.不存在就创建
                                        inbagVo = new AtmmoneyBagVo();
                                        if (Regex.isBag(barcoe)) { // 钞包
                                            inbagVo.setBagtype(6);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcode(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            money_dao.create(inbagVo);
                                        }

                                        if (Regex.isDiKaChao(barcoe)) { // 卡钞
                                            inbagVo.setBagtype(2);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcode(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            money_dao.create(inbagVo);
                                        }
                                        if (Regex.isDiChaoBag(barcoe)) { //抄袋
                                            inbagVo.setBagtype(1);
                                            inbagVo.setIsOut("Y");
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcode(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(0);
                                            money_dao.create(inbagVo);
                                        }
                                    }
                                }
                                mContext.sendBroadcast(new Intent(Config.GOODS_OUT));//出入库界面刷新

                                getDobaoInOut();
                            }  else if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国项目出入库

                                getTaiOutInData(jsonTotal);

                            }else {
                                //1.已经存在只更新状态
                                //2.不存在就创建
                                //出库  根据规则确定类型  钞箱抄袋  0为钞箱，1为钞袋    2 卡钞  3 废钞    setSendOrRecycle运送状态0，
                                JSONArray outData = jsonTotal.optJSONArray("itemoutlist");
                                AtmBoxBagVo bagVo;

                                if(outData.length() > 0){
                                    //如果接口有数据 就把任务中运送物品 isOut 设置为N  直接以接口数据为准
                                    HashMap<String, Object> is_out = new HashMap<String, Object>();
                                    is_out.put("isOut", "Y");
                                    is_out.put("sendOrRecycle", 0);
                                    is_out.put("isScan", "N");
                                    List<AtmBoxBagVo> outList = box_dao.quaryForDetail(is_out);
                                    if (outList != null && outList.size() > 0) {
                                        for (int k = 0; k < outList.size(); k++) {
                                            AtmBoxBagVo boxBagVo = outList.get(k);
                                            outList.get(k).setIsOut("N");
                                            box_dao.upDate(boxBagVo);
                                        }
                                    }
                                }
                                for (int i = 0; i < outData.length(); i++) {

                                    JSONObject goodinfo = outData.getJSONObject(i);
                                    String barcoe = goodinfo.optString("barcode");//编码
                                    String line = goodinfo.optString("bizinfo");//所属线路名字

                                    HashMap<String, Object> in_box = new HashMap<String, Object>();
                                    in_box.put("barcodeno", barcoe);
                                    List<AtmBoxBagVo> boxBagVoList = box_dao.quaryForDetail(in_box);
                                    if (boxBagVoList != null && boxBagVoList.size() > 0) {
                                        bagVo = boxBagVoList.get(boxBagVoList.size() - 1);
                                        HashMap<String, Object> is_scan = new HashMap<String, Object>();
                                        is_scan.put("isScan", "Y");
                                        List<AtmBoxBagVo> scanList = box_dao.quaryForDetail(is_scan);
                                        if (scanList != null && scanList.size() > 0) {//已经扫描过的不做处理
                                            if (Regex.isChaoBox(barcoe)) { // 钞箱
                                                bagVo.setBagtype(0);
                                                bagVo.setIsOut("Y");
                                                bagVo.setInPda("Y");//如果一个物品多次出库 回库 则出库状态为 Y 并且在Pda 上
                                                bagVo.setBarcodeno(barcoe);
                                                bagVo.setClientid(clientid);
                                                bagVo.setSendOrRecycle(0);
                                                box_dao.upDate(bagVo);
                                            }

                                            if (Regex.isChaoBag(barcoe)) { //抄袋
                                                bagVo.setBagtype(1);
                                                bagVo.setIsOut("Y");
                                                bagVo.setInPda("Y");
                                                bagVo.setBarcodeno(barcoe);
                                                bagVo.setClientid(clientid);
                                                bagVo.setSendOrRecycle(0);
                                                box_dao.upDate(bagVo);
                                            }
                                        } else {
                                            if (Regex.isChaoBox(barcoe)) { // 钞箱
                                                bagVo.setBagtype(0);
                                                bagVo.setIsOut("Y");
                                                bagVo.setInPda("Y");//如果一个物品多次出库 回库 则出库状态为 Y 并且在Pda 上
                                                bagVo.setIsScan("N");//一个物品多次出库回库  则出库时  物品为 未扫描
                                                bagVo.setBarcodeno(barcoe);
                                                bagVo.setClientid(clientid);
                                                bagVo.setSendOrRecycle(0);
                                                box_dao.upDate(bagVo);
                                            }

                                            if (Regex.isChaoBag(barcoe)) { //抄袋
                                                bagVo.setBagtype(1);
                                                bagVo.setIsOut("Y");
                                                bagVo.setInPda("Y");
                                                bagVo.setIsScan("N");
                                                bagVo.setBarcodeno(barcoe);
                                                bagVo.setClientid(clientid);
                                                bagVo.setSendOrRecycle(0);
                                                box_dao.upDate(bagVo);
                                            }
                                        }


                                    } else {//2.不存在就创建
                                        bagVo = new AtmBoxBagVo();
                                        if (Regex.isChaoBox(barcoe)) { // 钞箱
                                            bagVo.setBagtype(0);
                                            bagVo.setIsOut("Y");
                                            bagVo.setInPda("Y");
                                            bagVo.setIsScan("N");
                                            bagVo.setBarcodeno(barcoe);
                                            bagVo.setClientid(clientid);
                                            bagVo.setSendOrRecycle(0);
                                            box_dao.create(bagVo);
                                        }

                                        if (Regex.isChaoBag(barcoe)) { //抄袋
                                            bagVo.setBagtype(1);
                                            bagVo.setIsOut("Y");
                                            bagVo.setInPda("Y");
                                            bagVo.setIsScan("N");
                                            bagVo.setBarcodeno(barcoe);
                                            bagVo.setClientid(clientid);
                                            bagVo.setSendOrRecycle(0);
                                            box_dao.create(bagVo);
                                        }

                                    }


                                }

                                //入库  已经入库的物品 即不在pda上 不需要出库
                                JSONArray inData = jsonTotal.optJSONArray("iteminlist");
                                AtmBoxBagVo inbagVo;
                                for (int i = 0; i < inData.length(); i++) {

                                    JSONObject goodinfo = inData.getJSONObject(i);
                                    String barcoe = goodinfo.optString("barcode");//编码
                                    String ilne = goodinfo.optString("bizinfo");//所属线路名字

                                    HashMap<String, Object> in_box = new HashMap<String, Object>();
                                    in_box.put("barcodeno", barcoe);
                                    List<AtmBoxBagVo> boxBagVoList = box_dao.quaryForDetail(in_box);
                                    if (boxBagVoList != null && boxBagVoList.size() > 0) {
                                        inbagVo = boxBagVoList.get(boxBagVoList.size() - 1);
                                        if (Regex.isChaoBox(barcoe)) { // 钞箱
                                            inbagVo.setBagtype(0);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcodeno(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            box_dao.upDate(inbagVo);
                                        }

                                        if (Regex.isChaoBag(barcoe)) { //抄袋
                                            inbagVo.setBagtype(1);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcodeno(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            box_dao.upDate(inbagVo);
                                        }
                                        if (Regex.isKaChao(barcoe)) { // 卡钞
                                            inbagVo.setBagtype(2);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcodeno(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            box_dao.upDate(inbagVo);
                                        }

                                        if (Regex.isFeiChao(barcoe)) { //废钞
                                            inbagVo.setBagtype(3);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcodeno(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            box_dao.upDate(inbagVo);
                                        }

                                    } else {//2.不存在就创建
                                        inbagVo = new AtmBoxBagVo();
                                        if (Regex.isChaoBox(barcoe)) { // 钞箱
                                            inbagVo.setBagtype(0);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcodeno(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            box_dao.create(inbagVo);
                                        }

                                        if (Regex.isChaoBag(barcoe)) { //抄袋
                                            inbagVo.setBagtype(1);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcodeno(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            box_dao.create(inbagVo);
                                        }
                                        if (Regex.isKaChao(barcoe)) { // 卡钞
                                            inbagVo.setBagtype(2);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcodeno(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            box_dao.create(inbagVo);
                                        }

                                        if (Regex.isFeiChao(barcoe)) { //废钞
                                            inbagVo.setBagtype(3);
                                            inbagVo.setInPda("N");
                                            inbagVo.setBarcodeno(barcoe);
                                            inbagVo.setClientid(clientid);
                                            inbagVo.setSendOrRecycle(1);
                                            box_dao.create(inbagVo);
                                        }
                                    }
                                }
                                mContext.sendBroadcast(new Intent(Config.GOODS_OUT));//出入库界面刷新
                            }
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

            }
        });


    }


    //泰国项目 出入库物品
    public void getTaiOutInData(JSONObject jsonTotal) {

        JSONArray outData = jsonTotal.optJSONArray("itemoutlist");
        AtmmoneyBagVo moneyVo;
        if (outData.length() > 0) {
            //如果接口有数据 就把任务中运送物品 isOut 设置为N  直接以接口数据为准
            HashMap<String, Object> is_out = new HashMap<String, Object>();
            is_out.put("isOut", "Y");
            is_out.put("sendOrRecycle", 0);
            is_out.put("isScan", "N");
            List<AtmmoneyBagVo> outList = money_dao.quaryForDetail(is_out);
            if (outList != null && outList.size() > 0) {
                for (int k = 0; k < outList.size(); k++) {
                    AtmmoneyBagVo boxBagVo = outList.get(k);
                    boxBagVo.setIsOut("N");
                    money_dao.upDate(boxBagVo);
                }
            }
        }
        try {
            for (int i = 0; i < outData.length(); i++) {

                JSONObject goodinfo = null;

                goodinfo = outData.getJSONObject(i);

                String barcoe = goodinfo.optString("barcode");//编码
                String line = goodinfo.optString("bizinfo");//所属线路名字

                HashMap<String, Object> in_box = new HashMap<String, Object>();
                in_box.put("barcodeno", barcoe);
                List<AtmmoneyBagVo> boxBagVoList = money_dao.quaryForDetail(in_box);
                if (boxBagVoList != null && boxBagVoList.size() > 0) {
                    moneyVo = boxBagVoList.get(boxBagVoList.size() - 1);
                    HashMap<String, Object> is_scan = new HashMap<String, Object>();
                    is_scan.put("isScan", "Y");
                    List<AtmmoneyBagVo> scanList = money_dao.quaryForDetail(is_scan);
                    if (scanList != null && scanList.size() > 0) {//已经扫描过的不做处理
                        if (Regex.isTaiZipperBag(barcoe)) { // 扎带
                            moneyVo.setBagtype(5);
                            moneyVo.setIsOut("Y");
                            moneyVo.setInPda("Y");//如果一个物品多次出库 回库 则出库状态为 Y 并且在Pda 上
                            moneyVo.setBarcode(barcoe);
                            moneyVo.setClientid(clientid);
                            moneyVo.setSendOrRecycle(0);
                            money_dao.upDate(moneyVo);
                        }
                    } else {
                        if (Regex.isTaiZipperBag(barcoe)) { // 扎带
                            moneyVo.setBagtype(5);
                            moneyVo.setIsOut("Y");
                            moneyVo.setInPda("Y");//如果一个物品多次出库 回库 则出库状态为 Y 并且在Pda 上
                            moneyVo.setIsScan("N");//一个物品多次出库回库  则出库时  物品为 未扫描
                            moneyVo.setBarcode(barcoe);
                            moneyVo.setClientid(clientid);
                            moneyVo.setSendOrRecycle(0);
                            money_dao.upDate(moneyVo);
                        }
                    }

                } else {//2.不存在就创建
                    moneyVo = new AtmmoneyBagVo();
                    if (Regex.isTaiZipperBag(barcoe)) { // 扎带
                        moneyVo.setBagtype(5);
                        moneyVo.setIsOut("Y");
                        moneyVo.setInPda("Y");
                        moneyVo.setIsScan("N");
                        moneyVo.setBarcode(barcoe);
                        moneyVo.setClientid(clientid);
                        moneyVo.setSendOrRecycle(0);
                        money_dao.create(moneyVo);
                    }
                }
            }

            //入库数据只有扎带 和 Tebag  对PDA出库判断无用  所以可以不做入库处理
            /*//入库  已经入库的物品 即不在pda上 不需要出库
            JSONArray inData = jsonTotal.optJSONArray("iteminlist");
            AtmmoneyBagVo inbagVo;
            for (int i = 0; i < inData.length(); i++) {

                JSONObject goodinfo = inData.getJSONObject(i);
                String barcoe = goodinfo.optString("barcode");//编码
                String ilne = goodinfo.optString("bizinfo");//所属线路名字

                HashMap<String, Object> in_box = new HashMap<String, Object>();
                in_box.put("barcodeno", barcoe);
                List<AtmmoneyBagVo> boxBagVoList = money_dao.quaryForDetail(in_box);
                if (boxBagVoList != null && boxBagVoList.size() > 0) {
                    inbagVo = boxBagVoList.get(boxBagVoList.size() - 1);
                    if (Regex.isTaiZipperBag(barcoe)) { // 扎带
                        inbagVo.setBagtype(6);
                        inbagVo.setInPda("N");
                        inbagVo.setBarcode(barcoe);
                        inbagVo.setClientid(clientid);
                        inbagVo.setSendOrRecycle(1);
                        money_dao.upDate(inbagVo);
                    }
                } else {//2.不存在就创建
                    inbagVo = new AtmmoneyBagVo();
                    if (Regex.isTaiZipperBag(barcoe)) { // 扎带
                        inbagVo.setBagtype(6);
                        inbagVo.setInPda("N");
                        inbagVo.setBarcode(barcoe);
                        inbagVo.setClientid(clientid);
                        inbagVo.setSendOrRecycle(1);
                        money_dao.create(inbagVo);
                    }

                }
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mContext.sendBroadcast(new Intent(Config.GOODS_OUT));//出入库界面刷新
    }

    //迪堡 获取出入库
    public void getDobaoInOut() {
    }
}
