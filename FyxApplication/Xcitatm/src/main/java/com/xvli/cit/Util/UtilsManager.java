package com.xvli.cit.Util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;


import com.xvli.cit.CitApplication;
import com.xvli.cit.vo.LoginVo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 */
public class UtilsManager {


    public static boolean isSdCard() {
        boolean exist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        return exist;
    }

    public static String getPicturePath() {
        String sdDir = null;
        if (isSdCard()) {
            sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            sdDir = Environment.getRootDirectory().getAbsolutePath();
        }
        return sdDir + "/siginRepair/picture";
    }

    public static String getNetworkPicture() {
        String sdDir = null;
        if (isSdCard()) {
            sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            sdDir = Environment.getRootDirectory().getAbsolutePath();
        }
        return sdDir + "/takePhoto/picture";
    }

    /**
     * 网络是否可用
     *
     * @param ctx
     * @return
     */
    public static boolean isNetAvailable(Context ctx) {
        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].isAvailable()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 当前是否是wifi状态
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
    public static boolean isSDCardAvailable() {
        String mounted = "mounted";// 正常
        String temp = Environment.getExternalStorageState();
        if (temp.equals(mounted)) {
            return true;
        } else {
            return false;
        }
    }// end isSDCardAvailable

    private static float scale = -1;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        if (scale == -1) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        if (scale == -1) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Toast显示
     */
    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 判断email是否合法
     */
    public static boolean checkLoginEmail(String loginStr) {
        String regEx = "^([a-z0-9_A-Z]+[-|\\.]?)+[a-z0-9_A-Z]@([a-z0-9_A-Z]+(-[a-z0-9_A-Z]+)?\\.)+[a-zA-Z_]{2,}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(loginStr);
        return m.matches();// boolean
    }

    /**
     * 判断电话号码是否合法
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        // 区号+座机号码+分机号码：regexp="^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$"
        // 手机(中国移动手机号码)：regexp="^((\(\d{3}\))|(\d{3}\-))?13[456789]\d{8}|15[89]\d{8}"

        // 所有手机号码：regexp="^((\(\d{3}\))|(\d{3}\-))?13[0-9]\d{8}|15[89]\d{8}"(新添加了158,159两个号段)
        String expression = "^(((13)(\\d{9}))|((14)(0|5|7)(\\d{8}))|((15)(0|1|2|3|5|6|7|8|9)(\\d{8}))|((18)(0|2|3|5|6|7|8|9)(\\d{8})))$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {// || matcher2.matches()
            isValid = true;
        }
        return isValid;
    }

    /**
     * 格式化日期
     */
    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String str_date = formatter.format(date);
        return str_date;
    }

    /**
     * 格式化日期
     */
    public static String formatDateToHHmm(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmm");
        String str_date = formatter.format(date);
        return str_date;
    }

    /**
     * 格式化日期
     */
    public static String formatDateToHHmm1(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String str_date = formatter.format(date);
        return str_date;
    }

    /**
     * 格式化日期
     */
    public static String formatToymdhm(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        String str_date = formatter.format(date);
        return str_date;
    }
    /**
     * 格式化日期
     */
    public static String formatToymdhm1(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str_date = formatter.format(date);
        return str_date;
    }
    /**
     * 格式化数字，两位 1-->01
     */
    public static String formatInt(int value) {
        String format = "%02d";
//        String format = "%.2f";//保留两位小数
        return String.format(format, value);
    }

    /**
     * string转int
     */
    public static int string2Int(String value) {
        return Integer.valueOf(value);
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, float pixels) {
        if (bitmap != null) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);

            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
        return null;
    }

    /**
     * 生日格式转化2014-10-24---->20141024
     */
    public static String convert_birth(String value) {
        String[] result = value.split("\\-");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(result[i]);
        }
        return sb.toString();
    }

    public static boolean isServiceRunning(String serviceClassName){
        final ActivityManager activityManager = (ActivityManager) CitApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
     }

    /**
     *  判断某个字符串是否存在于数组中
     *  @param stringArray 原数组
     *  @param source 查找的字符串
     *  @return 是否找到
     */
    public static boolean contains(String[] stringArray, String source) {
        // 转换为list
        List<String> tempList = Arrays.asList(stringArray);

        // 利用list的包含方法,进行判断
        if(tempList.contains(source))
        {
            return true;
        } else {
            return false;
        }
    }
    //获取Clientid
    public static String getClientid (List<LoginVo> users){
        String clientId = "";
        if (users != null && users.size()>0){
            clientId = users.get(0).getClientid();
        }
        return clientId;
    }

    //获取指定路径下的图片路径：/storage/emulated/0/takePhoto/picture
    public static void deletePhoto(int saveDay,int witch){
        if (witch == 1) { //删除图片
            File file = new File(getNetworkPicture());
            if (file.exists()) {
                try {
                    showAllFiles(file, saveDay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {//删除签名
            File file = new File(getPicturePath());
            if (file.exists()) {
                try {
                    showAllFiles(file, saveDay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 递归调用
     *
     * @param dir
     * @throws Exception
     */
    final static void showAllFiles(File dir,int saveDay) throws Exception {
        File[] fs = dir.listFiles();
        for (int i = 0; i < fs.length; i++) {
//            PDALogger.d( fs[i].getAbsolutePath());
            String picName = fs[i].getAbsolutePath();
            picName = picName.substring(0, picName.lastIndexOf('_'));
            picName = picName.substring(picName.lastIndexOf('_') + 1);
//            PDALogger.d("结果 = " + picName);
            if (isDate(picName)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 小写的mm表示的是分钟
                Date datepic = sdf.parse(picName);
                int days = daysOfTwo(datepic, new Date());
//                PDALogger.d("天数  = " + days);
                if(days > saveDay){
                    // 删除sd卡下对应的图片
                    File file = new File(fs[i].getAbsolutePath());

//                    PDALogger.d( "picName--->"+fs[i].getAbsolutePath());
                    if (file.exists()) {
                        boolean delete = file.delete();
                        if (delete) {
//                            PDALogger.d( "删除成功");
                        } else {
//                            PDALogger.d( "删除失败");
                        }
                    }
                }

            }
            if (fs[i].isDirectory()) {
                try {
                    showAllFiles(fs[i],saveDay);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 是否符合闰年日期格式：YYMMDD
     *
     * @return
     */
    public static boolean isDate(String datetime) {
        Pattern p = Pattern
                .compile("^(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)$");
        Matcher m = p.matcher(datetime);
        return m.matches();
    }
    /**
     * 相差多少天，一天就是一天
     * @param fDate
     * @param oDate
     * @return
     */
    public static int daysOfTwo(Date fDate, Date oDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        return day2 - day1;

    }
    //获取图片
    public static  Bitmap getDiskBitmap(String pathString)
    {
        Bitmap bitmap = null;
        try
        {
            File file = new File(pathString);
            if(file.exists())
            {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e)
        {
            // TODO: handle exception
        }


        return bitmap;
    }

    public static String gpsData(String gisx ,String gisy ,String gisz){
        String gps = gisx+","+gisy+","+gisz ;

        return  gps ;

    }

    //获取栈顶Activity及其所属进程
    public static String getTopActivityNameAndProcessName(Context context){
        String processName=null;
        String topActivityName=null;
        ActivityManager activityManager =
                (ActivityManager)(context.getSystemService(Context.ACTIVITY_SERVICE )) ;
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1) ;
        if(runningTaskInfos != null){
            ComponentName f=runningTaskInfos.get(0).topActivity;
            String topActivityClassName=f.getClassName();
            String temp[]=topActivityClassName.split("\\.");
            //栈顶Activity的名称
            topActivityName=temp[temp.length-1];
            int index=topActivityClassName.lastIndexOf(".");
            //栈顶Activity所属进程的名称
            processName=topActivityClassName.substring(0, index);
            PDALogger.d("---->"+"---->topActivityName="+topActivityName + ",processName=" + processName);

        }
        return topActivityName+","+processName;
    }


}
