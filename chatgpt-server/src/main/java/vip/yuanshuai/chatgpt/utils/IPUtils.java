package vip.yuanshuai.chatgpt.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: chatgpt
 * @description: IPUtils
 * @author: yuanshuai
 * @create: 2023-07-03 12:25
 **/
public class IPUtils {

    /**
     * 获取客户端IP
     *
     * @param request 请求
     * @return {@link String}
     */
    public static String getClientIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

}
