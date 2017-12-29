package com.maggie.dating.common.util;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    public  static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String  postHttp(String url, Map<String,String> map){
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String respStr = null;
        HttpPost post = new HttpPost(url);
        List<NameValuePair> params = Lists.newArrayList();
        if(map!=null&&map.keySet().size()>0){
            for (String key: map.keySet()) {
                params.add(new BasicNameValuePair(key, map.get(key)));
            }
        }
        post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
        HttpEntity httpEntity = null;
        try {
            response = closeableHttpClient.execute(post);
            httpEntity= response.getEntity();
            respStr = EntityUtils.toString(httpEntity, Consts.UTF_8);
            EntityUtils.consume(httpEntity);
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            if(httpEntity!=null){
                try {
                    EntityUtils.consume(httpEntity);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        return respStr;
    }

}
