package com.code.xiaoma;

import com.alibaba.fastjson.JSON;
import com.code.xiaoma.Entity.Entity;
import com.xiaoma.code.ClassUtil;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: majing1in
 * @Date: 2022/07/07 20:58
 * @Email: 2533144458@qq.com
 */
public class TestClassUtil {

    @Test(timeout = 100, expected = Exception.class)
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

    @Test
    public void test04() {
        Date current = new Date();
        String date1 = "2012-09-15";
        String date2 = "2023-02-15";
        Date parse1 = parseToDate(date1, "yyyy-MM-dd");
        Date parse2 = parseToDate(date2, "yyyy-MM-dd");
        double workYear = getWorkYear(parse2, parse1);
        System.out.println(workYear);
    }

    /**
     * 获取两个日期相差的月数
     */
    public static double getWorkYear(Date d1, Date d2) {
        int count = 12;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        // 获取年的差值
        int yearInterval = year1 - year2;
        if (month1 < month2) {
            yearInterval--;
        }
        // 获取月数差值
        int monthInterval = (month1 + count) - month2;
        monthInterval = monthInterval % count;
        // 计算工龄
        int workYears = Math.abs(yearInterval * count + monthInterval);
        BigDecimal divide = new BigDecimal(count);
        BigDecimal total = new BigDecimal(workYears);
        BigDecimal result = total.divide(divide, 2, BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }

    public static Date parseToDate(String sDate, String pattern) {
        Date date = null;
        try {
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            date = sf.parse(sDate);
        } catch (ParseException e) {
        }
        return date;
    }

}
