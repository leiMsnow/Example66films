package com.shuyu.core.proxy;

/**
 * Config
 *
 * @author hellogv
 */
public class Config {

    public final static String LOCAL_IP_ADDRESS = "127.0.0.1";
    public final static int HTTP_PORT = 80;
    public final static String HTTP_BODY_END = "\r\n\r\n";
    public final static String HTTP_RESPONSE_BEGIN = "HTTP/";
    public final static String HTTP_REQUEST_BEGIN = "GET ";


    public static class ProxyRequest {
        /**
         * Http Request 内容
         */
        public String body;
        /**
         * Range的位置
         */
        public long rangePosition;
    }

    public static class ProxyResponse {
        public byte[] body;
        public byte[] other;
        public long currentPosition;
        public long duration;
    }
}