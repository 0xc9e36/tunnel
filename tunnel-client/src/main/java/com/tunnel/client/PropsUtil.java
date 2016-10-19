package com.tunnel.client;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置文件工具类
 * Created by CaiDongYu on 2016/4/8.
 */
public final class PropsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    private PropsUtil() {}
    
    /**
     * 加载属性文件
     */
    public static Properties loadProps(String fileName) {
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            LOGGER.error("load properties file [" + fileName + "] failure", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    LOGGER.error("close input stream failure ", e);
                }
            }
        }
        return props;
    }

    /**
     * 获取字符型属性（默认值为空字符串）
     */
    public static String getString(Properties props, String key) {
        return getString(props, key, "");
    }

    /**
     * 获取字符型属性（可以指定默认值）
     */
    public static String getString(Properties props, String key, String defaultValue) {
        String value = defaultValue;
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

}