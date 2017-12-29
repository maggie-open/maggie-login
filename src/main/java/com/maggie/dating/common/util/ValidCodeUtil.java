package com.maggie.dating.common.util;

import java.util.Random;

public class ValidCodeUtil {

    /**
     * 随机生成n位的验证码
     * @param n
     * @return
     */
    public static String getRandomStr(int n) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        String chars = "23456789abcdefghjkmnpqrstuvwxyz";
        chars = "11111111111111111111";
        int len = chars.length();
        for (int i = 0; i < n; i++) {
            String temp = String.valueOf(chars.charAt(r.nextInt(len)));
            sb.append(temp);
        }
        return sb.toString();
    }

}
