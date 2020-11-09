package com.sss.sssforum.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author lws
 * @date 2020-09-10 09:03
 **/
public class JsonUtils {


    private static final ObjectMapper JSON = new ObjectMapper();

    static {
        JSON.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JSON.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
    }

    public static String toJson(Object obj) {
        try {
            return JSON.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return
     */
      public static <T> T jsonToObject(String jsonData, Class<T> beanType) {
                try {
                   T t = JSON.readValue(jsonData, beanType);

                   return t;
               } catch (Exception e) {
                   e.printStackTrace();
               }
           return null;
       }
}
