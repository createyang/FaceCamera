package com.example.administrator.attendanceinputv3.utils;

import android.text.Editable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字符串工具类
 */
public class StringUtils {

    private static StringUtils stringUtils;
    public final static String UTF_8 = "utf-8";

    //类的初始化，使用单例模式
    public static StringUtils getInstance() {
        if (stringUtils == null) {
            synchronized (StringUtils.class) {
                if (stringUtils == null) {
                    stringUtils = new StringUtils();
                }
            }
        }
        return stringUtils;
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 计算两个时间的差值
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param format    表现形式
     * @param str       返回类型
     * @return
     */
    public static Long dateDiff(String startTime, String endTime, String format, String str) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        // 获得两个时间的毫秒时间差异
        try {
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            hour = diff % nd / nh + day * 24;// 计算差多少小时
            min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
            sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            if (str.equalsIgnoreCase("d")) {
                return day;
            } else if (str.equalsIgnoreCase("h")) {
                return hour;
            } else if (str.equals("m")) {
                return min;
            } else if (str.equalsIgnoreCase("s")) {
                return sec;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (str.equalsIgnoreCase("d")) {
            return day;
        } else if (str.equalsIgnoreCase("h")) {
            return hour;
        } else if (str.equals("m")) {
            return min;
        } else if (str.equalsIgnoreCase("s")) {
            return sec;
        } else {
            return hour;
        }

    }

    /**
     * 获取指定日期是星期几
     * 参数为null时表示获取当前日期是星期几
     *
     * @param dateValue
     * @return
     */
    public static String getWeekOfDate(String dateValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        Date date = null;
        try {
            date = sdf.parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekOfDays[w];
    }

    /**
     * 获取两个List的不同元素
     *
     * @param list1
     * @param list2
     * @return
     */
    public static List<String> getDiffrent(List<String> list1, List<String> list2) {
        List<String> diff = new ArrayList<>();
        List<String> maxList = list1;
        List<String> minList = list2;
        if (list2.size() > list1.size()) {
            maxList = list2;
            minList = list1;
        }

        // 将List中的数据存到Map中
        Map<String, Integer> maxMap = new HashMap<>(maxList.size());
        for (String string : maxList) {
            maxMap.put(string, 1);
        }

        // max:1,2,3,4,5
        // min:2,4,6,8,10

        // 循环minList中的值，标记 maxMap中 相同的 数据2
        for (String string : minList) {
            // 相同的
            if (maxMap.get(string) != null) {
                maxMap.put(string, 2);
                continue;
            }
            // 不相等的
            diff.add(string);
        }

        // 循环maxMap
        for (Map.Entry<String, Integer> entry : maxMap.entrySet()) {
            if (entry.getValue() == 1) {
                diff.add(entry.getKey());
            }
        }

        return diff;
    }

    /**
     * 输入内容小数点的控制
     *
     * @param edt
     */
    public static void edit(Editable edt, int maxLength) {
        {
            String temp = edt.toString();
            int posDot = temp.indexOf(".");
            if (posDot <= 0) return;
            if (temp.length() - posDot - 1 > maxLength) {
                edt.delete(posDot + (maxLength+1), posDot + (maxLength+2));
            }
        }
    }



    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    /**
     * 日期 yyyy-hh-mm hh:mm:ss格式转化为yyyy-hh-mm
     *
     * @param value
     * @return
     */
    public String dateShowValue(String value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1;
        try {
            d1 = new SimpleDateFormat("yyyy-MM-dd").parse(value);
            String d2 = format.format(d1);
            return d2;
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
    }

    /**
     * 日期 20160226格式转化为yyyy.hh.mm
     *
     * @param value
     * @return
     */
    public String dateToValue(String value) {
        try {
            String time;
            SimpleDateFormat formatter1=new SimpleDateFormat("yyyy.HH.dd");
            SimpleDateFormat formatter2=new SimpleDateFormat("yyyyHHdd");
            time=formatter1.format(formatter2.parse(value));
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * double类型的数据去科学计数法
     *
     * @param value
     * @return
     */
    public String doubleShowValue(double value) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(2);
        return df.format(value);
    }

    /**
     * 根据不同的时差返回不同的时间结果
     *
     * @return
     */
    public String getInterval(String createtime) {
        // 定义最终返回的结果字符串。
        try {
            String interval = null;

            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition pos = new ParsePosition(0);
            Date createAt = (Date) sd.parse(createtime, pos);
            int creatYear = createAt.getYear();
            int currentYear = new Date().getYear();
            long millisecond = new Date().getTime() - createAt.getTime();

            long second = millisecond / 1000;
            if (second <= 0) {
                second = 0;
            }
            if (second == 0) {
                interval = "刚刚";
            } else if (second >= 60 && second < 60 * 60 * 24) {//大于1分钟 小于24小时
                interval = getFormatTime(createAt, "HH:mm"); //时间
            } else if (second >= 60 * 60 * 24 && second < 60 * 60 * 24 * 365) {//大于1D 小于一年
                //没大于365天，但是过了一年
                if(creatYear<currentYear){
                    interval = getFormatTime(createAt, "yyyy-MM-dd"); //月日
                }else{
                    interval = getFormatTime(createAt, "MM-dd"); //月日
                }

            } else if (second >= 60 * 60 * 24 * 365) {//大于365天
                interval = getFormatTime(createAt, "yyyy-MM-dd");
            } else {
                interval = "0";

            }
            return interval;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    private String getFormatTime(Date date, String express){
        SimpleDateFormat sdf = new SimpleDateFormat(express);
        return  sdf.format(date);
    }
}
