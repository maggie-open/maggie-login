package com.maggie.dating.services.impls.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.maggie.dating.common.util.JsonUtil;
import com.maggie.dating.services.sms.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@PropertySource({"classpath:config/sms.properties"})
public class SmsServiceImpl implements SmsService {
    private final static Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);
    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";
    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    @Value("${accessKeyId}")
    private String accessKeyId;
    @Value("${accessKeySecret}")
    private String accessKeySecret;
    @Value("${signName}")
    private String signName;
    @Value("${defaultConnectTimeout}")
    private String defaultConnectTimeout;
    @Value("${defaultReadTimeout}")
    private String defaultReadTimeout;

    @Value("${SMSTemplateCode_validCode}")
    private String SMSTemplateCode_validCode;


    @Override
    public int sendSms(String mobile, Map<String, String> vals, String model) {
        return 0;
    }

    @Override
    public int sendSms(List<String> mobiles, Map<String, String> vals, String model) {
        return 0;
    }

    @Override
    public int sendValidCode(String mobile, String code) {
        Map<String, String> map = new HashMap<String, String>();
        SendSmsResponse smb = null;
        Integer results = 0;
        try {
            map.put("code", code);
            smb = sendSms(mobile, SMSTemplateCode_validCode, JsonUtil.mapToJson(map));
            System.out.println("短信接口返回的数据----------------");
            System.out.println("Code=" + smb.getCode());
            System.out.println("Message=" + smb.getMessage());
            System.out.println("RequestId=" + smb.getRequestId());
            System.out.println("BizId=" + smb.getBizId());
            if (smb.getCode().equals("OK")) {
                return 0;
            } else {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("验证码发送发生异常，异常信息：" + e.getMessage());
            return 1;
        }
    }

    public SendSmsResponse sendSms(String mobile, String smsTemplateCode, String msgJsonStr) throws Exception {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", defaultConnectTimeout);
        System.setProperty("sun.net.client.defaultReadTimeout", defaultReadTimeout);
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(mobile);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(smsTemplateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(msgJsonStr);
        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");
        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        return sendSmsResponse;
    }
}
