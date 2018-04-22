package com.maggie.dating.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * webSocket消息通知枚举
 */
public enum WmsTypeEnum {

    ORDER("1010","买家下单"),
    TO_CHOOSE("1020","可抢单"),
    CHODEN("1030","已抢单"),
    CHODEN_SUCCESS("1040","抢单成功"),
    CHODEN_FAIL("1050","抢单失败");

    private String code;
    private String mark;


    private WmsTypeEnum(String code, String mark){
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
        for (WmsTypeEnum be : values()) {
            map = new HashMap<String,String>();
            map.put("CODE", be.getCode());
            map.put("FLAG", be.getMark());
            list.add(map);
        }
        return list;
    }

}
