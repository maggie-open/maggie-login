package com.maggie.dating.services.ca;

import com.maggie.dating.beans.vos.RespDataVo;

public interface CAService {
    /**
     * 申请设备证书
     * @param equitCode 设备号
     * @param mobile    手机号
     * @param publicKey 公钥
     * @return
     */
    RespDataVo applyCertificate(String equitCode, String mobile, String publicKey, String userId);

    /**
     * 登录验证
     * @param cert
     * @param code
     * @return
     */
    RespDataVo toCertValidation(String cert, String code);

    /**
     * 查找用户设备证书
     * @param
     * @return
     */
    RespDataVo queryUserCert(String mobile,String equipCode,String pubKey);

    /**
     * 会话验证
     * @param poKey
     * @param mobile
     * @param equitCode
     * @param random
     * @return
     */
    RespDataVo certValidation(String poKey, String mobile, String equitCode, String random);

    /**
     * 根据手机号查询用户激活状态密钥信息
     * @param userId
     * @return
     */
    RespDataVo queryUserPublicKey(String userId);//参数变更为userId

}
