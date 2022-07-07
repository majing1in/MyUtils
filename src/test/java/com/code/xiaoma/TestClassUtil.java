package com.code.xiaoma;

import com.alibaba.fastjson.JSON;
import com.xiaoma.code.ClassUtil;
import com.xiaoma.code.Entity.Entity;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xiaoma
 * @Date: 2022/07/07 20:58
 * @Email: 2533144458@qq.com
 */
public class TestClassUtil {

    @Test
    public void test01() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 1);
        map.put("message", "message");
        Entity.Data data = new Entity.Data("data");
        map.put("data", data);
        Entity entity = ClassUtil.newInstance(Entity.class, map);
        System.out.println(JSON.toJSONString(entity));
    }

    @Test
    public void test02() {
        Class[] classes = {Integer.class, String.class, Object.class};
        Object[] objects = {1, "message", new Entity.Data("data")};
        Entity entity = ClassUtil.newInstance(Entity.class, classes, objects);
        System.out.println(JSON.toJSONString(entity));
    }

    @Test
    public void test03() {
        Entity entity = new Entity();
        List<String> fieldName = ClassUtil.getFieldName(entity);
        System.out.println(JSON.toJSONString(fieldName));
    }

}
