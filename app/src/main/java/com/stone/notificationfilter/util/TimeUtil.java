package com.stone.notificationfilter.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {


    /**
     * 时间戳转为时间(年月日，时分秒)
     *
     * @param cc_time 时间戳
     * @return
     */
    public static String getStrTime(long cc_time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");//12小时制
        Date date = new Date();
        date.setTime(cc_time);
        String time = simpleDateFormat.format(date);
        return time;

    }
    /**
     * 根据时间戳（13位数，10位数需要乘以1000）获取当前月份
     * @param timeStamp 时间戳
     * @return month
     */
    public static int getMonth(long timeStamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeStamp);
        //Time.MONTH及Calendar.MONTH 默认的月份为  0-11
        return c.get(Calendar.MONTH)+1;
    }

    /**
     * 根据时间戳（13位数，10位数需要乘以1000）获取当前月份
     * @param timeStamp 时间戳
     * @return month
     */
    public static int getDay(long timeStamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeStamp);
        //Time.MONTH及Calendar.MONTH 默认的月份为  0-11
        return c.get(Calendar.DAY_OF_YEAR)+1;
    }




}
