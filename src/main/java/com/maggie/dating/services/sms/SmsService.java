package com.maggie.dating.services.sms;

import java.util.List;
import java.util.Map;

/**
 * 短信服务short message service
 */
public interface SmsService {

    int sendSms(String mobile,Map<String,String> vals,String model);

    int sendSms(List<String> mobiles,Map<String,String> vals,String model);

    int sendValidCode(String mobile,String code);

}
