package com.maggie.dating.common.constants;

public class Constants {

    public final static String SESSION_MOBILE = "mobile";
    public final static String SESSION_REGCODE = "regCode";
    public final static String SESSION_LOGINCODE = "loginCode";
    public final static String SESSION_LOGINCERT = "loginCert";
    public final static String SESSION_CUSTTYPE = "custType";
    public final static String SESSION_EQUITCODE = "equitCode";
    public final static String SESSION_USERID = "userId";
    public final static int REGIST_CODE = 4;

    public static final int SESSION_REGCODE_COUNT = 3000;
    public static final String SESSION_VALIDCOUNT = "validCodeCount";
    public static final String SESSION_VALIDSTATUS = "sessionValidStatus";
    public static final String SESSION_USERTYPE = "userType";
    public static final long SESSION_REGCODE_TIME = 20;



    //redis--->key
    /**
     * 卖家地理位置list
     */
    public static final String REDIS_SELLER_GEO = "sellersGeoList";

    /**
     * 买家的卖家浏览临时list
     */
    public static final String REDIS_BUYER_TEMP_SELLERS = "tempsellerslist";


    /**
     * 买家的卖家距离
     */
    public static final String REDIS_BUYER_SELLER_DIST = "dist";

    /**
     * 买家浏览过的卖家
     */
    public static final String REDIS_BUYER_SELLER_PASSED = "passedSellers";

    /**
     * 抢单订单 可推送卖家
     */
    public static final String REDIS_ORDER_CANDIDATE_SELLERS = "orderCandidateList";


    public static final String REDIS_SEND_ORDER_LIST = "sendOrderList";

    public static final String REDIS_SEND_ORDER_INFO_LIST = "sendOrderInfoList";

    public static final String REDIS_BUYER_SCAN_AGE = "18:120";
    public static final long REDIS_SELLER_PASSED_MINUTE = 1;
}
