package com.maggie.dating.common.exception;

import lombok.Data;
import lombok.ToString;
import vip.maggie.dating.beans.vos.RespDataVo;
import vip.maggie.dating.common.enums.RespStatusEnum;

@Data
@ToString
public class BusiException extends RuntimeException{
    private RespDataVo resp;


    public BusiException(){
        this(RespStatusEnum.FAIL);
    }

    public BusiException(RespDataVo resp){
        super(resp.getMsg());
        this.resp = resp;
    }
    public BusiException(RespStatusEnum rse){
        super(rse.getMark());
        this.resp = RespDataVo.fail(rse);
    }

    public BusiException(String msg){
        super();
        this.resp = new RespDataVo();
        this.resp.setStatus(RespStatusEnum.FAIL.getCode());
        this.resp.setMsg(msg);
    }
    public BusiException(String status,String msg){
        super("status:"+status+"\tmsg:"+msg);
        this.resp = new RespDataVo();
        this.resp.setStatus(status);
        this.resp.setMsg(msg);
    }

}
