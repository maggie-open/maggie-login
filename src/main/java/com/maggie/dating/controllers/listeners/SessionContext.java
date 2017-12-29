package com.maggie.dating.controllers.listeners;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * session处理
 */
public class SessionContext {

    private static HashMap sessionMap = new HashMap();

    public static synchronized void AddSession(HttpSession session) {
        if (session != null) {
            sessionMap.put(session.getId(), session);
        }
    }

    public static synchronized void DelSession(HttpSession session) {
        if (session != null) {
            sessionMap.remove(session.getId());
        }
    }

    public static synchronized HttpSession getSession(String session_id) {
        if (session_id == null)
            return null;
        return (HttpSession) sessionMap.get(session_id);
    }


    public static synchronized void SetSession(HttpSession session) {
        if (session != null) {
            sessionMap.put("123456789", session);
        }
    }

}
