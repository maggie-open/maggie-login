package com.maggie.dating.services.impls.core;

import com.maggie.dating.beans.Account;
import com.maggie.dating.beans.Buyer;
import com.maggie.dating.beans.Seller;
import com.maggie.dating.beans.vos.ReqDataVo;
import com.maggie.dating.beans.vos.RespDataVo;
import com.maggie.dating.common.constants.Constants;
import com.maggie.dating.common.constants.MaggieCoinConstants;
import com.maggie.dating.common.enums.RespStatusEnum;
import com.maggie.dating.common.enums.TxStatusEnum;
import com.maggie.dating.common.enums.UserTypeEnum;
import com.maggie.dating.common.util.DataUtil;
import com.maggie.dating.common.util.JsonUtil;
import com.maggie.dating.common.util.ValidCodeUtil;
import com.maggie.dating.common.util.ValidatorUtil;
import com.maggie.dating.daos.mysql.AccountDao;
import com.maggie.dating.services.ca.CAService;
import com.maggie.dating.services.core.BuyerService;
import com.maggie.dating.services.core.RedisService;
import com.maggie.dating.services.core.SellerService;
import com.maggie.dating.services.core.UserService;
import com.maggie.dating.services.oss.OSSService;
import com.maggie.dating.services.sms.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserServiceImpl implements UserService{

    public final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CAService caService;

    @Autowired
    private OSSService ossService;

    @Autowired
    private AccountDao accountDao;

    @Override
    public RespDataVo sendRegistCodeToMobile(HttpSession session, ReqDataVo reqData) {
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        String dataStr = reqData==null?null:reqData.getData();
        Map<String,String> dataMap = (Map<String, String>) JsonUtil.jsonStrToBean(dataStr, HashMap.class);
        String mobile = dataMap == null?"":dataMap.get("mobile");
        if(ValidatorUtil.isMobile(mobile)){
            //判断此手机号是否可以发送
            boolean canSend = redisService.canSendSmsCode(session.getId(),mobile);
            if(canSend){
                //发送短信验证码
                String registCode = ValidCodeUtil.getRandomStr(Constants.REGIST_CODE);
                session.setAttribute(Constants.SESSION_MOBILE,mobile);
                session.setAttribute(Constants.SESSION_REGCODE,registCode);
                session.setAttribute(Constants.SESSION_VALIDCOUNT,0);
                //验证状态为false
                session.setAttribute(Constants.SESSION_VALIDSTATUS,"false");
                resp.setStatus(RespStatusEnum.SUCCESS.getCode());
                resp.setMsg("动态验证码已发送到【"+mobile+"】");
                //存入redis
                redisService.sendSmsCode(session.getId(),mobile,registCode);
                logger.info("【"+mobile+"】发送注册验证码["+registCode+"]");
            }else{
                resp.setMsg("该手机号今日发送短信次数已超限");
            }

        }else{
            resp.setMsg("手机号异常");
        }
        return resp;
    }

    @Override
    public RespDataVo validUserMobile(HttpSession session, ReqDataVo reqData) {
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        String dataStr = reqData==null?null:reqData.getData();
        Map<String,String> dataMap = (Map<String, String>) JsonUtil.jsonStrToBean(dataStr, HashMap.class);
        String mobile = dataMap == null?null:dataMap.get("mobile");
        String registCode = dataMap == null?null:dataMap.get("registCode");
        session.setAttribute(Constants.SESSION_VALIDSTATUS,"false");
        if(DataUtil.isNotEmpty(mobile)&&DataUtil.isNotEmpty(registCode)){
            String sessionMobile = DataUtil.getString(session.getAttribute(Constants.SESSION_MOBILE));
            String sessionRegCode = DataUtil.getString(session.getAttribute(Constants.SESSION_REGCODE));
            int validCount = DataUtil.getInt(session.getAttribute(Constants.SESSION_VALIDCOUNT));
            //同一验证码3次验证失败
            if(mobile.equalsIgnoreCase(sessionMobile)&&registCode.equalsIgnoreCase(sessionRegCode)&&validCount<=3){
                //验证通过,标记验证通过,查询手机号对应的客户类型
                session.setAttribute(Constants.SESSION_VALIDSTATUS,"true");
                Buyer buyer = buyerService.getByMobile(mobile);
                Seller seller = sellerService.getByMobile(mobile);
                Map<String,String> dataRespMap = new HashMap<String,String>();
                dataRespMap.put("userType", UserTypeEnum.NONE.getCode());
                resp.setStatus(RespStatusEnum.SUCCESS.getCode());
                session.setAttribute(Constants.SESSION_USERTYPE,UserTypeEnum.NONE.getCode());
                if(buyer!=null){
                    dataRespMap.put("userType", UserTypeEnum.BUYER.getCode());
                    session.setAttribute(Constants.SESSION_USERTYPE,UserTypeEnum.BUYER.getCode());
                }else if(seller!=null){
                    dataRespMap.put("userType", UserTypeEnum.SELLER.getCode());
                    session.setAttribute(Constants.SESSION_USERTYPE,UserTypeEnum.SELLER.getCode());
                }
                resp.setData(JsonUtil.mapToJson(dataRespMap));
            }else{
                if (validCount<3){
                    int newValidCount = validCount+1;
                    session.setAttribute(Constants.SESSION_VALIDCOUNT,newValidCount);
                    resp.setMsg("验证失败，请重新验证，还有"+(3-newValidCount)+"次机会");
                }else{
                    resp.setMsg("验证失败，请重新获取验证码");
                }

            }

        }else{
            resp.setMsg("参数错误");
        }
        return resp;
    }

    @Transactional
    @Override
    public RespDataVo getUserCert(HttpSession session, ReqDataVo reqData) {
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        resp.setMsg("访问异常");
        String dataStr = reqData==null?null:reqData.getData();
        Map<String,String> dataMap = (Map<String, String>) JsonUtil.jsonStrToBean(dataStr, HashMap.class);
        Map<String,String> dataResp = new HashMap<String,String>();
        String sessionValid = DataUtil.getString(session.getAttribute(Constants.SESSION_VALIDSTATUS));
        if("true".equalsIgnoreCase(sessionValid)){
            //验证已通过,session链接可信
            String sessionMobile = DataUtil.getString(session.getAttribute(Constants.SESSION_MOBILE));
            //获取平台证书1:新用户 2：已有用户
            String sessionUserType = DataUtil.getString(session.getAttribute(Constants.SESSION_USERTYPE));
            //重新验证userType
            Buyer buyerValid = buyerService.getByMobile(sessionMobile);
            Seller sellerValid = sellerService.getByMobile(sessionMobile);
            if(buyerValid!=null){
                session.setAttribute(Constants.SESSION_USERTYPE,UserTypeEnum.BUYER);
            }else if(sellerValid!=null){
                session.setAttribute(Constants.SESSION_USERTYPE,UserTypeEnum.SELLER);
            }else{
                session.setAttribute(Constants.SESSION_USERTYPE,UserTypeEnum.NONE);
            }
            if(sessionUserType!=null){
                String equipCode = DataUtil.getString(dataMap.get("equipCode"));
                String pubKey = DataUtil.getString(dataMap.get("pubKey"));
                if((UserTypeEnum.NONE.getCode()).equalsIgnoreCase(sessionUserType)){
                    //新用户，注册逻辑:根据用户类型注册
                    String userType = DataUtil.getString(dataMap.get("userType"));
                    String nickName = DataUtil.getString(dataMap.get("nickName"));
                    String gender = DataUtil.getString(dataMap.get("gender"));
                    String birthday = DataUtil.getString(dataMap.get("birthday"));
                    String city = DataUtil.getString(dataMap.get("city"));
                    //新增用户获取userId
                    String userId = null;
                    if((UserTypeEnum.BUYER.getCode()).equalsIgnoreCase(userType)){
                        Buyer buyer = new Buyer();
                        buyer.setMobile(sessionMobile);
                        buyer.setUserName(sessionMobile);
                        buyer.setNickName(nickName);
                        buyer.setBirthday(birthday);
                        buyer.setGender(gender);
                        buyer.setCity(city);
                        buyer = buyerService.saveBuyer(buyer);

                        userId = buyer.getId();
                    }else if((UserTypeEnum.SELLER.getCode()).equalsIgnoreCase(userType)){
                        Seller seller = new Seller();
                        seller.setMobile(sessionMobile);
                        seller.setUserName(sessionMobile);
                        seller.setNickName(nickName);
                        seller.setBirthday(birthday);
                        seller.setGender(gender);
                        seller.setCity(city);
                        seller.setIsVisible("true");
                        seller = sellerService.saveSeller(seller);
                        //生成账户信息
                        userId = seller.getId();
                    }
                    resp.setMsg("验证失败");
                    if(userId!=null){
                        //生成证书
                        RespDataVo caApplyResp = caService.applyCertificate(equipCode,sessionMobile,pubKey,userId);
                        if(caApplyResp!=null&&(RespStatusEnum.SUCCESS.getCode()).equalsIgnoreCase(caApplyResp.getStatus())){
                            //生成成功，返回
                            resp.setStatus(RespStatusEnum.SUCCESS.getCode());
                            dataResp.put("cert",caApplyResp.getData());
                            dataResp.put("userId",caApplyResp.getKey());
                            resp.setMsg("生成新证书");
                            //生成账户
                            //生成账户信息===>或许有坑
                            Account account = new Account();
                            account.setAmount(new BigDecimal(0.00));
                            account.setUserId(userId);
                            account = accountDao.save(account);
                        }
                    }
                }else if(sessionUserType.equalsIgnoreCase(UserTypeEnum.SELLER.getCode())||sessionUserType.equalsIgnoreCase(UserTypeEnum.BUYER.getCode())){
                    //已有用户找回证书:根据手机号 mobile equipCode pubKey
                    RespDataVo caResp = caService.queryUserCert(sessionMobile,equipCode,pubKey);
                    if(caResp!=null&&(RespStatusEnum.SUCCESS.getCode()).equalsIgnoreCase(caResp.getStatus())){
                        //找到证书，返回
                        resp.setStatus(RespStatusEnum.SUCCESS.getCode());
                        dataResp.put("cert",caResp.getData());
                        dataResp.put("userId",caResp.getKey());
                        resp.setMsg("找回证书");
                    }else{
                        //查找失败，生成新证书
                        resp.setMsg("无有效证书，生成新证书失败");
                        String userId  = null;
                        if((UserTypeEnum.BUYER.getCode()).equalsIgnoreCase(sessionUserType)){
                            Buyer buyer = buyerService.getByMobile(sessionMobile);
                            userId = buyer.getId();
                        }else{
                            Seller seller = sellerService.getByMobile(sessionMobile);
                            userId = seller.getId();
                        }
                        RespDataVo caApplyResp = caService.applyCertificate(equipCode,sessionMobile,pubKey,userId);
                        if(caApplyResp!=null&&(RespStatusEnum.SUCCESS.getCode()).equalsIgnoreCase(caApplyResp.getStatus())){
                            //生成成功，返回
                            resp.setStatus(RespStatusEnum.SUCCESS.getCode());
                            dataResp.put("cert",caApplyResp.getData());
                            dataResp.put("userId",caApplyResp.getKey());
                            resp.setMsg("生成新证书");
                        }
                    }
                }
            }
            resp.setData(JsonUtil.beanToJsonStr(dataResp));
        }else{
            resp.setMsg("请验证手机动态验证码");
        }
        return resp;
    }

    @Override
    public RespDataVo toLogin(HttpSession session, ReqDataVo reqData) {
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        String code =  ValidCodeUtil.getRandomStr(4);
        session.setAttribute(Constants.SESSION_LOGINCODE,code);
        String dataStr = reqData==null?null:reqData.getData();
        Map<String,String> dataMap = (Map<String, String>) JsonUtil.jsonStrToBean(dataStr, HashMap.class);
        String cert = dataMap!=null?DataUtil.getString(dataMap.get("cert")):null;
        RespDataVo certRes = caService.toCertValidation(cert,code);
        if(certRes!=null&&(RespStatusEnum.SUCCESS.getCode()).equalsIgnoreCase(certRes.getStatus())){
            resp.setStatus(RespStatusEnum.SUCCESS.getCode());
            Map<String,String> certMap = (Map<String, String>) JsonUtil.jsonStrToBean(certRes.getData(), HashMap.class);
            session.setAttribute(Constants.SESSION_MOBILE,certMap.get(Constants.SESSION_MOBILE));
            session.setAttribute(Constants.SESSION_EQUITCODE,certMap.get(Constants.SESSION_EQUITCODE));

            Map<String,String> respDataMap = new HashMap<String,String>();
            respDataMap.put("validMsg",certMap.get("key"));
            resp.setData(JsonUtil.beanToJsonStr(respDataMap));
            resp.setMsg("验证信息已发送");
        }else{
            resp.setStatus(RespStatusEnum.FAIL.getCode());
            resp.setMsg(certRes.getMsg());
        }
        return resp;
    }

    @Override
    public RespDataVo login(HttpSession session, ReqDataVo reqData) {
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        //
        String dataStr = reqData==null?null:reqData.getData();
        Map<String,String> dataMap = (Map<String, String>) JsonUtil.jsonStrToBean(dataStr, HashMap.class);
        String poKey = dataMap!=null?DataUtil.getString(dataMap.get("poKey")):null;
        if(poKey!=null){
            //验证
            String code = DataUtil.getString(session.getAttribute(Constants.SESSION_LOGINCODE));
            String mobile = DataUtil.getString(session.getAttribute(Constants.SESSION_MOBILE));
            String equipCode = DataUtil.getString(session.getAttribute(Constants.SESSION_EQUITCODE));
            RespDataVo caValidResp = caService.certValidation(poKey,mobile,equipCode,code);
            if(caValidResp!=null&&(RespStatusEnum.SUCCESS.getCode()).equalsIgnoreCase(caValidResp.getStatus())){
                //验证成功存入session
                String loginUserId = caValidResp.getKey();
                //查找用户信息
                Buyer buyer = buyerService.getById(loginUserId);
                Map<String,String> respDataMap = new HashMap<String,String>();
                String userType = null;
                if(buyer!=null){
                    resp.setStatus(RespStatusEnum.SUCCESS.getCode());
                    userType = UserTypeEnum.BUYER.getCode();
                    respDataMap.put("userType",userType);
                    respDataMap.put("user",JsonUtil.beanToJsonStr(buyer));
                }else{
                    Seller seller = sellerService.getById(loginUserId);
                    if(seller!=null){
                        resp.setStatus(RespStatusEnum.SUCCESS.getCode());
                        userType = UserTypeEnum.SELLER.getCode();
                        respDataMap.put("userType",userType);
                        respDataMap.put("user",JsonUtil.beanToJsonStr(seller));

                    }else{
                        resp.setMsg("登录失败");
                    }
                }
                if(userType!=null){
                    session.setAttribute(Constants.SESSION_USERID,loginUserId);
                    session.setAttribute(Constants.SESSION_USERTYPE,userType);
                }
                respDataMap.put("JSESSIONID",session.getId());
                resp.setData(JsonUtil.beanToJsonStr(respDataMap));
            }
        }
        return resp;
    }


    @Override
    public RespDataVo logout(HttpSession session, ReqDataVo reqData) {
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        resp.setMsg("退出失败");
        session.invalidate();
        resp.setStatus(RespStatusEnum.SUCCESS.getCode());
        resp.setMsg("已退出");
        return resp;
    }

    @Override
    public RespDataVo getOssToken(HttpSession session, ReqDataVo reqData) {
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        resp.setMsg("获取ossToken失败");
        String userId = DataUtil.getString(session.getAttribute(Constants.SESSION_USERID));
        RespDataVo ossResp = ossService.token(userId);
        if(ossResp!=null&&(RespStatusEnum.SUCCESS.getCode()).equalsIgnoreCase(ossResp.getStatus())){
            resp.setStatus(RespStatusEnum.SUCCESS.getCode());
            resp.setMsg("获取成功");
            resp.setData(ossResp.getData());
        }
        return resp;
    }

    @Override
    public RespDataVo getUserPubKey(HttpSession session, ReqDataVo reqData) {
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        resp.setMsg("获取公钥失败");
        String dataStr = reqData==null?null:reqData.getData();
        Map<String,String> dataMap = (Map<String, String>) JsonUtil.jsonStrToBean(dataStr, HashMap.class);
        String userId = dataMap!=null?DataUtil.getString(dataMap.get("userId")):null;
        if(userId!=null){
            RespDataVo caResp = caService.queryUserPublicKey(userId);
            if(caResp!=null&&(RespStatusEnum.SUCCESS.getCode()).equalsIgnoreCase(caResp.getStatus())){
                resp.setStatus(RespStatusEnum.SUCCESS.getCode());
                resp.setMsg("获取成功");
                Map<String,String> respDataMap = new HashMap<String,String>();
                respDataMap.put("pubKey",caResp.getData());
                resp.setData(JsonUtil.beanToJsonStr(respDataMap));
            }
        }
        return resp;
    }

    @Override
    public RespDataVo updateOrderTxStatus(HttpSession session, ReqDataVo reqData) {
        //更新订单状态：确认、取消等 分状态执行不同业务
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        resp.setMsg("操作失败");
        String dataStr = reqData==null?null:reqData.getData();
        Map<String,String> dataMap = (Map<String, String>) JsonUtil.jsonStrToBean(dataStr, HashMap.class);
        String userId = DataUtil.getString(session.getAttribute(Constants.SESSION_USERID));
        String txId = DataUtil.getString(dataMap.get("txId"));
        String txStatus = DataUtil.getString(dataMap.get("txStatus"));
        String custType = DataUtil.getString(session.getAttribute(Constants.SESSION_USERTYPE));
        String status = null;
        if((UserTypeEnum.BUYER.getCode()).equalsIgnoreCase(custType)){
            status = buyerService.updateOrderTxStatus(userId,txId,txStatus);
        }else{
            status = sellerService.updateOrderTxStatus(userId,txId,txStatus);
        }
        Map<String,String> respDataMap = new HashMap<String,String>();
        respDataMap.put("status",status);
        resp.setData(JsonUtil.beanToJsonStr(respDataMap));
        resp.setStatus(RespStatusEnum.SUCCESS.getCode());
        resp.setMsg("操作成功");
        return resp;
    }


    @Override
    public RespDataVo getMaggieCoin(HttpSession session, ReqDataVo reqData) {
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        resp.setMsg("获取失败");
        String userId = DataUtil.getString(session.getAttribute(Constants.SESSION_USERID));
        if(DataUtil.isNotEmpty(userId)){
            Account account = accountDao.findAccountByUserId(userId);
            if(account==null){
                account = new Account();
                account.setAmount(new BigDecimal(0.00));
                account.setUserId(userId);
            }
            account.setAmount(account.getAmount().add(MaggieCoinConstants.RANDOM_ADD_COIN));
            account = accountDao.save(account);
            Map<String,String> respDataMap = new HashMap<String,String>();
            respDataMap.put("amt",DataUtil.getString(account.getAmount()));
            resp.setData(JsonUtil.beanToJsonStr(respDataMap));
            resp.setStatus(RespStatusEnum.SUCCESS.getCode());
            resp.setMsg("成功获取麦粒");
        }
        return resp;
    }

    @Override
    public RespDataVo getAccountAmt(HttpSession session, ReqDataVo reqData) {
        RespDataVo resp = new RespDataVo();
        resp.initDefaultVo();
        resp.setMsg("获取失败");
        String userId = DataUtil.getString(session.getAttribute(Constants.SESSION_USERID));
        if(DataUtil.isNotEmpty(userId)){
            Account account = accountDao.findAccountByUserId(userId);
            Map<String,String> respDataMap = new HashMap<String,String>();
            respDataMap.put("amt",DataUtil.getString(account.getAmount()));
            resp.setData(JsonUtil.beanToJsonStr(respDataMap));
            resp.setStatus(RespStatusEnum.SUCCESS.getCode());
            resp.setMsg("成功");
        }
        return resp;
    }
}
