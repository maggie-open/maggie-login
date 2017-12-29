package com.maggie.dating.common.util;

import com.maggie.dating.beans.Buyer;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtil {

    public static String getString(Object object){
        String res = null;
        if(object!=null){
            res = String.valueOf(object);
        }
        return res;
    }

    public static int getInt(Object object){
        int res = 0;
        if(object!=null){
            try {
                res = Integer.valueOf(object+"");
            }catch (Exception e){
                res = 0;
            }
        }
        return res;
    }

    public static long getLong(Object object){
        long res = 0;
        if(object!=null){
            try {
                res = Long.valueOf(object+"");
            }catch (Exception e){
                res = 0;
            }
        }
        return res;
    }

    public static double getDouble(Object object){
        double res = 0;
        if(object!=null){
            try {
                res = Double.valueOf(object+"");
            }catch (Exception e){
                res = 0;
            }
        }
        return res;
    }


    public static String dateAddOneYearToStr(Date date){
        java.text.Format formatter=new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date todayDate=new java.util.Date();
        long afterTime=(todayDate.getTime()/1000)+60*60*24*365;
        todayDate.setTime(afterTime*1000);
        String afterDate=formatter.format(todayDate);
        return afterDate;
    }

    public static long dateCalRemainSecondsTheDay(Date date){
        long remSeds = 0;
        if(date!=null){
            long daySeds = 24*60*60;
            int hour = date.getHours();
            int mins = date.getMinutes();
            int seds = date.getSeconds();
            long passSed = hour*60*60+mins*60+seds;
            remSeds = daySeds-passSed;
        }
        return remSeds;
    }

    public static String StringUtil(Object obj) {
        if (isEmpty(obj)) {
            return null;
        } else {
            return String.valueOf(obj);
        }
    }

    public static String dateToStr(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String str=sdf.format(date);
        return str;
    }

    public static String dateToStrFull(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str=sdf.format(date);
        return str;
    }

    public static Date strToDate(Object obj) {
        if (isEmpty(obj)) {
            return null;
        } else {
            String strDate =StringUtil(obj);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            ParsePosition pos = new ParsePosition(0);
            Date strtodate = formatter.parse(strDate, pos);
            return strtodate;
        }
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static boolean isEmpty(Object obj)
    {
        if (obj == null)
        {
            return true;
        }
        if ((obj instanceof List))
        {
            return ((List) obj).size() == 0;
        }
        if ((obj instanceof String))
        {
            return ((String) obj).trim().equals("");
        }
        return false;
    }

    public static boolean isNotEmpty(Object obj)
    {
        return !isEmpty(obj);
    }

    public static boolean isNull(Object obj) {
        return (null == obj) ? true : false;
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

}
