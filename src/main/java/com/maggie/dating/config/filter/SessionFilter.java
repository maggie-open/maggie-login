package com.maggie.dating.config.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.maggie.dating.common.constants.Constants;
import com.maggie.dating.common.util.DataUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


//@Component
//@ConfigurationProperties(prefix = "web.auth")
//@WebFilter(urlPatterns = "/*",filterName = "SessionFilter")
public class SessionFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    private static List<String> notAuthUrl ;

    private static String sessionInvalid;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        String url = req.getRequestURI();
        if(isNotAuthUrl(url)){
            chain.doFilter(request, response);
            return;
        }
        String loginPath = req.getContextPath() + "/"+sessionInvalid;
        String userId = DataUtil.getString(session.getAttribute(Constants.SESSION_USERID));
        if(userId == null){
            req.getRequestDispatcher(loginPath).forward(request,response);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * 判断是否不需要认证页面
     * @param url
     * @return
     */
    private boolean isNotAuthUrl(String url){
        for(String str:notAuthUrl){
            if(url.contains(str))
                return true;
        }
        return false;
    }

    @Override
    public void destroy() {

    }

    public List<String> getNotAuthUrl() {
        return notAuthUrl;
    }

    public void setNotAuthUrl(List<String> notAuthUrl) {
        this.notAuthUrl = notAuthUrl;
    }

    public String getSessionInvalid() {
        return sessionInvalid;
    }

    public void setSessionInvalid(String sessionInvalid) {
        this.sessionInvalid = sessionInvalid;
    }
}
