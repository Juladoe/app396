package com.soooner.EplayerPluginLibary.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: daocren
 * Date: 11-5-30
 * Time: 下午5:27
 */
public class DateUtil {

    private static final DateFormat FORMATOR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat FORMATOR_HM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final DateFormat FORDATE_HOUR = new SimpleDateFormat("MM-dd HH:mm");
    private static final DateFormat FORMATOR_YMD = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat FORMATOR_SIMPLE = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final DateFormat FORMATOR_HMS = new SimpleDateFormat("HH:mm:ss");
    private static final DateFormat FORMATOR_HOUR_MINUTE = new SimpleDateFormat("HH:mm");
    private static final DateFormat FORMATOR_MONTH_DATE = new SimpleDateFormat("MM月dd日");
    private static final DateFormat FORMATOR_E_CN = new SimpleDateFormat("E", Locale.CHINA);

    public static Date getNow(){
        return Calendar.getInstance().getTime();
    }

    public static long getTimeInMillisByDate(Date date){
        Calendar cal= Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis();
    }



    public static Calendar getInstance(){
        return Calendar.getInstance();
    }

    public static LinkedHashMap<Long, String> getLastDays( int days ){
        LinkedHashMap<Long, String> dateMap = new LinkedHashMap<Long, String>( days );
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");//设置日期格式

        Date date = new Date();
        long daylong = 1000 * 3600 * 24;

        dateMap.put( new Long( date.getTime() ), df.format( date ) );
        for( int i=1;i<days;i++){
            date = new Date( date.getTime() - daylong );
//            System.out.println( df.format( date ));// new Date()为获取当前系统时间
            dateMap.put( new Long( date.getTime() ), df.format( date ) );
        }
        return dateMap;
    }

    /**
     * 根据 yyyy-MM-dd HH:mm:ss 格式获取日期字符串
     *
     * @param date
     * @return String
     */
    public static String getString(Date date) {
        return FORMATOR.format(date);
    }

    /**
     *
     * @param date date
     * @return format yyyy-MM-dd
     */
    public static String getStringYMD(Date date) {
        return FORMATOR_YMD.format(date);
    }

    public static String getStringHMS(Date date) {
        return FORMATOR_HMS.format(date);
    }

    /**
     * 根据 yyyyMMddHHmmss 格式获取日期字符串
     *
     * @param date
     * @return String
     */
    public static String getSimpleString(Date date) {
        return FORMATOR_SIMPLE.format(date);
    }

    /**
     * 根据long值获取时间字符串，long值中不包含毫秒信息
     *
     * @param time
     * @return
     */
    public static String getString(long time) {
        return FORMATOR.format(new Date(time * 1000));
    }

    public static String getHourMinute(long time) {
        return FORMATOR_HOUR_MINUTE.format(new Date(time));
    }
    public static String getYearhourminute(long time) {
        return FORMATOR_HM.format(new Date(time));
    }

    public static String getHms(long time) {
        return FORMATOR_HMS.format(new Date(time));
    }

    public static String getHmbyMilliseconds(long time) {

        return FORMATOR_HMS.format(time);

    }

    public static Date convertHMS2Date(String Hms) {
        try {
            return FORMATOR_HMS.parse(Hms);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getDateHourMinute(long time) {
        return FORDATE_HOUR.format(new Date(time));
    }

    public static String getYearMonthDay(long time) {
        return FORMATOR_YMD.format(new Date(time));
    }

    /**
     * 根据字符串生成日期
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss 格式的字符串
     * @return Date
     */
    public static Date getDate(String dateStr) {
        try {
            return FORMATOR.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取系统时间  返回星期几
     */
    public static String getWeekDay() {
        SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E ");

        String[] week = dateformat2.format(new Date()).split(" ");

        return week[2];
    }

    public static String getStringDateAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sdf.format(new Date() );
    }

    /**
     * @param created
     * @param expire
     * @return
     */
    public static boolean isExpired(String created, long expire) {
        if (expire != 0) {
            long createTime = 0;
            try {
                createTime = Long.parseLong(created);
            } catch (NumberFormatException e) {

            }
            long expireDate = new Date(createTime).getTime() + expire * 24 * 60 * 60 * 1000;
            return expireDate < new Date().getTime();
        }
        return false;
    }

    public static String getTimeInEpgFormat( long time ){
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.setTime( time );
        calendar.setTime( date );
        String timeStr = "";
        timeStr = calendar.get( Calendar.HOUR )<10?"0"+calendar.get( Calendar.HOUR ):""+calendar.get( Calendar.HOUR );
        timeStr = timeStr + ":"+ ( calendar.get( Calendar.MINUTE )<10?"0"+calendar.get( Calendar.MINUTE ):""+calendar.get( Calendar.MINUTE ) );
        timeStr = timeStr + ":"+ ( calendar.get( Calendar.SECOND )<10?"0"+calendar.get( Calendar.SECOND ):""+calendar.get( Calendar.SECOND ) );
        return timeStr;
    }

    // 设置视频最大时间
    public static String getMediaPlayerDuration(int i) {
        String str = "";
        int hour;
        int minute;
        int second;

        hour = (i / 1000) / 3600;
        minute = ((i / 1000) % 3600) / 60;
        second = ((i / 1000) % 3600) % 60;
        String strhour, strminute, strsecond;
        if (hour < 10) {
            strhour = "0" + hour;
        } else {
            strhour = "" + hour;
        }
        if (minute < 10) {
            strminute = "0" + minute;
        } else {
            strminute = "" + minute;
        }
        if (second < 10) {
            strsecond = "0" + second;
        } else {
            strsecond = "" + second;
        }
        str = strhour + ":" + strminute + ":" + strsecond;
        return str;
    }

    //
    public static String getHMS(Date date) {
        Calendar calendar = getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        StringBuilder timeBuild = new StringBuilder();
        timeBuild.append(hour < 10 ? ("0" + String.valueOf(hour)) : String.valueOf(hour));
        timeBuild.append(":");
        timeBuild.append(minute < 10 ? ("0" + String.valueOf(minute)) : String.valueOf(minute));
        timeBuild.append(":");
        timeBuild.append(second < 10 ? ("0" + String.valueOf(second)) : String.valueOf(second));

        return timeBuild.toString();
    }


    public static int getMillisStartWithToday() {

        Date date = new Date();
        return (date.getHours() * 3600 + date.getMinutes() * 60 + date.getSeconds()) * 1000;
    }

    public static long getTimeMillis() {
        Calendar calendar = getInstance();
        return calendar.getTimeInMillis();
    }

    public static Date getDateFromTodayBegin(long millisPlus){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.setTimeInMillis(calendar.getTimeInMillis() + millisPlus);
        return calendar.getTime();
    }


    public static String getSimpleDateFromTodayBegin(long millisPlus) {



        getDateFromTodayBegin(millisPlus);

        return DateUtil.getSimpleString(getDateFromTodayBegin(millisPlus));
    }

    public static Map<Integer,String> getDateMapForWeekBeginToday(){

        Map<Integer,String> dateMap = new HashMap<Integer, String>();
        Calendar calendar = Calendar.getInstance();
        dateMap.put(calendar.get(Calendar.DAY_OF_WEEK ) - 1, getStringYMD(calendar.getTime()));
        Log.d( "DateUtil.java ", "calendar.get(Calendar.DAY_OF_WEEK ) -1  == " + (calendar.get(Calendar.DAY_OF_WEEK ) -1) + "  " +  getStringYMD(calendar.getTime()));
        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            dateMap.put(calendar.get(Calendar.DAY_OF_WEEK ) - 1, getStringYMD(calendar.getTime()));
            Log.d( "DateUtil.java ", "calendar.get(Calendar.DAY_OF_WEEK ) - 1  == " + (calendar.get(Calendar.DAY_OF_WEEK ) -1) + "  " +  getStringYMD(calendar.getTime()));
        }
        return dateMap;
    }

    public static Date getDate(Date date,int field,int offset){

        if (date == null) {
            return null;
        }
        Calendar calendar = getInstance();
        calendar.setTime(date);
        calendar.add(field, offset);
        return calendar.getTime();
    }

    //获得当前日期的中文星期内容（比如：星期一），目前改为 周X 的形式
    public static String getWeekCn(Date date) {
        //加replace()，为了三星手机适配
        return FORMATOR_E_CN.format(date).replace("星期","周");
    }

    //判断两个日期是否在同一天
    public static boolean isSameDay(Date sDate,Date tDate){
        if (sDate == null || tDate == null) {
            return false;
        }
        Calendar sCalendar = getInstance();
        sCalendar.setTime(sDate);
        Calendar tCalendar = getInstance();
        tCalendar.setTime(tDate);
        return sCalendar.get(Calendar.YEAR) == tCalendar.get(Calendar.YEAR)
                && sCalendar.get(Calendar.MONTH) == tCalendar.get(Calendar.MONTH)
                && sCalendar.get(Calendar.DAY_OF_MONTH) == tCalendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断两个日期之间相差的天数，返回相差天数的绝对值
     * @param sDate
     * @param tDate
     * @return int Abs value
     */
    public static int getDayOffset(Date sDate,Date tDate){
        if (sDate == null || tDate == null) {
            return -1;
        }
        Calendar sCalendar = getInstance();
        sCalendar.setTime(sDate);
        Calendar tCalendar = getInstance();
        tCalendar.setTime(tDate);
        return Math.abs(sCalendar.get(Calendar.DAY_OF_YEAR) - tCalendar.get(Calendar.DAY_OF_YEAR));
    }

    public static int getHmsToMilliSecond(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return  (hour * 3600 + minute * 60 + second) * 1000;
    }

    /**
     * 将毫秒数转换成00:00:00格式，毫秒数从0开始，不是Date级别的毫秒数
     * @param milliSecond
     * @return
     */
    public static String getHmsFromMilliSecond(long milliSecond) {

        long second = milliSecond / 1000;
        long minute = second / 60;

        String showSecond = String.valueOf(second % 60).length() == 1 ? "0" + second % 60 : String.valueOf(second % 60);
        String showMinute = String.valueOf(minute % 60).length() == 1 ? "0" + minute % 60 : String.valueOf(minute % 60);
        String showHour = String.valueOf(minute / 60).length() == 1 ? "0" + minute / 60 : String.valueOf(minute / 60);

        return showHour + ":" + showMinute + ":" + showSecond;

    }





    public static void main(String[] args) {
//        Map<Integer, String> dateMapForWeekBeginToday = getDateMapForWeekBeginToday();
//        for (Integer integer : dateMapForWeekBeginToday.keySet()) {
//            System.out.println(integer + "--------"+dateMapForWeekBeginToday.get(integer));
//        }
//        Calendar calendar = Calendar.getInstance();
//        System.out.println( DateUtil.getStringYMD( new Date() ));

//        System.out.println( getMediaPlayerDuration( (int)new Date().getTime() ) );
//        System.out.println( new Date(1367023440000l) );

//        Date date = getDate(new Date(), Calendar.DAY_OF_MONTH, 3);
//        System.out.println("----:"+FORMATOR_SIMPLE.format(date));
//        System.out.println("----:"+FORMATOR_E_CN.format(date));

//        System.out.println(getDayOffset(DateUtil.getDate("2013-06-01 00:00:00"), DateUtil.getDate("2013-06-01 12:00:00")));
//        System.out.println(getDayOffset(DateUtil.getDate("2013-06-01 00:00:00"), DateUtil.getDate("2013-06-21 00:00:00")));
//        System.out.println(getDayOffset(DateUtil.getDate("2013-06-21 00:00:00"), DateUtil.getDate("2013-06-01 00:00:00")));
//        System.out.println(getDayOffset(DateUtil.getDate("2013-06-01 00:00:00"), DateUtil.getDate("2013-04-01 00:00:00")));

//        System.out.println(getHmsFromMilliSecond(0 * 1000));
//        System.out.println(getHmsFromMilliSecond(10 * 1000));
//        System.out.println(getHmsFromMilliSecond(60 * 1000));
//        System.out.println(getHmsFromMilliSecond(100 * 1000));
//        System.out.println(getHmsFromMilliSecond(3600 * 1000));
//        System.out.println(getHmsFromMilliSecond(3706 * 1000));

//        try {
//            DateFormat FORMATOR = new SimpleDateFormat("yyyyMMddHHmmss");
//            Date date= FORMATOR.parse("20140811180000");
//            Calendar cal=Calendar.getInstance();
//            cal.setTime(date);
//           long start= cal.getTimeInMillis();
//
//           long current= Calendar.getInstance().getTimeInMillis();
//
//           System.out.print(start+";"+current);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String temp=" http://liveat.ott.soooner.com/yzbAC.m3u8?uuid=5B63686E5D445830303030303038377C3130303033307C7C7C7C687474707C74735B2F63686E5D5B74735D315B2F74735D5B7375735D315B2F7375735DVSDNSOOONERCOM00&start=20140811160800&end=20140811163800&stbid=0E:76:E9:D5:57:8A&timestamp=1407745699586&sign=291E8DF78500F1E173C9D35226AF6BD7&channelId=106";
//        String temp2=temp.substring(temp.lastIndexOf("&start=")+7);
//        String temp3= temp2.substring(0,temp2.indexOf("&"));
//        System.out.print(temp3+";"+temp);
        //20140811160800

        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.MINUTE, -5);
        System.out.print( getStringHMS(cal.getTime())+"---");
    }



}
