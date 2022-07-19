package com.dommy.music.util;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用工具类
 */
public class CommonUtil {

    /**
     * 判断是否为空
     *
     * @param str 字符串
     * @return boolean 空/非空
     */
    public static boolean isNull(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为空
     *
     * @param editText 输入框
     * @return boolean 空/非空
     */
    public static boolean isNull(EditText editText) {
        if (editText == null || editText.getText().toString().trim().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断一组选框是否为空
     *
     * @param checkBoxList
     * @return boolean 空/非空
     */
    public static boolean isNull(List<CheckBox> checkBoxList) {
        if (checkBoxList == null || checkBoxList.size() == 0) {
            return true;
        }
        for (CheckBox chb : checkBoxList) {
            if (chb.isChecked()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取连接串
     *
     * @param set 数据集
     * @param tag 分隔符
     * @return 拼接串
     */
    public static String getJoinStr(Set<String> set, String tag) {
        StringBuffer sb = new StringBuffer();
        if (set != null) {
            for (String str : set) {
                if (sb.length() > 0) {
                    sb.append(tag);
                }
                sb.append(str);
            }

        }
        return sb.toString();
    }

    /**
     * 获取非空字符串
     *
     * @param str 字符串
     * @return String 非空串
     */
    public static String getNotNullString(String str) {
        return str == null ? "" : str;
    }

    /**
     * 获取标红后的字符串
     *
     * @param str 原字符串
     * @return String 带html标签的字符串
     */
    public static String getRed(String str) {
        return str == null ? "" : str.replace("<em>", "<font color='#FF3333'>").replace("</em>", "</font>");
    }

    /**
     * 获取不带标红的字符串
     *
     * @param str 原字符串
     * @return String 不带html标签的字符串
     */
    public static String getUnRed(String str) {
        return str == null ? "" : str.replace("<em>", "").replace("</em>", "");
    }

    /**
     * 获取不带&、^ 符号的字符串
     *
     * @param str 原字符串
     * @return String 不带特殊符号的字符串
     */
    public static String getNoAnd(String str) {
        return str == null ? "" : str.replace("&", " ").replace("＆", " ").replace("^", " ").replace("＾", " ");
    }

    /**
     * 取消checkbox选中状态
     *
     * @param checkBoxList 复选框列表
     */
    public static void unCheck(List<CheckBox> checkBoxList) {
        for (CheckBox chb : checkBoxList) {
            chb.setChecked(false);
        }
    }

    /**
     * 选中checkbox
     *
     * @param checkBoxList 复选框列表
     */
    public static void checkOne(List<CheckBox> checkBoxList, String tag) {
        unCheck(checkBoxList);
        for (CheckBox chb : checkBoxList) {
            if (chb.getTag() == null) {
                continue;
            }
            if (chb.getTag().toString().equals(tag)) {
                chb.setChecked(true);
            }
        }
    }

    /**
     * 选中多个checkbox
     *
     * @param checkBoxList
     * @param values       值
     */
    public static void checkMany(List<CheckBox> checkBoxList, String values) {
        if (values == null || values.length() == 0) {
            return;
        }
        unCheck(checkBoxList);
        String[] strArray = values.split(",");
        for (CheckBox chb : checkBoxList) {
            if (chb.getTag() == null) {
                continue;
            }
            String tag = chb.getTag().toString();
            if (isContains(strArray, tag)) {
                chb.setChecked(true);
            }
        }
    }

    /**
     * 获取单选值
     *
     * @param checkBoxList
     * @return String 单选值
     */
    public static String getOne(List<CheckBox> checkBoxList) {
        String tag = "";
        for (CheckBox chb : checkBoxList) {
            if (chb.getTag() == null) {
                continue;
            }
            if (chb.isChecked()) {
                tag = chb.getTag().toString();
                break;
            }
        }
        return tag;
    }

    /**
     * 获取多选值
     *
     * @param checkBoxList
     * @return String 多个值结合，逗号分隔
     */
    public static String getMany(List<CheckBox> checkBoxList) {
        StringBuffer sb = new StringBuffer();
        for (CheckBox chb : checkBoxList) {
            if (chb.getTag() == null) {
                continue;
            }
            if (chb.isChecked()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(chb.getTag().toString());
            }
        }
        return sb.toString();
    }

    /**
     * 判断集合中是否包含特定值
     *
     * @param strArray
     * @param value
     * @return
     */
    private static boolean isContains(String[] strArray, String value) {
        if (strArray == null || strArray.length == 0) {
            return false;
        }
        if (value == null || value.length() == 0) {
            return false;
        }
        for (String str : strArray) {
            if (str == null) {
                continue;
            }
            if (str.trim().equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符串转换为Iint值
     *
     * @param str 字符串
     * @return int 整型值
     */
    public static int string2Int(String str) {
        int value = 0;
        try {
            value = Integer.parseInt(str);
        } catch (Exception e) {
        }
        return value;
    }

    /**
     * <p>按格式将数字位数补全</p>
     * <p>1->0001 1->01等</p>
     *
     * @param format 格式"0000","00"
     * @param num    要位数补全的数字
     * @return String 补全后的字符串
     */
    public static String formatNum(String format, int num) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(num);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 比较两个日期的先后关系
     *
     * @param date1
     * @param date2
     * @return boolean date1是否早于date2
     */
    public static boolean compareDate(String date1, String date2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            Date d1 = format.parse(date1);
            Date d2 = format.parse(date2);
            return d1.compareTo(d2) < 0 ? true : false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 比较两个时间的先后关系
     *
     * @param date1
     * @param date2
     * @param strFormat date1是否早于date2
     * @return boolean
     * @author yangdong
     * @date 2015-10-12 下午3:28:23
     */
    public static boolean compareDate(String date1, String date2, String strFormat) {
        SimpleDateFormat format = new SimpleDateFormat(strFormat);
        try {
            Date d1 = format.parse(date1);
            Date d2 = format.parse(date2);
            return d1.compareTo(d2) < 0 ? true : false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取yyyy-MM-dd 格式的日期
     *
     * @param str "yyyy年MM月dd日" 格式的日期
     * @return yyyy-MM-dd 格式的日期
     */
    public static String getFormatDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.format(format.parse(str));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化日期，返回字符串
     *
     * @param date 日期
     * @return String 格式yyyy-MM-dd HH:mm:ss
     * @author yangdong
     * @date 2015-9-24 下午5:51:18
     */
    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化日期，返回字符串
     *
     * @param date            日期
     * @param strTargetFormat 目标格式
     * @return String 目标格式日期串
     * @author yangdong
     * @date 2015-9-24 下午5:51:18
     */
    public static String formatDate(Date date, String strTargetFormat) {
        SimpleDateFormat format = new SimpleDateFormat(strTargetFormat);
        try {
            return format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将字符串转换为日期
     *
     * @param str yyyy-MM-dd格式的日期串
     * @return Date
     * @author yangdong
     * @date 2015-10-10 下午3:09:01
     */
    public static Date string2Date(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 将字符串转换为日期
     *
     * @param str       字符串
     * @param strFormat 字符串源格式
     * @return Date
     * @author yangdong
     * @date 2015-10-10 下午3:09:28
     */
    public static Date string2Date(String str, String strFormat) {
        SimpleDateFormat format = new SimpleDateFormat(strFormat);
        try {
            return format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 日期增减计算
     *
     * @param time     初始时间
     * @param interval 日期间隔（1天、-1天）
     * @return Date 增减后的日期
     * @author yangdong
     * @date 2016-8-25 上午10:37:31
     */
    public static Date dateAdd(Date time, int interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.DAY_OF_MONTH, interval);
        return cal.getTime();
    }

    /**
     * 移除字符串头尾的回车键
     *
     * @param str 字符串
     * @return String 移除回车键
     */
    public static String removeEnter(String str) {
        if (str == null) {
            return str;
        }
        if (!str.contains("\n")) {
            return str;
        }
        while (true) {
            if (str.endsWith("\n")) {
                str = str.substring(0, str.length() - 1);
            } else if (str.startsWith("\n")) {
                str = str.substring(1);
            } else {
                break;
            }
        }
        return str;
    }

    // 替换、过滤特殊字符
    public static String stringFilter(String str) {
        try {
            str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");//替换中文标号
            str = str.replaceAll("，", ",").replaceAll("“", "\"").replaceAll("”", "\"");//替换中文标号
            String regEx = "[『』]"; // 清除掉特殊字符
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
        } catch (Exception e) {
            return str;
        }
    }

    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static String getUrlParamter(String url, String key) {
        if (url == null || key == null) {
            return null;
        }
        try {
            URL urlObj = new URL(url);
            // 去参数串
            String queryString = urlObj.getQuery();
            if (queryString == null) {
                return null;
            }
            // 多参数分割
            String[] pairs = queryString.split("&");
            if (pairs == null || pairs.length == 0) {
                return null;
            }
            for (String pair : pairs) {
                if (pair == null) {
                    continue;
                }
                if (pair.startsWith(key + "=")) {
                    String[] values = pair.split("=");
                    if (values != null && values.length > 1) {
                        return values[1];
                    }
                    break;
                }
            }
        } catch (MalformedURLException e) {
        }
        return null;
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param str 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String str) {
        return !(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(str));
    }

}
