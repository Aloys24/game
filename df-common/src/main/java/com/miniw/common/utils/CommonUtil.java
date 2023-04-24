package com.miniw.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 工具 Util
 */
public class CommonUtil {

    /**
     * Integer
     * 是否正数
     */
    public static boolean positive(Integer value) {
        if (value == null)
            return false;

        return value > 0;
    }
    public static boolean positive(Integer value, boolean containZero) {
        if (value == null)
            return false;
        if (containZero)
            return value >= 0;
        return value > 0;
    }

    /**
     * 是否正整数
     */
    public static boolean positive(Long value) {
        if (value == null)
            return false;
        return value > 0;
    }

    /**
     * int 初始化
     */
    public static int initIValue(String value) {
        if (StringUtils.isBlank(value))
            return 0;
        return Integer.parseInt(value);
    }

    /**
     * Long 初始化
     */
    public static long initValue(String value) {
        if (StringUtils.isBlank(value))
            return 0;
        return Long.parseLong(value);
    }

    /**
     * 获取当天零点时间
     */
    public static Date zero() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当天23:59:59时间
     */
    public static Date twelve() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static <T> T parseObject(Object obj, Class<T> clazz) {
        if (obj == null)
            return null;
        JSONObject jsonObject = parseJsonObject(JSON.toJSONString(obj));
        if (jsonObject == null)
            return null;
        return jsonObject.toJavaObject(clazz);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        try {
            if (StringUtils.isNotBlank(text))
                return JSONObject.parseObject(text, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> parseArrayObject(Object obj, Class<T> clazz) {
        if (obj == null)
            return null;
        JSONArray jsonArray = parseJsonArray(JSON.toJSONString(obj));
        if (jsonArray == null || jsonArray.isEmpty())
            return null;
        return jsonArray.toJavaList(clazz);
    }

    public static <T> List<T> parseArrayObject(String str, Class<T> clazz) {
        if (str == null)
            return null;
        JSONArray jsonArray = parseJsonArray(str);
        if (jsonArray == null || jsonArray.isEmpty())
            return null;
        return jsonArray.toJavaList(clazz);
    }

    public static JSONObject parseJsonObject(String text) {
        try {
            if (StringUtils.isNotBlank(text))
                return JSONObject.parseObject(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray parseJsonArray(String text) {
        try {
            if (StringUtils.isNotBlank(text))
                return JSONArray.parseArray(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取整型随机数
     *
     * @param bound
     */
    public static int randomInx(Integer bound) {
        if (positive(bound))
            return ThreadLocalRandom.current().nextInt(bound);
        return ThreadLocalRandom.current().nextInt();
    }

    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }


    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    public static String randomUUID8() {
        StringBuilder sb = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            sb.append(chars[x % 0x3E]);
        }
        return sb.toString();
    }

}