package com.xiaoma.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: majing1in
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

    /**
     * 获取类上注解指定属性值
     *
     * @param obj             对象
     * @param clazz           注解类型
     * @param annotationField 注解属性名
     * @return 注解属性值
     */
    public static <A extends Annotation, V> V getObjAnnotationValue(Object obj, Class<A> clazz, String annotationField) {
        return getAnnotationValue(obj, clazz, null, annotationField, 1);
    }

    /**
     * 获取方法上注解指定属性值
     *
     * @param obj             对象
     * @param clazz           注解类型
     * @param attributeName   属性名称
     * @param annotationField 注解属性名
     * @return 注解属性值
     */
    public static <A extends Annotation, V> V getFieldAnnotationValue(Object obj, Class<A> clazz, String attributeName, String annotationField) {
        return getAnnotationValue(obj, clazz, attributeName, annotationField, 2);

    }

    /**
     * 获取方法上注解指定属性值
     *
     * @param obj             对象
     * @param clazz           注解类型
     * @param attributeName   方法名称
     * @param annotationField 注解属性名
     * @return 注解属性值
     */
    public static <A extends Annotation, V> V getMethodAnnotationValue(Object obj, Class<A> clazz, String attributeName, String annotationField) {
        return getAnnotationValue(obj, clazz, attributeName, annotationField, 3);
    }

    /**
     * 获取方法上注解指定属性值
     *
     * @param obj           对象
     * @param clazz         注解类型
     * @param attributeName 方法名称
     * @return 注解属性值
     */
    public static <A extends Annotation, V> Map<String, V> getObjAnnotationValues(Object obj, Class<A> clazz, String attributeName) {
        return getAnnotationValues(obj, clazz, attributeName, 1);
    }


    /**
     * 获取方法上注解指定属性值
     *
     * @param obj           对象
     * @param clazz         注解类型
     * @param attributeName 方法名称
     * @return 注解属性值
     */
    public static <A extends Annotation, V> Map<String, V> getFieldAnnotationValues(Object obj, Class<A> clazz, String attributeName) {
        return getAnnotationValues(obj, clazz, attributeName, 2);
    }


    /**
     * 获取方法上注解指定属性值
     *
     * @param obj           对象
     * @param clazz         注解类型
     * @param attributeName 方法名称
     * @return 注解属性值
     */
    public static <A extends Annotation, V> Map<String, V> getMethodAnnotationValues(Object obj, Class<A> clazz, String attributeName) {
        return getAnnotationValues(obj, clazz, attributeName, 3);
    }

    /**
     * 获取注解
     *
     * @param obj           对象
     * @param clazz         注解类型
     * @param attributeName 注解属性名
     * @param type          执行类型
     *                      <br> type=1 -> 类名 <br/>
     *                      <br> type=2 -> 方法 <br/>
     *                      <br> type=3 -> 变量 <br/>
     * @return 注解
     */
    public static <A extends Annotation> Annotation getAnnotation(Object obj, Class<A> clazz, String attributeName, Integer type) {
        A annotation = null;
        try {
            if (1 == type) {
                annotation = obj.getClass().getAnnotation(clazz);
            } else if (2 == type) {
                Field field = obj.getClass().getDeclaredField(attributeName);
                annotation = field.getAnnotation(clazz);
            } else if (3 == type) {
                Method[] methods = obj.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    if (attributeName.equals(method.getName())) {
                        annotation = method.getAnnotation(clazz);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取注解指定值异常!", e);
        }
        return annotation;
    }

    /**
     * 获取注解指定属性值
     *
     * @param obj             对象
     * @param clazz           注解类型
     * @param attributeName   方法名称
     * @param annotationField 注解属性名
     * @return 注解属性值
     */
    private static <A extends Annotation, V> V getAnnotationValue(Object obj, Class<A> clazz, String attributeName, String annotationField, Integer type) {
        try {
            Annotation annotation = getAnnotation(obj, clazz, attributeName, type);
            Field field = annotation.getClass().getDeclaredField(annotationField);
            field.setAccessible(true);
            return (V) field.get(annotation);
        } catch (Exception e) {
            log.error("获取注解指定值异常!", e);
        }
        return null;
    }

    /**
     * 获取注解所有属性值
     *
     * @param obj           对象
     * @param clazz         注解类型
     * @param attributeName 注解属性名
     * @return 注解属性值
     */
    private static <A extends Annotation, V> Map<String, V> getAnnotationValues(Object obj, Class<A> clazz, String attributeName, Integer type) {
        try {
            Annotation annotation = getAnnotation(obj, clazz, attributeName, type);
            Field[] fields = annotation.getClass().getDeclaredFields();
            Map<String, V> map = new HashMap<>();
            for (Field field : fields) {
                String key = field.getName();
                field.setAccessible(true);
                Object value = field.get(annotation);
                map.put(key, (V) value);
            }
            return map;
        } catch (Exception e) {
            log.error("获取注解指定值异常!", e);
        }
        return null;
    }

}
