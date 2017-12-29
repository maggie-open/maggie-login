package com.maggie.dating.services.core;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.List;

/**
 * webSocket服务
 */
public interface MessagePushService {
    void sendMessageToUsers(List<String> userIds, String messageJson,String tip, boolean isAll);

    void sendMessageToUser(String userId, String messageJson,String tip);
}
