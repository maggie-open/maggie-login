package com.maggie.dating.services.core;

import java.util.List;
import java.util.Map;

/**
 * 推送消息服务
 */
public interface BizMessageNotifyService {

    //通知卖家 买家下单约会
    void notifySellerHasDating(String sellerId,Map<String,Object> orderInfo);

    /**
     * 通知买家 卖家抢单
     * @param buyerId
     * @param orderInfo
     */
    void notifyBuyerHasChosen(String buyerId, Map<String, Object> orderInfo);

    /**
     * 通知卖家 有单可抢
     * @param sellerId
     * @param orderInfo
     */
    void notifySellerHasChosen(String sellerId, Map<String, Object> orderInfo);

    /**
     * 通知卖家 买家确定抢单成功
     * @param sellerId
     * @param orderInfo
     */
    void notifySellerHasChosenSuccess(String sellerId, Map<String, Object> orderInfo);

    /**
     * 通知卖家 买家确定抢单失败
     * @param sellerId
     * @param orderInfo
     */
    void notifySellerHasChosenFail(String sellerId, Map<String, Object> orderInfo);

    /**
     * 通知相关卖家 买家确定抢单失败
     * @param sellerIds
     * @param orderInfo
     */
    void notifySellerHasChosenFail(List<String> sellerIds, Map<String, Object> orderInfo);
}
