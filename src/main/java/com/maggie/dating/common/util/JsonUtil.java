package com.maggie.dating.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonUtil {


    public static Object jsonStrToBean(String jsonStr, Class pojoCalss) {
        Object obj= null;
        if(DataUtil.isNotEmpty(jsonStr)){
            ObjectMapper mapper = new ObjectMapper();
            try {
                obj = mapper.readValue(jsonStr,pojoCalss);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }


    public static List jsonStrToListBean(String jsonStr, Class pojoCalss) {
        List obj= null;
        if(DataUtil.isNotEmpty(jsonStr)){
            ObjectMapper mapper = new ObjectMapper();
            try {
                JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, pojoCalss);
                obj = mapper.readValue(jsonStr,javaType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }


    public static String beanToJsonStr(Object bean){
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    public static String mapToJson(Map map) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    public static Map stringToMap(String str) {
        if(DataUtil.isNotEmpty(str)){
            ObjectMapper mapper = new ObjectMapper();
            Map map = new HashMap();
            try {
                map=mapper.readValue(str, Map.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }
        return null;
    }

}
