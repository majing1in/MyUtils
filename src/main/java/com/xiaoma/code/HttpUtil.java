package com.xiaoma.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @Author: majing1in
 * @Date: 2022/07/06 20:24
 * @Email: 2533144458@qq.com
 * @Description: HTTP请求工具类
 */
public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    static {
        try {
            X509TrustManager[] x509TrustManager = {new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, x509TrustManager, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((urlHostName, session) -> true);
        } catch (Exception e) {
            log.error("初始化HTTPS配置失败!");
        }
    }

    /**
     * GET请求方式
     *
     * @param path    请求路径
     * @param params  请求参数
     * @param headers 请求头
     */
    public static String doGet(String path, Map<String, String> params, Map<String, String> headers) {
        return doGet(path, null, null, params, headers);
    }

    /**
     * GET请求方式(代理)
     *
     * @param path      请求路径
     * @param proxyIp   代理地址
     * @param proxyPort 代理端口
     * @param params    请求参数
     * @param headers   请求头
     */
    public static String doGet(String path, String proxyIp, Integer proxyPort, Map<String, String> params, Map<String, String> headers) {
        if (params != null && !params.isEmpty()) {
            StringBuilder paramsBuilder = new StringBuilder("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramsBuilder.append(entry.getKey());
                paramsBuilder.append("=");
                if (entry.getValue() != null) {
                    paramsBuilder.append(entry.getValue());
                }
            }
            paramsBuilder.delete(paramsBuilder.length() - 1, paramsBuilder.length());
            path = path + paramsBuilder;
        }
        try {
            HttpURLConnection connection = createHttpUrlConnection(path, proxyIp, proxyPort, "GET", headers);
            String result = get(connection.getInputStream());
            connection.disconnect();
            return result;
        } catch (Exception e) {
            log.error("执行doGet方法异常!", e);
        }
        return null;
    }

    /**
     * POST JSON请求
     *
     * @param path    请求路径
     * @param json    请求JSON
     * @param headers 请求头
     */
    public static String doPost(String path, String json, Map<String, String> headers) {
        return doPost(path, null, null, json, headers);
    }

    /**
     * POST JSON请求(代理)
     *
     * @param path      请求路径
     * @param proxyIp   代理地址
     * @param proxyPort 代理端口
     * @param json      请求JSON
     * @param headers   请求头
     */
    public static String doPost(String path, String proxyIp, Integer proxyPort, String json, Map<String, String> headers) {
        try {
            HttpURLConnection connection = createHttpUrlConnection(path, proxyIp, proxyPort, "POST", headers);
            connection.setRequestProperty("Content-Type", "application/json");
            send(connection.getOutputStream(), json);
            String result = get(connection.getInputStream());
            connection.disconnect();
            return result;
        } catch (Exception e) {
            log.error("执行doPost方法异常!", e);
        }
        return null;
    }

    /**
     * POST表单请求
     *
     * @param path    请求路径
     * @param params  请求参数
     * @param headers 请求头
     */
    public static String doPost(String path, Map<String, Object> params, Map<String, String> headers) {
        return doPost(path, null, null, params, headers);
    }

    /**
     * POST表单请求(代理)
     *
     * @param path      请求路径
     * @param proxyIp   代理地址
     * @param proxyPort 代理端口
     * @param params    请求参数
     * @param headers   请求头
     */
    public static String doPost(String path, String proxyIp, Integer proxyPort, Map<String, Object> params, Map<String, String> headers) {
        try {
            HttpURLConnection connection = createHttpUrlConnection(path, proxyIp, proxyPort, "POST", headers);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream outputStream = connection.getOutputStream();
            StringBuilder paramsBuilder = new StringBuilder();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    paramsBuilder.append(entry.getKey());
                    paramsBuilder.append("=");
                    if (entry.getValue() != null) {
                        paramsBuilder.append(entry.getValue());
                    }
                    paramsBuilder.append("&");
                }
                paramsBuilder.delete(paramsBuilder.length() - 1, paramsBuilder.length());
            }
            send(outputStream, paramsBuilder.toString());
            InputStream inputStream = connection.getInputStream();
            String result = get(inputStream);
            connection.disconnect();
            return result;
        } catch (Exception e) {
            log.error("执行doPost方法异常!", e);
        }
        return null;
    }

    /**
     * 构建HttpURLConnection对象
     *
     * @param path    请求路径
     * @param method  请求方式
     * @param headers 请求头
     */
    public static HttpURLConnection createHttpUrlConnection(String path, String method, Map<String, String> headers) {
        return createHttpUrlConnection(path, null, null, method, headers);
    }

    /**
     * 构建HttpURLConnection对象
     *
     * @param path      请求路径
     * @param proxyIp   代理地址
     * @param proxyPort 代理端口
     * @param method    请求方式
     * @param headers   请求头
     */
    public static HttpURLConnection createHttpUrlConnection(String path, String proxyIp, Integer proxyPort, String method, Map<String, String> headers) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(path);
            // 请求代理设置
            if (proxyIp != null && proxyPort != null && !"".equals(proxyIp) && 0 != proxyPort) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, proxyPort));
                connection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }
            // 设置cookie策略，只接受与你对话服务器的cookie，而不接收Internet上其它服务器发送的cookie
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
            CookieHandler.setDefault(cookieManager);
            // 请求方式
            connection.setRequestMethod(method);
            // 设置超时时间
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            // 构建请求头
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.5");
            buildHeaders(connection, headers);
            // POST 请求不能使用缓存
            if ("POST".equals(method)) {
                connection.setUseCaches(false);
            }
            // 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true
            connection.setDoInput(true);
            connection.setDoOutput(true);
        } catch (Exception e) {
            log.error("初始化HttpURLConnection失败", e);
        }
        return connection;
    }

    /**
     * 发送HTTP请求参数
     *
     * @param outputStream 输出流
     * @param value        参数
     */
    private static void send(OutputStream outputStream, String value) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        writer.write(value);
        writer.flush();
        writer.close();
    }

    /**
     * 获取HTTP请求结果
     *
     * @param inputStream 输入流
     * @return 请求结果
     */
    private static String get(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        char[] chars = new char[1024];
        while (reader.read(chars) != -1) {
            result.append(chars);
        }
        return result.toString();
    }

    /**
     * 构建请求头
     *
     * @param connection HttpURL连接
     * @param headers    请求头
     */
    private static void buildHeaders(HttpURLConnection connection, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 获取客户端真实IP地址
     *
     * @param request 请求request对象
     * @return IP地址
     */
    public String getIpAddress(HttpServletRequest request) {
        // X-Forwarded-For:Squid服务代理
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            // Proxy-Client-IP:apache服务代理
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            // WL-Proxy-Client-IP:WebLogic服务代理
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            // HTTP_CLIENT_IP:有些代理服务器
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            // X-Real-IP:Nginx服务代理
            ipAddress = request.getHeader("X-Real-IP");
        }
        // 还是不能获取到，最后再通过request.getRemoteAddr()获取
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    log.error("获取ipAddress失败!", e);
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割，"***.***.***.***".length() = 15
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 可用于构建返回Headers与Cookie的结果集
     */
    public static class HttpResult {

        private String result;

        private Map<String, String> headers;

        private Map<String, String> cookies;

        public HttpResult() {
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public Map<String, String> getCookies() {
            return cookies;
        }

        public void setCookies(Map<String, String> cookies) {
            this.cookies = cookies;
        }
    }

}
