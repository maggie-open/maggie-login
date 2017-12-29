package com.maggie.dating.beans.vos;

import com.maggie.dating.common.enums.RespStatusEnum;

public class RespDataVo {

    private String status;

    private String key;

    private String msg;

    private String data;


    public RespDataVo() {
    }

    public RespDataVo(String status, String key, String msg, String data) {
        this.status = status;
        this.key = key;
        this.msg = msg;
        this.data = data;
    }

    public void initDefaultVo(){
        this.status = RespStatusEnum.FAIL.getCode();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespDataVo{" +
                "status='" + status + '\'' +
                ", key='" + key + '\'' +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
