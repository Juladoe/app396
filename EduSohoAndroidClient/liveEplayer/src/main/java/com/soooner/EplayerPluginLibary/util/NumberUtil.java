package com.soooner.EplayerPluginLibary.util;

import android.util.Log;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: SlothMonkey
 * Date: 12-4-28
 * Time: 下午2:39
 * To change this template use File | Settings | File Templates.
 */
public class NumberUtil {

    private static final String TAG = NumberUtil.class.getSimpleName();

    public static BigDecimal long2Decimal(long money) {
        return  BigDecimal.valueOf(money, 2);
    }

    //有两位小数的钱转换为以分为单位的钱
    public static Long decimal2Long(BigDecimal money) {
        return (money.multiply(BigDecimal.valueOf(100))).longValue();
    }

    //将分转换成元的字符串
    public static String getMoneyYuanStr(long moneyFen){
        return long2Decimal(moneyFen).toString();
    }

    //保留n位小数，四舍五入，返回Folat类型对象
    public static Float getFloatFromFloatRoundHalfUp(float sourceNum,int scale) {
        BigDecimal bigDecimal = new BigDecimal(sourceNum);
        return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    //float 转换至 int 小数四舍五入
    public static int convertFloatToInt(float sourceNum) {
        BigDecimal bigDecimal = new BigDecimal(sourceNum);
        return bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    public static int parseInt(String str, int def) {

        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            Log.e(TAG, "String to Integer Error! Use def value! " + def);
        }
        return def;
    }

    public static long parseLong(String str, long def) {

        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            Log.e(TAG, "String to Long Error! Use def value!");
        }
        return def;
    }

//    /**
//     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
//     */
//    public static int dip2px(float dpValue) {
//        final float scale = DeviceUtil.getDensity();
//        return (int) (dpValue * scale + 0.5f);
//    }
//
//    /**
//     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
//     */
//    public static int px2dip(float pxValue) {
//        final float scale = DeviceUtil.getDensity();
//        return (int) (pxValue / scale + 0.5f);
//    }






    public static void main(String[] args) {

        System.out.println(getMoneyYuanStr(123456789));
        System.out.println(getMoneyYuanStr(0));
        System.out.println(getMoneyYuanStr(10));
        System.out.println(getMoneyYuanStr(000));
        System.out.println(getMoneyYuanStr(123));
        System.out.println(getMoneyYuanStr(1234));
        System.out.println(getMoneyYuanStr(1));

        System.out.println(parseInt("", -1));
        System.out.println(parseInt("", 1));
        System.out.println(parseInt("", 0));
        System.out.println(parseInt("error", 0));
        System.out.println(parseInt("error", -1));
        System.out.println(parseInt("error", 1));
        System.out.println(parseInt("111", 1));
        System.out.println(parseInt("111", 1));
        System.out.println(parseInt("111", 1));




    }


}
