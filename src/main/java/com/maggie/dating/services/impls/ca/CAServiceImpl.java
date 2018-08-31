package com.maggie.dating.services.impls.ca;

import com.maggie.dating.beans.UserKey;
import com.maggie.dating.beans.vos.RespDataVo;
import com.maggie.dating.common.enums.RespStatusEnum;
import com.maggie.dating.common.util.DataUtil;
import com.maggie.dating.common.util.JsonUtil;
import com.maggie.dating.common.util.RSAUtil;
import com.maggie.dating.daos.mysql.UserKeyDao;
import com.maggie.dating.services.ca.CAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@PropertySource({"classpath:config/platkey.properties"})
public class CAServiceImpl implements CAService{
    Logger logger = LoggerFactory.getLogger(CAServiceImpl.class);

    /**
     * 指定公钥存放文件
     */
    @Value("${plat.publicKey}")
    private String PUBLIC_KEY_FILE;
    /**
     * 指定私钥存放文件
     */
    @Value("${plat.privateKey}")
    private String PRIVATE_KEY_FILE;

    @Autowired
    UserKeyDao userKeyDao;

    @Transient
    public RespDataVo applyCertificate(String equitCode, String mobile, String publicKey, String userId) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("equitCode",equitCode);
        map.put("mobile",mobile);
//        map.put("caStartTime", DataUtils.dateToStr(new Date()));
        map.put("caEndTime", DataUtil.dateAddOneYearToStr(new Date()));
//        map.put("key",publicKey);
        String source = JsonUtil.mapToJson(map);//ObjectTurnMap.mapToJson(map);
        String CA = null;
        try {
            String planKey = RSAUtil.getKey(PUBLIC_KEY_FILE);
            CA = RSAUtil.encryptByPublicKey(source,planKey);
            saveUserKeyInfo(equitCode,mobile,publicKey,userId,CA);
            RespDataVo vo = returnMsgDealWith(RespStatusEnum.SUCCESS.getMark(), RespStatusEnum.SUCCESS.getCode(),CA,userId);
            return vo;
        } catch (Exception e) {
            logger.info("CA证书生成发生异常，异常信息："+e.getMessage());
            e.printStackTrace();
            return returnMsgDealWith(RespStatusEnum.FAIL.getMark(), RespStatusEnum.FAIL.getCode(),null,userId);
        }
    }

    @Override
    public RespDataVo toCertValidation(String cert,String code) {
        String data = "";
        String jsonStr = "";
        String userId=null;
        try {
            String planKey = RSAUtil.getKey(PRIVATE_KEY_FILE);
            String source = RSAUtil.decryptByPrivateKey(cert,planKey);
            Map<String,Object> dataMap = JsonUtil.stringToMap(source);
            if(DataUtil.isNull(dataMap)){
                return returnMsgDealWith(RespStatusEnum.CA_CIPHER.getMark(), RespStatusEnum.CA_CIPHER.getCode(),null,null);
            }
            String equitCode = DataUtil.StringUtil(dataMap.get("equitCode"));
            String mobile = DataUtil.StringUtil(dataMap.get("mobile"));
            UserKey entity = userKeyDao.findByMobileAndEquipmentCode(mobile,equitCode);
            if(DataUtil.isEmpty(entity)){
                return returnMsgDealWith(RespStatusEnum.CA_USER_NO.getMark(), RespStatusEnum.CA_USER_NO.getCode(),null,null);
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dt1 = df.parse(DataUtil.dateToStr(new Date()));
            Date dt2 = df.parse(DataUtil.dateToStr(DataUtil.strToDate(dataMap.get("caEndTime"))));
            if(dt1.getTime()>dt2.getTime()){
                return returnMsgDealWith(RespStatusEnum.CA_TIME_NO.getMark(), RespStatusEnum.CA_TIME_NO.getCode(),null,null);
            }
            data = RSAUtil.encryptByPublicKey(code,entity.getPublicKey());
            userId = DataUtil.StringUtil(entity.getUserId());
            Map<String,String> jsonMap = new HashMap<String, String>();
            jsonMap.put("mobile",mobile);
            jsonMap.put("equitCode",equitCode);
//            jsonMap.put("random",code);
            jsonMap.put("key",DataUtil.replaceBlank(data));
            jsonStr = JsonUtil.mapToJson(jsonMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("用户证书解密发生异常，异常信息："+e.getMessage());
            return returnMsgDealWith(RespStatusEnum.CA_FAIL.getMark(), RespStatusEnum.CA_FAIL.getCode(),null,null);
        }
        return returnMsgDealWith(RespStatusEnum.SUCCESS.getMark(), RespStatusEnum.SUCCESS.getCode(),jsonStr,userId);
    }

    @Override
    @Transient
    public RespDataVo certValidation(String poKey, String mobile, String equitCode, String random) {
        UserKey entity = userKeyDao.findByMobileAndEquipmentCode(mobile,equitCode);
        String verify = "";
        if(DataUtil.isEmpty(entity)){
            return returnMsgDealWith(RespStatusEnum.CA_USER_NO.getMark(), RespStatusEnum.CA_USER_NO.getCode(),null,null);
        }
        try {
            verify = RSAUtil.decryptByPublicKey(poKey,entity.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("会话验证发生异常，异常信息："+e.getMessage());
            return returnMsgDealWith(RespStatusEnum.CA_VALID_ERROR.getMark(), RespStatusEnum.CA_VALID_ERROR.getCode(),null,null);
        }
        if (verify.equals(random)){
            updateUserKeyInfo(mobile);
            String jsonStr = JsonUtil.beanToJsonStr(entity);
            entity.setStatus(1);
            userKeyDao.save(entity);
            return returnMsgDealWith(RespStatusEnum.SUCCESS.getMark(), RespStatusEnum.SUCCESS.getCode(),jsonStr,DataUtil.StringUtil(entity.getUserId()));
        }else{
            return returnMsgDealWith(RespStatusEnum.CA_VALID_ERROR.getMark(), RespStatusEnum.CA_VALID_ERROR.getCode(),null,null);
        }
    }

    public RespDataVo queryUserPublicKey(String userId){
        UserKey userKey = userKeyDao.findByUserIdAndStatus(userId,1);
        if(DataUtil.isNotEmpty(userKey)){
            String jsonStr =DataUtil.replaceBlank(userKey.getPublicKey());//ObjectTurnMap.classToJson(userKey);
            return returnMsgDealWith(RespStatusEnum.SUCCESS.getMark(), RespStatusEnum.SUCCESS.getCode(),jsonStr,DataUtil.StringUtil(userKey.getUserId()));
        }else{
            return returnMsgDealWith(RespStatusEnum.CA_USER_NO.getMark(), RespStatusEnum.CA_USER_NO.getCode(),null,null);
        }
    }

    public String getRandomString(int length,String flag) {
        String base = "abcdefghijklmnopqrstuvwxyz123456789";
        base = base.replace(flag, "");
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    @Override
    public RespDataVo queryUserCert(String mobile, String equipCode, String pubKey) {
        UserKey userKey = userKeyDao.findByMobileAndEquipmentCodeAndPublicKeyAndStatus(mobile,equipCode,pubKey,1);
        String cert = userKey!=null?userKey.getUserCa():null;
        if(DataUtil.isNotEmpty(cert)){
           return returnMsgDealWith(RespStatusEnum.SUCCESS.getMark(), RespStatusEnum.SUCCESS.getCode(),cert,DataUtil.StringUtil(userKey.getUserId()));
        }else{
            return returnMsgDealWith(RespStatusEnum.CA_USER_NO.getMark(), RespStatusEnum.CA_USER_NO.getCode(),null,null);
        }

    }

    /**
     * 根据手机号修改全部用户登录状态
     * @param mobile
     */
    private void updateUserKeyInfo(String mobile){
        List<UserKey> userList = userKeyDao.findByMobile(mobile);
        for (UserKey entity:userList) {
            entity.setStatus(0);
            userKeyDao.save(entity);
        }
    }

    /**
     * 保存用户密钥信息
     * @param equitCode
     * @param mobile
     * @param publicKey
     * @param userId
     */
    private void saveUserKeyInfo(String equitCode, String mobile, String publicKey,String userId,String ca){
//        UserKey entity = new UserKey();
        UserKey entity = userKeyDao.findByMobileAndEquipmentCode(mobile,equitCode);
        if (entity == null) {
            entity=new UserKey();
        }
        entity.setCaEndTime(DataUtil.strToDate(DataUtil.dateAddOneYearToStr(new Date())));
        entity.setCaStartTime(new Date());
        entity.setCreateTime(new Date());
        entity.setCreateId(userId);
        entity.setEquipmentCode(equitCode);
        entity.setPublicKey(publicKey);
        entity.setStatus(0);
        entity.setMobile(mobile);
        entity.setUserCa(ca);
        entity.setUserId(userId);
        userKeyDao.save(entity);
    }

    private RespDataVo returnMsgDealWith(String msg, String status, String data, String key){
        RespDataVo vo = new RespDataVo();
        vo.setMsg(msg);
        vo.setStatus(status);
        vo.setData(data);
        vo.setKey(key);
        return vo;
    }

}
