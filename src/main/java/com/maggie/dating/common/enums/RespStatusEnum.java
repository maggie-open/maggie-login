package com.maggie.dating.common.enums;

/**
 * 系统基础状态
 */
public enum RespStatusEnum {
    //通用
    SUCCESS("0000","成功"),
    FAIL("1000","失败"),
    FUNCTION_NO("9999","功能未开放"),

    //web:2000
    WEB_NOLOGIN("2010","未登录"),

    //ca:3000
    CA_FAIL("3000","证书错误,非法证书"),
    CA_CIPHER("3010","密文错误"),
    CA_USER_NO("3020","用户不存在"),
    CA_TIME_NO("3030","用户证书已过期"),
    CA_VALID_ERROR("3040","登录失败，验签不通过"),
    //oss:4000
    OSS_ERROR("4000","OSS请求异常"),

    //IM:5000
    IM_ERROR("5000","IM请求异常");


    private String code;
    private String mark;

    private RespStatusEnum(String code, String mark) {
        this.code = code;
        this.mark = mark;
    }

    public String getCode() {
        return code;
    }

    public String getMark() {
        return mark;
    }
}
