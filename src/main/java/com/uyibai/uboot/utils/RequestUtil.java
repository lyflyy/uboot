package com.uyibai.uboot.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestUtil {


    public RequestUtil() {
    }

    public static HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    public static HttpServletResponse getResponse() {
        HttpServletResponse response = ((ServletWebRequest) RequestContextHolder.getRequestAttributes()).getResponse();
        return response;
    }

    public static boolean isAjaxRequest() {
        return isAjaxRequest(getRequest());
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String contextType = request.getContentType();
        String xr = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(xr) || MediaType.valueOf(contextType).equals(MediaType.APPLICATION_JSON);
    }

    public static String getUserAgent() {
        return getRequest().getHeader("User-Agent");
    }

    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            return index != -1 ? ip.substring(0, index) : ip;
        } else {
            ip = request.getHeader("X-Real-IP");
            return StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip) ? ip : request.getRemoteAddr();
        }
    }

    public static String getClientIP() {
        return getClientIP(getRequest());
    }


    public static String getServerName(HttpServletRequest request) {
        return request.getServerName();
    }

    public static boolean isIpString(String ipStr) {
        String regex = "(((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(ipStr);
        return m.matches();
    }
}
