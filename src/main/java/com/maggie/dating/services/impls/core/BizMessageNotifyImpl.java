package com.maggie.dating.services.impls.core;

import com.maggie.dating.beans.vos.WssDataVo;
import com.maggie.dating.common.enums.WmsTypeEnum;
import com.maggie.dating.common.util.JsonUtil;
import com.maggie.dating.services.core.BizMessageNotifyService;
import com.maggie.dating.services.core.MessagePushService;
import com.maggie.dating.services.core.OrderService;
import com.maggie.dating.services.core.SellerService;
import net.sf.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BizMessageNotifyImpl implements BizMessageNotifyService{

    public final static Logger logger = LoggerFactory.getLogger(BizMessageNotifyService.class);


    @Autowired
    private MessagePushService messagePushService;

    @Override
    public void notifySellerHasDating(String sellerId,Map<String,Object> orderInfo) {
        try {
            //component sms
            WssDataVo wssDataVo = new WssDataVo();
            wssDataVo.setWmsType(WmsTypeEnum.ORDER.getCode());
            wssDataVo.setMsg("您有新的约会！");
            //查询订单信息
//            Map<String,Object> order = orderService.findOrderVoByOrderId(orderId);
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("order", JsonUtil.beanToJsonStr(orderInfo));
            wssDataVo.setData(JsonUtil.mapToJson(data));
            String wssStr = JsonUtil.beanToJsonStr(wssDataVo);
            messagePushService.sendMessageToUser(sellerId,wssStr,wssDataVo.getMsg());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    @Override
    public void notifyBuyerHasChosen(String buyerId, Map<String, Object> orderInfo) {
        try {
            //component sms
            WssDataVo wssDataVo = new WssDataVo();
            wssDataVo.setWmsType(WmsTypeEnum.CHODEN.getCode());
            wssDataVo.setMsg("您的单有人抢单！");
            //查询订单信息
//            Map<String,Object> order = orderService.findOrderVoByOrderId(orderId);
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("order", JsonUtil.beanToJsonStr(orderInfo));
            wssDataVo.setData(JsonUtil.mapToJson(data));
            String wssStr = JsonUtil.beanToJsonStr(wssDataVo);
            messagePushService.sendMessageToUser(buyerId,wssStr,wssDataVo.getMsg());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    @Override
    public void notifySellerHasChosen(String sellerId, Map<String, Object> orderInfo) {
        try {
            //component sms
            WssDataVo wssDataVo = new WssDataVo();
            wssDataVo.setWmsType(WmsTypeEnum.TO_CHOOSE.getCode());
            wssDataVo.setMsg("有土豪发单啦！");
            //查询订单信息
//            Map<String,Object> order = orderService.findOrderVoByOrderId(orderId);
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("order", JsonUtil.beanToJsonStr(orderInfo));
            wssDataVo.setData(JsonUtil.mapToJson(data));
            String wssStr = JsonUtil.beanToJsonStr(wssDataVo);
            messagePushService.sendMessageToUser(sellerId,wssStr,wssDataVo.getMsg());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }


    @Override
    public void notifySellerHasChosenSuccess(String sellerId, Map<String, Object> orderInfo) {
        try {
            //component sms
            WssDataVo wssDataVo = new WssDataVo();
            wssDataVo.setWmsType(WmsTypeEnum.CHODEN_SUCCESS.getCode());
            wssDataVo.setMsg("土豪选中了你！");
            //查询订单信息
//            Map<String,Object> order = orderService.findOrderVoByOrderId(orderId);
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("order", JsonUtil.beanToJsonStr(orderInfo));
            wssDataVo.setData(JsonUtil.mapToJson(data));
            String wssStr = JsonUtil.beanToJsonStr(wssDataVo);
            messagePushService.sendMessageToUser(sellerId,wssStr,wssDataVo.getMsg());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    @Override
    public void notifySellerHasChosenFail(String sellerId, Map<String, Object> orderInfo) {
        try {
            //component sms
            WssDataVo wssDataVo = new WssDataVo();
            wssDataVo.setWmsType(WmsTypeEnum.CHODEN_FAIL.getCode());
            wssDataVo.setMsg("土豪撇了你一眼，就过去了！");
            //查询订单信息
//            Map<String,Object> order = orderService.findOrderVoByOrderId(orderId);
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("order", JsonUtil.beanToJsonStr(orderInfo));
            wssDataVo.setData(JsonUtil.mapToJson(data));
            String wssStr = JsonUtil.beanToJsonStr(wssDataVo);
            messagePushService.sendMessageToUser(sellerId,wssStr,wssDataVo.getMsg());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }


    @Override
    public void notifySellerHasChosenFail(List<String> sellerIds, Map<String, Object> orderInfo) {
        try {
            //component sms
            WssDataVo wssDataVo = new WssDataVo();
            wssDataVo.setWmsType(WmsTypeEnum.CHODEN_FAIL.getCode());
            wssDataVo.setMsg("土豪撇了你一眼，就过去了！");
            //查询订单信息
//            Map<String,Object> order = orderService.findOrderVoByOrderId(orderId);
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("order", JsonUtil.beanToJsonStr(orderInfo));
            wssDataVo.setData(JsonUtil.mapToJson(data));
            String wssStr = JsonUtil.beanToJsonStr(wssDataVo);
            messagePushService.sendMessageToUsers(sellerIds,wssStr,wssDataVo.getMsg(),false);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
