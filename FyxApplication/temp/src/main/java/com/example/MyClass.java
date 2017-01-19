package com.example;

import com.google.gson.Gson;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MyClass {

    public static void main(String[] args) {

//        getTheMonthOfTaday();
//        System.out.println(getTheMonthOfTaday());
//        initBefore();
        getMonthDate(2017,2);


    }


    /**
     * 获得某个月最大天数
     *
     * @param year 年份
     * @param month 月份 (1-12)
     * @return 某个月最大天数
     */
    public static int getMaxDayByYearMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        return calendar.getActualMaximum(Calendar.DATE);
    }
    /**
     * 获取一月的日期（几月几号）
     */
    public static  List<String> getMonthDate(int year,int month) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        String dateString = null;
        List<String> dates = new ArrayList<>();
        int operateMonth = month-1;//这里要减一
        int daysByYearMonth = getMaxDayByYearMonth(year,month);//这个是传入的月份最大的天数
        System.out.println(daysByYearMonth);
        for (int i = 1; i <= daysByYearMonth; i++) {//这个样子循环才是从一号到最后一号
            calendar.set(year, operateMonth, i);
            dateString = formatter.format(calendar.getTime());
            dates.add(dateString);
            System.out.println(dateString);
        }
        return dates;
    }



    /**
     * 获取本月是第几月份
     * @return
     */
    public static int getTheMonthOfTaday() {
        Calendar now= Calendar.getInstance();
        return now.get(Calendar.MONTH)+1;
    }
    public static int getTheMonthOfTaday1() {
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH) + 1;
        return month;
    }

    public static int getMaxWeekNumOfYear1(int year) {
        Calendar c = new GregorianCalendar();
        c.set(year, 11, 31);
        System.out.println(dateToStrLong(c.getTime()));
        return getWeekOfYear(c.getTime());
    }
    public static int getMaxWeekNumOfYear(int year){
        Calendar c = new GregorianCalendar();
        c.set(year, 11, 31);
        int maxWeek = getWeekOfYear(c.getTime());
        c.set(year, 11, 30);
        if(maxWeek<getWeekOfYear(c.getTime())){
            maxWeek = getWeekOfYear(c.getTime());
        }
        c.set(year, 11, 29);
        if(maxWeek<getWeekOfYear(c.getTime())){
            maxWeek = getWeekOfYear(c.getTime());
        }
        c.set(year, 11, 28);
        if(maxWeek<getWeekOfYear(c.getTime())){
            maxWeek = getWeekOfYear(c.getTime());
        }
        c.set(year, 11, 27);
        if(maxWeek<getWeekOfYear(c.getTime())){
            maxWeek = getWeekOfYear(c.getTime());
        }
        c.set(year, 11, 26);
        if(maxWeek<getWeekOfYear(c.getTime())){
            maxWeek = getWeekOfYear(c.getTime());
        }
        c.set(year, 11, 25);
        if(maxWeek<getWeekOfYear(c.getTime())){
            maxWeek = getWeekOfYear(c.getTime());
        }
        return maxWeek;
    }
    public static int getWeekOfYear(Date date) {
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(date);
        return g.get(Calendar.WEEK_OF_YEAR);//获得周数
    }
    public static String dateToStrLong(Date dateDate) {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }
    private static void initBefore() {
        //日期处理
        System.out.println(getMaxWeekNumOfYear(2017));
        //        creatFile();
//        String aaa = "/dddd/fffff/eef/20161128_15:11:11.jpg";
//        System.out.println(aaa);
//            aaa = aaa.substring(aaa.lastIndexOf('/')+1,aaa.lastIndexOf("_"));
//        System.out.println(aaa);
//        String epirationtime = "20161116 12:30:30";
        String epirationtime = "2016-11-28 12:30:30";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 小写的mm表示的是分钟
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 小写的mm表示的是分钟
        Date dateTime = null;
        try {
            dateTime = sdf.parse(epirationtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int days = daysOfTwo(dateTime, new Date());
        System.out.println(days);
    }

    private static void creatFile() {
        System.out.println("Hello World!");
        String filePath = "d:/s/e/a.rar";
        File file = new File(filePath);
//        mkDir(file);

        try {
            if(!file.exists()) {
                System.out.println("not have create");
                FileUtils.forceMkdirParent(file);
            }else{
                System.out.println("have hahaha");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String filename = FilenameUtils.getName(file.getAbsolutePath());//取出文件名字带后缀比如aaa.jpg或者bbb.zip
        String fileParent = FilenameUtils.getFullPathNoEndSeparator(file.getAbsolutePath());
        System.out.println(filename+"文件路径"+fileParent);
    }

    public static int daysOfTwo(Date fDate, Date oDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        System.out.println("day1"+day1+",day2"+day2);
        return day2 - day1;
    }


    public static void mkDir(File file) {
            if (file.getParentFile().exists()) {
                file.mkdir();
            } else {
                mkDir(file.getParentFile());
                file.mkdir();
            }
        }

    public static String getFileNameWithSuffix(String filePath){
        String fileName=null;
        return fileName;
    }


    }
