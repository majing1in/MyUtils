package com.xiaoma.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author majing1in
 * @Date 2022/7/12 15:18:55
 */
public class PropertiesUtil {

    private static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    public static final ConcurrentHashMap<String, Properties> PROPERTIES_CACHE = new ConcurrentHashMap<>();

    public static final Object LOCK = new Object();

    public static String getPropertiesValue(String path, String key) {
        return getPropertiesValue(path, "utf8", key);
    }

    public static Properties loadProperties(String path) {
        return loadProperties(path, "utf8");
    }

    public static String getPropertiesValueInCache(String path, String key) {
        return getPropertiesValueInCache(path, "utf8", key);
    }

    public static Properties loadPropertiesInCache(String path) {
        return loadPropertiesInCache(path, "utf8");
    }

    public static String getPropertiesValue(String path, String charset, String key) {
        Properties properties = loadProperties(path, charset);
        return properties != null ? properties.getProperty(key) : null;
    }

    public static String getPropertiesValueInCache(String path, String charset, String key) {
        Properties properties = loadPropertiesInCache(path, charset);
        return properties != null ? properties.getProperty(key) : null;
    }

    public static Properties loadProperties(String path, String charset) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        if (charset == null || "".equals(charset)) {
            charset = "utf8";
        }
        Properties properties = null;
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), charset);
            properties = new Properties();
            properties.load(reader);
            reader.close();
        } catch (Exception e) {
            log.error("读取{}文件失败", path, e);
        }
        return properties;
    }

    public static Properties loadPropertiesInCache(String path, String charset) {
        Properties properties = PROPERTIES_CACHE.get(path);
        if (properties == null) {
            synchronized (LOCK) {
                properties = loadProperties(path, charset);
                if (properties == null) {
                    return null;
                }
                PROPERTIES_CACHE.put(path, properties);
            }
        }
        return properties;
    }
}
