package com.maggie.dating.beans.vos;

public class ReqDataVo {

    private String data;

    public ReqDataVo() {
    }

    public ReqDataVo(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReqDataVo{" +
                "data='" + data + '\'' +
                '}';
    }
}
