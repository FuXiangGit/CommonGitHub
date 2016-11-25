package com.xvli.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 卡钞废钞 钞箱等编码规则
 */

public class Regex {

    //卡钞编码规则
    public static boolean isKaChao(String kachao) {
        Pattern p = Pattern.compile("^(0){5}3[0-9]{4}$");
        Matcher m = p.matcher(kachao);
        return m.matches();
    }

    //废钞编码规则
    public static boolean isFeiChao(String feichao) {
        Pattern p = Pattern.compile("^(0){5}4[0-9]{4}$");
        Matcher m = p.matcher(feichao);
        return m.matches();
    }


    public static boolean isRightCode(String code) {
        Pattern p = Pattern.compile("^[0-9]{10}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //钞箱编码规则
    public static boolean isChaoBox(String feichao) {
//        Pattern p = Pattern.compile("^[0-9]{4}[1-4]{1}[0-9]{5}$");// 钞箱规则 10-49
        Pattern p = Pattern.compile("^[0-9]{4}([1-4]{1}[0-9]{1}||09)[0-9]{4}$");//钞箱 规则 08-22  改为 09-49
        Matcher m = p.matcher(feichao);
        return m.matches();
    }

    //钞袋编码规则
    public static boolean isChaoBag(String feichao) {
//        Pattern p = Pattern.compile("^[0-9]{4}[0]{1}[2]{1}[1-9]{1}[0-9]{3}$");
        Pattern p = Pattern.compile("^(0){5}2[0-9]{4}$");
        Matcher m = p.matcher(feichao);
        return m.matches();
    }


    //钥匙编码规则
    public static boolean isChaoKey(String feichao) {
        Pattern p = Pattern.compile("^[5]{1}[0-9a-zA-Z]{1}[0-9]{4}[5]{1}[3]{1}[0-9]{4}$");
        Matcher m = p.matcher(feichao);
        return m.matches();
    }

    //密码编码规则
    public static boolean isChaoPaw(String feichao) {
        Pattern p = Pattern.compile("^[6]{1}[0-9a-zA-Z]{1}[0-9]{4}[5]{1}[3]{1}[0-9]{4}$");
        Matcher m = p.matcher(feichao);
        return m.matches();
    }

    //车辆编码规则
    public static boolean isCar(String feichao) {
        Pattern p = Pattern.compile("^[a-zA-Z_0-9]{4}[5]{1}[0]{1}[0-9]{4}$");
        Matcher m = p.matcher(feichao);
        return m.matches();
    }

    //车牌号规则
    public  static boolean isPlanmu(String carNum){

        Pattern p = Pattern.compile("^[\\u4e00-\\u9fa5]{1}[a-zA-Z]{1}[a-zA-Z_0-9]{4}[a-zA-Z_0-9]$");
        Matcher m = p.matcher(carNum);
        return m.matches();
    }


    //网点规则
    public static boolean isBranch(String code){
        Pattern p = Pattern.compile("^[0-9]{4}[5]{1}[3]{1}[0-9]{4}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //通用钥匙
    public  static boolean  isCurrencyKey(String code){
        Pattern p = Pattern.compile("[7]{1}[a-zA-Z_0-9]{3}[0]{8}$");
        Matcher m = p.matcher(code);
        return  m.matches();
    }
    //迪堡-------------------------------------------------------------------

    //钞包规则   测试用
    public static  boolean isBag(String code){
        Pattern p = Pattern.compile("[0-9]{4}[0]{1}[6]{1}[0-9]{4}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //卡钞编码规则
    public static boolean isDiKaChao(String kachao) {
        Pattern p = Pattern.compile("[0-9]{4}[0]{1}[3]{1}[0-9]{4}$");
        Matcher m = p.matcher(kachao);
        return m.matches();
    }

    //废钞编码规则
    public static boolean isDiFeiChao(String feichao) {
        Pattern p = Pattern.compile("[0-9]{4}[0]{1}[4]{1}[0-9]{4}$");
        Matcher m = p.matcher(feichao);
        return m.matches();
    }

    //钞袋编码规则
    public static boolean isDiChaoBag(String feichao) {
        Pattern p = Pattern.compile("[0-9]{4}[0]{1}[2]{1}[0-9]{4}$");
        Matcher m = p.matcher(feichao);
        return m.matches();
    }

    //车辆编码规则
    public static boolean isDICar(String feichao) {
        Pattern p = Pattern.compile("[0-9]{4}[5]{1}[0]{1}[0-9]{4}$");
        Matcher m = p.matcher(feichao);
        return m.matches();
    }
    //泰国-------------------------------------------------------------------

    // ATM编码规则
    public static boolean isAtmCode(String code) {
        Pattern p = Pattern.compile("^^A[0-9]{2}[0-9]{2}(0){2}[0-9]{5}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }


    //钞箱编码规则
    public static boolean isTaiCashbox(String code) {
        Pattern p = Pattern.compile("^17[2-5]\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }


    //钞袋编码规则
    public static boolean isTaiCashbog(String code) {
        Pattern p = Pattern.compile("^17[2-5]\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //钥匙编码规则
    public static boolean isTaiKey(String code){
        Pattern p = Pattern.compile("^181\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //备用钥匙编码规则
    public static boolean isTaiKeyCopy(String code){
        Pattern p = Pattern.compile("^182\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }




    //密码编码规则
    public static boolean isTaiPassWord(String code){
        Pattern p = Pattern.compile("^181\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //工作手机编码规则
    public static boolean isTaiPhone(String code){
        Pattern p = Pattern.compile("^185\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //枪支编码规则
    public static boolean isTaiGun(String code){
        Pattern p = Pattern.compile("^191\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //弹药包编码
    public static boolean isTaiCartridgeBag(String code){
        Pattern p = Pattern.compile("^192\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //过高速贴条
    public static boolean isTaiHighSpeedBar(String code){
        Pattern p = Pattern.compile("^194\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //导航仪
    public static boolean isTaiGPS(String code){
        Pattern p = Pattern.compile("^195\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //车辆钥匙编码规则
    public static boolean isTaiCar(String code){
        Pattern p = Pattern.compile("^186\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //车辆侧门钥匙编码规则
    public static boolean isTaiCarSideDoor(String code){
        Pattern p = Pattern.compile("^186\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //车辆备用钥匙编码规则
    public static boolean isTaiCarCopy(String code){
        Pattern p = Pattern.compile("^189\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }




    //废钞箱编码规则
    public static boolean isTaiFeiChao(String code){
        Pattern p = Pattern.compile("^176\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //扎带编码规则
    public static boolean isTaiZipperBag(String code){
        Pattern p = Pattern.compile("^17[7-8]\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //Tebag编码规则
    public static boolean isTaiTeBag(String code){
        Pattern p = Pattern.compile("GF\\d{8}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }


    //车辆编码规则
    public static boolean isTaiCarUP(String code){
        Pattern p = Pattern.compile("^V\\d{11}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //维修更换钞箱 编码规则
    public static boolean isRepairBog(String code) {
        Pattern p = Pattern.compile("^171\\d{9}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }

    //员工卡号编码
    public static boolean isJobCard(String code){
        Pattern p = Pattern.compile("^[0-9]{5}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }
}
