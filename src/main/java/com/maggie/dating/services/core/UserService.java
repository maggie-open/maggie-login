package com.maggie.dating.services.core;

import com.maggie.dating.beans.vos.ReqDataVo;
import com.maggie.dating.beans.vos.RespDataVo;

import javax.servlet.http.HttpSession;

/**
 * 用户公共服务接口
 */
public interface UserService {

    RespDataVo sendRegistCodeToMobile(HttpSession session, ReqDataVo reqData);

    /**
     * 验证手机号与动态验证码是否匹配，验证该手机号的用户类型
     * @param session
     * @param reqData
     * @return
     */
    RespDataVo validUserMobile(HttpSession session, ReqDataVo reqData);

    RespDataVo getUserCert(HttpSession session, ReqDataVo reqData);

    RespDataVo toLogin(HttpSession session, ReqDataVo reqData);

    RespDataVo login(HttpSession session, ReqDataVo reqData);

    RespDataVo logout(HttpSession session, ReqDataVo reqData);

    RespDataVo getOssToken(HttpSession session, ReqDataVo reqData);

    RespDataVo getUserPubKey(HttpSession session, ReqDataVo reqData);

    RespDataVo updateOrderTxStatus(HttpSession session, ReqDataVo reqData);

    RespDataVo getMaggieCoin(HttpSession session, ReqDataVo reqData);

    RespDataVo getAccountAmt(HttpSession session, ReqDataVo reqData);
}
