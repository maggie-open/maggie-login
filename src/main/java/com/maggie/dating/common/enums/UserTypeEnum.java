package com.maggie.dating.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口返回状态枚举
 */
public enum UserTypeEnum {

    BUYER("buyer","买家"),
    SELLER("seller","卖家"),
    NONE("none","非平台用户");

    private String code;
    private String mark;


    private UserTypeEnum(String code, String mark){
        this.code = code;
        this.mark = mark;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }


    public static List getList(){
        List list = new ArrayList();
        Map<String,String> map = null;
        for (UserTypeEnum be : values()) {
            map = new HashMap<String,String>();
            map.put("CODE", be.getCode());
            map.put("FLAG", be.getMark());
            list.add(map);
        }
        return list;
    }

}
