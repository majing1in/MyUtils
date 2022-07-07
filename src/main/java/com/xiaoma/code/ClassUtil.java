package com.xiaoma.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Xiaoma
 * @Date: 2022/07/07 20:11
 * @Email: 2533144458@qq.com
 */
@SuppressWarnings("unchecked")
public class ClassUtil {

    private static final Logger log = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * set方法名前缀
     */
    public static final String SET_PREFIX = "set";

    /**
     * get方法名前缀
     */
    public static final String GET_PREFIX = "get";

    /**
     * 获取所有属性名称
     *
     * @param obj 对象
     * @return 属性集合
     */
    public static List<String> getFieldName(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        return Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
    }

    /**
     * 根据变量名获取变量值
     *
     * @param obj       对象
     * @param fieldName 变量名
     * @return 变量值
     */
    public static <T> T getFieldValueByName(Object obj, String fieldName) {
        String methodFirstLetter = fieldName.substring(0, 1).toUpperCase();
        String getterMethodName = GET_PREFIX + methodFirstLetter + fieldName.substring(1);
        try {
            Method method = obj.getClass().getMethod(getterMethodName);
            Object value = method.invoke(obj);
            return (T) value;
        } catch (Exception e) {
            log.error("根据变量名获取变量值异常!", e);
        }
        return null;
    }

    /**
     * 根据变量名设置对象值
     *
     * @param obj        对象
     * @param fieldName  变量名
     * @param fieldValue 变量值
     */
    public static void setFieldValueByName(Object obj, String fieldName, Object... fieldValue) {
        String methodFirstLetter = fieldName.substring(0, 1).toUpperCase();
        String setterMethodName = SET_PREFIX + methodFirstLetter + fieldName.substring(1);
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            if (setterMethodName.equals(method.getName())) {
                try {
                    method.invoke(obj, fieldValue);
                } catch (Exception e) {
                    log.error("存在多态方法，继续寻找!");
                }
            }
        }
    }

    /**
     * 通过set方法构造对象
     *
     * @param clazz 返回对象类型
     * @param map   参数
     *              <br> k -> 变量名<br/>
     *              <br> v -> 变量值<br/>
     * @return 构造对象
     */
    public static <T> T newInstance(Class<T> clazz, Map<String, Object> map) {
        T obj = null;
        try {
            obj = clazz.newInstance();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                setFieldValueByName(obj, entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            log.error("通过set方法构造对象异常!", e);
        }
        return obj;
    }

    /**
     * 通过指定构造方法构造对象
     *
     * @param clazz   返回对象类型
     * @param classes 获取指定构造方法 -> 顺序与构造方法一致
     * @param args    构造方法参数 -> 与构造方法类型一致
     * @return 构造对象
     */
    public static <T, C> T newInstance(Class<T> clazz, Class<C>[] classes, Object[] args) {
        T obj = null;
        try {
            obj = clazz.getConstructor(classes).newInstance(args);
        } catch (Exception e) {
            log.error("通过构造方法构造对象异常!", e);
        }
        return obj;
    }
}
