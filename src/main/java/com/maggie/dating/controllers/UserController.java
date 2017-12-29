package com.maggie.dating.controllers;

import com.maggie.dating.beans.vos.ReqDataVo;
import com.maggie.dating.beans.vos.RespDataVo;
import com.maggie.dating.controllers.listeners.SessionContext;
import com.maggie.dating.services.core.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {
    private final  static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 获取短信验证码
     * @param session 保存mobile+registCode
     * @param reqData {mobile:''}
     * @return
     */
    @PostMapping("/registCode")
    public RespDataVo registCode(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //发送短信验证码,记录发送记录
        respData = userService.sendRegistCodeToMobile(session,reqData);
        return respData;
    }


    /**
     * 验证手机号与动态验证码是否匹配，验证该手机号的用户类型
     * @param session
     * @param reqData {mobile,registCode}
     * @return data{userType}
     */
    @PostMapping("/validMobile")
    public RespDataVo validMobile(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //验证短信验证码，查询用户类型
        respData = userService.validUserMobile(session,reqData);
        return respData;
    }


    /**
     * 申请平台通行证
     * @param session
     * @param reqData data{equipCode pubKey userType==NONE?=>nickName gender birthday city}
     * @return
     */
    @PostMapping("/cert")
    public RespDataVo applyCert(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //根据equipCode pubKey userType判断，是注册、找回、重新生成
        respData = userService.getUserCert(session,reqData);
        return respData;
    }


    /**
     * 准备使用通行证登录平台：验证证书，发送验证私钥信息
     * @param session 保存用户登录信息 cert validMsg
     * @param reqData data{cert}
     * @return data{validMsg}
     */
    @PostMapping("/toLogin")
    public RespDataVo toLogin(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //验证cert是否过期，发送加密信息
        respData = userService.toLogin(session,reqData);
        return respData;
    }


    /**
     * 使用通行证登录平台,验证私钥解密信息是否正确
     * @param session
     * @param reqData data{poKey}
     * @return
     */
    @PostMapping("/login")
    public RespDataVo login(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //根据私钥解密信息，判断登录是否成功
        respData = userService.login(session,reqData);
        SessionContext.AddSession(session);
        return respData;
    }


    /**
     * 使用通行证登录平台,验证私钥解密信息是否正确
     * @param session
     * @param reqData data{poKey}
     * @return
     */
    @PostMapping("/logout")
    public RespDataVo logout(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //根据私钥解密信息，判断登录是否成功
        respData = userService.logout(session,reqData);
        return respData;
    }


    /**
     * 获取文件服务器token
     * @param session
     * @param reqData
     * @return
     */
    @RequestMapping("/getOssToken")
    public RespDataVo getOssToken(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //已成功登录的用户，获取文件服务器操作权限
        respData = userService.getOssToken(session,reqData);
        return respData;
    }

    /**
     * 获取指定用户的公钥，供加密会话使用
     * @param session
     * @param reqData data{userId}
     * @return data{pubKey}
     */
    @PostMapping("/getPubKey")
    public RespDataVo getPubKey(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //已成功登录的用户，获取指定用户的公钥
        respData = userService.getUserPubKey(session,reqData);
        return respData;
    }


    /**
     * 操作订单状态
     * @param session
     * @param reqData data{txId txStatus }
     * @return
     */
    @PostMapping("/orderTxStatus")
    public RespDataVo updateOrderTxStatus(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //已成功登录的用户 更改指定订单状态
        respData = userService.updateOrderTxStatus(session,reqData);
        return respData;
    }



    /**
     * 获取麦粒
     * @param session
     * @param reqData data{amt  }
     * @return
     */
    @PostMapping("/getMaggieCoin")
    public RespDataVo getMaggieCoin(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //已成功登录的用户 更改指定订单状态
        respData = userService.getMaggieCoin(session,reqData);
        return respData;
    }


    /**
     * 获取麦粒
     * @param session
     * @param reqData data{amt  }
     * @return
     */
    @PostMapping("/getAccountAmt")
    public RespDataVo getAccountAmt(HttpSession session , ReqDataVo reqData){
        RespDataVo respData = null;
        //已成功登录的用户 更改指定订单状态
        respData = userService.getAccountAmt(session,reqData);
        return respData;
    }

}
