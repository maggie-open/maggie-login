package com.maggie.dating.controllers;

import com.maggie.dating.beans.vos.RespDataVo;
import com.maggie.dating.common.enums.RespStatusEnum;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 公共资源
 */
@RestController
public class IndexController {
    /**
     * session断开 转发
     * @return
     */
    @RequestMapping("/sessInvalid")
    public RespDataVo sessionInvalid(){
        RespDataVo rs = new RespDataVo();
        rs.setStatus(RespStatusEnum.WEB_NOLOGIN.getCode());
        rs.setMsg(RespStatusEnum.WEB_NOLOGIN.getMark());
        return rs;
    }

}
