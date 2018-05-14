package com.maggie.dating.services.impls.core;

import com.maggie.dating.beans.*;
import com.maggie.dating.beans.vos.SellerDistVo;
import com.maggie.dating.common.constants.Constants;
import com.maggie.dating.common.constants.OrderSellerPushConstants;
import com.maggie.dating.common.enums.AccountActionTypeEnum;
import com.maggie.dating.common.enums.OrderPushStatusEnum;
import com.maggie.dating.common.enums.TxStatusEnum;
import com.maggie.dating.common.util.DataUtil;
import com.maggie.dating.common.util.GeoUtil;
import com.maggie.dating.common.util.JsonUtil;
import com.maggie.dating.daos.mongo.BuyerDao;
import com.maggie.dating.daos.mongo.SellerDao;
import com.maggie.dating.daos.mysql.*;
import com.maggie.dating.daos.mysql.basic.BasicDao;
import com.maggie.dating.daos.mysql.basic.PageData;
import com.maggie.dating.daos.mysql.basic.PageDataUtil;
import com.maggie.dating.services.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.*;

@Component
public class OrderServiceImpl implements OrderService{
    public final static Logger logger = LoggerFactory.getLogger(OrderService.class);


    @Autowired
    private BasicDao basicDao;

    @Autowired
    private SellerDao sellerDao;

    @Autowired
    private BuyerDao buyerDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderTransactionDao orderTransactionDao;

    @Autowired
    private OrderTransactionRecordDao orderTransactionRecordDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BizMessageNotifyService messageNotifyService;

    @Override
    public PageData findAllOrderByBuyerId(String buyerId, String pageNum, String txStatus) {
        PageData pg = new PageData();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t1.id as order_id, t1.buyer_id, t1.message, t1.place,");
        sql.append("t1.date_time, t1.expiration, t1.geo, t1.is_anonymous,");
        sql.append("date_format(t1.order_time,'%Y-%m-%d %H:%i:%s'), t1.subject, t2.seller_id,  t2.amount,");
        sql.append("t2.status,t2.id as tx_id FROM t_user_order t1 ");
        sql.append("left join t_order_transaction t2 on t2.order_id = t1.id ");

        sql.append("where t2.buyer_id = '"+buyerId+"' ");
        if(DataUtil.isNotEmpty(txStatus)){
            sql.append("and t2.status = '"+txStatus+"' ");
        }
        sql.append(" order by t1.order_time desc");
        PageData orders = basicDao.findPageBySql(sql.toString(),10,Integer.valueOf(pageNum));

        String[] keys = new String[]{"orderId","buyerId","message","place"
                                    ,"dateTime","expiration","geo","isAnonymous","orderTime",
                                    "subject","sellerId","amount","status","txId"};
        int page = orders.getPage();//当前页
        long totalPage = orders.getTotal();//一共多少
        long pageCount = orders.getPageCount();
        List<Map<String,Object>> orderList = PageDataUtil.convertPageDateResult(orders.getResult(),keys);
        if(orderList!=null&&orderList.size()>0){
            //循环遍历添加买家卖家信息
            for (Map<String,Object> map:orderList
                 ) {
                //获取卖家信息
                String sellerId = DataUtil.getString(map.get("sellerId"));
                if(DataUtil.isNotEmpty(sellerId)){
                    map.put("seller",sellerDao.findOne(sellerId));
                }
            }
        }
        pg.setPage(page);
        pg.setPageSize(10);
        pg.setTotal(totalPage);
        pg.setPageCount(pageCount);
        pg.setResult(orderList);
        return pg;
    }

    @Override
    public PageData findAllOrderBySellerId(String sellerId, String pageNum, String txStatus) {
        PageData pg = new PageData();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t1.id as order_id, t1.buyer_id, t1.message, t1.place,");
        sql.append("t1.date_time, t1.expiration, t1.geo, t1.is_anonymous,");
        sql.append("date_format(t1.order_time,'%Y-%m-%d %H:%i:%s'), t1.subject, t2.seller_id,  t2.amount,");
        sql.append("t2.status,t2.id as tx_id FROM t_user_order t1 ");
        sql.append("left join t_order_transaction t2 on t2.order_id = t1.id ");

        sql.append("where t2.seller_Id = '"+sellerId+"' ");
        if(DataUtil.isNotEmpty(txStatus)){
            sql.append("and t2.status = '"+txStatus+"' ");
        }
        sql.append(" order by t1.order_time desc");

        PageData orders = basicDao.findPageBySql(sql.toString(),10,Integer.valueOf(pageNum));

        String[] keys = new String[]{"orderId","buyerId","message","place"
                ,"dateTime","expiration","geo","isAnonymous","orderTime",
                "subject","sellerId","amount","status","txId"};
        int page = orders.getPage();//当前页
        long totalPage = orders.getTotal();//一共多少页
        List<Map<String,Object>> orderList = PageDataUtil.convertPageDateResult(orders.getResult(),keys);
        if(orderList!=null&&orderList.size()>0){
            //循环遍历添加买家卖家信息
            for (Map<String,Object> map:orderList
                    ) {
                //获取卖家信息
                String buyerId = DataUtil.getString(map.get("buyerId"));
                if(DataUtil.isNotEmpty(buyerId)){
                    Buyer buyer = buyerDao.findOne(buyerId);
                    buyer.filterBuyerInfo();
                    map.put("buyer",buyer);
                }
            }
        }
        pg.setPage(page);
        pg.setPageSize(10);
        pg.setTotal(totalPage);
        pg.setResult(orderList);
        return pg;
    }



    @Override
    public void dealWithOrder(OrderTransaction otx) {
        //判断订单类型
        if(otx!=null){
            String buyerId = otx.getBuyerId();
            Long orderId = otx.getOrderId();
            String sellerId = otx.getSellerId();
            String status = otx.getSTATUS();
            if(DataUtil.isNotEmpty(sellerId)){
                //定向单
                Map<String,Object> orderInfo = this.findOrderVoByOrderId(otx.getOrderId());
                messageNotifyService.notifySellerHasDating(sellerId,orderInfo);
            }else{
                //抢单
                this.dealWithNoChosenOrder(buyerId,status,orderId);
            }
        }else{
            logger.info("无效订单状态");
        }
    }


    public void dealWithNoChosenOrder(String buyerId,String status,Long orderId ){
        Order order = orderDao.findOne(orderId);
        String geoStr = order.getGeo();
        Geo geo = (Geo) JsonUtil.jsonStrToBean(geoStr,Geo.class);

        //买家黑名单
        Buyer buyer = buyerDao.findOne(buyerId);
        List<String> blackList = buyer.getBlackList();
        if(blackList==null) blackList = new ArrayList<String>();
        //符合订单GEO条件的 卖家
        if(geo==null) geo = buyer.getLastGeo();
        String dist = buyer.getScanDistance();
        if(DataUtil.isEmpty(dist)){
            dist = OrderSellerPushConstants.BUYER_SCAN_DIST;
        }
        List<SellerDistVo> sellers = redisService.getAllSellersByGeoAndDist(geo,dist);
        List<String> sellersWhite = null;
        if(sellers!=null){
            //将符合条件的卖家 黑名单 存入redis
            sellersWhite = redisService.filterWhiteSellerDist(sellers,blackList,String.valueOf(orderId));
            //获取所有验证通过的validSellers
            List<Seller> validSellers = sellerDao.findAllByChosenOrderCondition(sellersWhite);
            //查出所有的卖家存入redis,等待推送
            redisService.makeOrderCandidateList(buyerId,orderId,validSellers);
            //推送卖家抢单
            this.sendOrderToSellers();
        }
    }

    //定时扫描订单推送情况,未确认的订单，继续推送出栈卖家
    @Override
    public void sendOrderToSellers() {

        Set<String> sendOrderSellers =  redisService.makeOrderToSeller();
        for (String sendStr: sendOrderSellers
             ) {
            String[] keys = sendStr.split(":");
            String sellerId = keys[0];
            String orderId = keys[1];
            //this.findOrderVoByOrderId(DataUtil.getLong(orderId));
            //组装详细信息
            Map<String,Object> orderInfo = this.componentChosenOrderInfo(orderId,sellerId,OrderPushStatusEnum.REMAIN_CHOOSE.getCode());
            messageNotifyService.notifySellerHasChosen(sellerId,orderInfo);
        }
    }


    @Override
    public Map<String, Object> findOrderVoByOrderId(Long orderId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t1.id as order_id, t1.buyer_id, t1.message, t1.place,");
        sql.append("t1.date_time, t1.expiration, t1.geo, t1.is_anonymous,");
        sql.append("date_format(t1.order_time,'%Y-%m-%d %H:%i:%s'), t1.subject, t2.seller_id,  t2.amount,");
        sql.append("t2.status,t2.id as tx_id FROM t_user_order t1 ");
        sql.append("left join t_order_transaction t2 on t2.order_id = t1.id ");

        sql.append("where t1.id ="+orderId+" ");

        List order =  basicDao.findBySql(sql.toString());

        String[] keys = new String[]{"orderId","buyerId","message","place"
                ,"dateTime","expiration","geo","isAnonymous","orderTime",
                "subject","sellerId","amount","status","txId"};

        List<Map<String,Object>> orderInfo = PageDataUtil.convertPageDateResult(order,keys);

        Map<String,Object> orderMap = orderInfo!=null?orderInfo.get(0):null;
        if(orderMap!=null){
            String sellerId = DataUtil.getString(orderMap.get("sellerId"));
            Seller seller = null;
            if(DataUtil.isNotEmpty(sellerId)){
                seller = sellerDao.findOne(sellerId);
            }
            orderMap.put("seller",seller);
            orderMap.put("buyer",buyerDao.findOne(DataUtil.getString(orderMap.get("buyerId"))));
        }

        return orderMap;
    }

    public Map<String,Object> componentChosenOrderInfo(String orderIdStr,String sellerId,String statusStr){
        Order order = orderDao.findOne(DataUtil.getLong(orderIdStr));
        OrderTransaction orderTx = orderTransactionDao.findByOrderId(order.getId());
        Buyer buyer = buyerDao.findOne(order.getBuyerId());
        Seller seller = sellerDao.findOne(sellerId);
        Geo sellerGeo = seller.getLastGeo();
        //计算获取的订单地理位置
        String geoInfo = order.getGeo();
        Geo geo  = new Geo();
        if(DataUtil.isNotEmpty(geoInfo)){
            geo = (Geo) JsonUtil.jsonStrToBean(geoInfo,Geo.class);
        }else{
            geo = buyer.getLastGeo();
        }
        double dist =  GeoUtil.Distance(DataUtil.getDouble(sellerGeo.getLongitude()),DataUtil.getDouble(sellerGeo.getLatitude()),
                DataUtil.getDouble(geo.getLongitude()),DataUtil.getDouble(geo.getLatitude()));
        if(dist>0){
            dist = dist/1000;
        }

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("order",order);
        map.put("amount",DataUtil.getString(orderTx.getAmount()));
        map.put("orderStatus",DataUtil.getString(orderTx.getSTATUS()));
        //考虑是否过滤
        buyer.filterBuyerInfo();
        map.put("buyer",buyer);
        map.put("status",statusStr);
        map.put("dist",DataUtil.getString(dist));
        return map;
    }


    @Override
    public Map<String, Object> findChosenOrderByBuyerId(String userId) {
        //order sellers{seller,dist}
        Map<String,Object> map = new HashMap<String,Object>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t1.id as order_id, t1.buyer_id, t1.message, t1.place,");
        sql.append("t1.date_time, t1.expiration, t1.geo, t1.is_anonymous,");
        sql.append("t1.order_time, t1.subject, t2.seller_id,  t2.amount,");
        sql.append("t2.status,t2.id as tx_id FROM t_user_order t1 ");
        sql.append("left join t_order_transaction t2 on t2.order_id = t1.id ");

        sql.append("where t1.buyer_id = '"+userId+"' and t2.seller_Id is null and  t2.status = '"+TxStatusEnum.SELLER_CHOOSE.getCode()+"'");

        List<Map<String,Object>> orders = basicDao.findBySql(sql.toString());

        String[] keys = new String[]{"orderId","buyerId","message","place"
                ,"dateTime","expiration","geo","isAnonymous","orderTime",
                "subject","sellerId","amount","status","txId"};
        List<Map<String,Object>> orderList = PageDataUtil.convertPageDateResult(orders,keys);
        Map<String,Object> order = (orderList!=null&&orderList.size()>0)?orderList.get(0):null;
        //获取详细的订单抢单信息,查询redis中的订单抢单信息
        if(order!=null){
            String orderId = DataUtil.getString(order.get("orderId"));
            String buyerId = DataUtil.getString(order.get("buyerId"));
            String geo = DataUtil.getString(order.get("geo"));//订单地址
            Buyer buyer = buyerDao.findOne(buyerId);
            Geo buyerGeo = buyer.getLastGeo();
            String latitud = buyerGeo.getLatitude();
            String longitude =buyerGeo.getLongitude();
            //查询所有抢单中的卖家 orderId:status:sellerId
            List<String> orderSellerStr =  redisService.getAllChosenOrderSellerByOrderId(orderId);
            //解析数据:组装抢单卖家
            List<Map<String,Object>> sellerList = new ArrayList<Map<String,Object>>();
            for (String os:orderSellerStr
                 ) {
                String[] componentStr = os.split(":");
                String orderIdStr = componentStr[0];
                String statusStr = componentStr[1];
                String sellerIdStr = componentStr[2];
                //component vo
                Seller seller = sellerDao.findOne(sellerIdStr);
                if(seller!=null){
                    Map<String,Object> sellerMap = new HashMap<String,Object>();
                    sellerMap.put("seller",seller);
                    sellerMap.put("status" ,statusStr);
                    //计算距离
                    Geo sellerGeo = seller.getLastGeo();
                    if(sellerGeo!=null){
                       double dist =  GeoUtil.Distance(DataUtil.getDouble(sellerGeo.getLongitude()),DataUtil.getDouble(sellerGeo.getLatitude()),
                                DataUtil.getDouble(longitude),DataUtil.getDouble(latitud));
                       if(dist>=-1){
                           dist = dist/1000;
                           sellerMap.put("dist",DataUtil.getString(dist));
                       }
                    }
                    sellerList.add(sellerMap);
                }
            }
            map.put("order",order);
            map.put("sellers",sellerList);
        }
        return map;
    }


    @Override
    public List<Map<String, Object>> findChosenOrdersBySellerId(String userId) {
        //缓存中获取已推送列表sellerId:orderId:status
        List<String> sellerOrderStr =  redisService.getAllChosenOrderBySellerId(userId);
        List<Map<String,Object>> orders = new ArrayList<Map<String,Object>>();
        Seller seller = sellerDao.findOne(userId);
        Geo sellerGeo = seller.getLastGeo();
        for (String str:sellerOrderStr
             ) {
            String[] componentStr = str.split(":");
            String sellerIdStr = componentStr[0];
            String orderIdStr = componentStr[1];
            String statusStr = componentStr[2];
            //组装order+买家信息+status+dist(订单地址)
            Order order = orderDao.findOne(DataUtil.getLong(orderIdStr));
            OrderTransaction orderTx = orderTransactionDao.findByOrderId(order.getId());
            Buyer buyer = buyerDao.findOne(order.getBuyerId());
            //计算获取的订单地理位置
            String geoInfo = order.getGeo();
            Geo geo  = new Geo();
            if(DataUtil.isNotEmpty(geoInfo)){
                geo = (Geo) JsonUtil.jsonStrToBean(geoInfo,Geo.class);
            }else{
                geo = buyer.getLastGeo();
            }
            double dist =  GeoUtil.Distance(DataUtil.getDouble(sellerGeo.getLongitude()),DataUtil.getDouble(sellerGeo.getLatitude()),
                    DataUtil.getDouble(geo.getLongitude()),DataUtil.getDouble(geo.getLatitude()));
            if(dist>0){
                dist = dist/1000;
            }

            Map<String,Object> map = new HashMap<String,Object>();
            map.put("order",order);
            map.put("amount",DataUtil.getString(orderTx.getAmount()));
            map.put("orderStatus",DataUtil.getString(orderTx.getSTATUS()));
            //考虑是否过滤
            buyer.filterBuyerInfo();
            map.put("buyer",buyer);
            map.put("status",statusStr);
            map.put("dist",DataUtil.getString(dist));
            orders.add(map);
        }
        return orders;
    }

    @Override
    public String sellerChosenOrder(String userId, long orderId) {
        //操作redis
        String status = redisService.sellerChosenOrder(userId,orderId);
        Map<String,Object> orderInfo = this.findOrderVoByOrderId(DataUtil.getLong(orderId));
        String buyerId = DataUtil.getString(orderInfo.get("buyerId"));

        List<Map<String,Object>> sellerList = new ArrayList<Map<String,Object>>();
        Seller seller = sellerDao.findOne(userId);
        if(seller!=null){
            Buyer buyer = buyerDao.findOne(buyerId);
            Geo buyerGeo = buyer.getLastGeo();
            String latitud = buyerGeo.getLatitude();
            String longitude =buyerGeo.getLongitude();
            Map<String,Object> sellerMap = new HashMap<String,Object>();
            sellerMap.put("seller",seller);
            sellerMap.put("status" ,status);
            //计算距离
            Geo sellerGeo = seller.getLastGeo();
            if(sellerGeo!=null){
                double dist =  GeoUtil.Distance(DataUtil.getDouble(sellerGeo.getLongitude()),DataUtil.getDouble(sellerGeo.getLatitude()),
                            DataUtil.getDouble(longitude),DataUtil.getDouble(latitud));
                    if(dist>=-1){
                        dist = dist/1000;
                        sellerMap.put("dist",DataUtil.getString(dist));
                    }
                }
                sellerList.add(sellerMap);
            }
        orderInfo.put("sellers",sellerList);
        messageNotifyService.notifyBuyerHasChosen(buyerId,orderInfo);
        return status;
    }

    @Override
    public void updateOrderOperateStatus(String txId, String code, String userId) {
        //更新状态
        OrderTransaction otx = orderTransactionDao.findOne(DataUtil.getLong(txId));
        Order order = orderDao.findOne(otx.getOrderId());
        //验证userId
        String buyerId = otx.getBuyerId();
        String sellerId = otx.getSellerId();
        Date nowDate = new Date();
        if(userId.equalsIgnoreCase(buyerId)||userId.equalsIgnoreCase(sellerId)){
            //是订单所有者
            otx.setSTATUS(code);
            //生成订单操作记录
            OrderTransactionRecord otr = new OrderTransactionRecord();
            otr.setNewStatus(code);
            otr.setRecordTime(nowDate);
            otr.setTxId(otx.getId());
            //更新
            orderTransactionDao.save(otx);
            orderTransactionRecordDao.save(otr);
        }

    }

    @Override
    public void confirmChosenSeller(String userId, String sellerId) {
        //查找当前买家的抢单订单
        //更新sellerId = sellerId
        //生成状态等待卖家确认--待卖家抢单--》待卖家确认
        //redis中删除该卖家 该订单状态 --》其余置为已失效
        List<OrderTransaction> otxs = orderTransactionDao.findChosenOrderByBuyerId(userId);
        if(otxs!=null&&otxs.size()>0){
            //获取到交易记录
            Date nowDate = new Date();
            OrderTransaction otx = otxs.get(0);
            otx.setSTATUS(TxStatusEnum.SELLER_CONFIRM.getCode());
            otx.setSellerId(sellerId);
            orderTransactionDao.save(otx);
            //生成订单操作记录
            OrderTransactionRecord otr = new OrderTransactionRecord();
            otr.setNewStatus(TxStatusEnum.SELLER_CONFIRM.getCode());
            otr.setRecordTime(nowDate);
            otr.setTxId(otx.getId());
            orderTransactionRecordDao.save(otr);
            //操作redis标记状态
            List<String> failSeller =  redisService.confirmChosenSeller(otx.getOrderId(),sellerId,userId);
            Map<String,Object> orderInfo = new HashMap<String,Object>();
            orderInfo.put("orderId",otx.getOrderId());
            orderInfo.put("status",OrderPushStatusEnum.ORDER_PUSH_CLOSE.getCode());
            messageNotifyService.notifySellerHasChosenSuccess(sellerId,orderInfo);
            Map<String,Object> orderInfoFail = new HashMap<String,Object>();
            orderInfoFail.put("orderId",otx.getOrderId());
            orderInfoFail.put("status",OrderPushStatusEnum.ORDER_EXP.getCode());
            messageNotifyService.notifySellerHasChosenFial(failSeller,orderInfoFail);
        }

    }

    @Override
    public void removeChosenSeller(String userId, String sellerId) {
        //查找当前买家的抢单订单
        //redis中删除该卖家 该订单状态 --》关闭
        List<OrderTransaction> otxs = orderTransactionDao.findChosenOrderByBuyerId(userId);
        if(otxs!=null&&otxs.size()>0){
            //获取到交易记录
            Date nowDate = new Date();
            OrderTransaction otx = otxs.get(0);
            //操作redis标记状态
            redisService.removeChosenSeller(otx.getOrderId(),sellerId,userId);
            Map<String,Object> orderInfo = new HashMap<String,Object>();
            orderInfo.put("orderId",otx.getOrderId());
            orderInfo.put("status",OrderPushStatusEnum.ORDER_PUSH_CLOSE.getCode());
            messageNotifyService.notifySellerHasChosenFial(sellerId,orderInfo);
        }
    }
}
