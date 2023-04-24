package com.miniw.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 请求参数集印射成map
 *
 * @author luoquan
 * @date 2021/07/29
 */
public class RequestParamsToMapUtil {

    /**
     * 印射成Map<String, Object>
     *
     * @param request 请求
     * @return {@link Map<String, Object>}
     */
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, String[]> properties = request.getParameterMap();
        Map<String, Object> returnMap = new HashMap<>(5);
        Iterator<Map.Entry<String, String[]>> iter = properties.entrySet().iterator();
        String name = "";
        String value = "";
        while (iter.hasNext()) {
            Map.Entry<String, String[]> entry = iter.next();
            name = entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    /**
     * 返回值类型为Map<String, String>
     *
     * @param request 请求
     * @return {@link Map<String, String>}
     */
    public static Map<String, String> getParameterStringMap(HttpServletRequest request) {
        System.out.println(request.getParameter("resultData"));
        Map<String, String[]> properties = request.getParameterMap();
        Map<String, String> returnMap = new HashMap<String, String>(5);
        String name = "";
        String value = "";
        for (Map.Entry<String, String[]> entry : properties.entrySet()) {
            name = entry.getKey();
            String[] values = entry.getValue();
            if (null == values) {
                value = "";
            } else if (values.length > 1) {
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = values[0];
            }
            returnMap.put(name, value);

        }
        return returnMap;
    }

}
