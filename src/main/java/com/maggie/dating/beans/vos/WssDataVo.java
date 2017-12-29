package com.maggie.dating.beans.vos;


import java.io.Serializable;

public class WssDataVo implements Serializable{

    private String wmsType;

    private String msg;

    private String data;

    public WssDataVo() {
    }

    public String getWmsType() {
        return wmsType;
    }

    public void setWmsType(String wmsType) {
        this.wmsType = wmsType;
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
        return "WssDataVo{" + "wmsType='" + wmsType + '\'' + ", msg='" + msg + '\'' + ", data='" + data + '\'' + '}';
    }
}
