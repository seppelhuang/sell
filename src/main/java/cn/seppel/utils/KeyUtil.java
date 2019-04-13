package cn.seppel.utils;

import java.util.Random;

/**
 * @description:
 * @author: Huangsp
 * @create: 2019-03-17
 **/
public class KeyUtil {
    public static synchronized String genUniqueKey() {
        Random random = new Random();

        Integer result = random.nextInt(900000) + 100000;

        return System.currentTimeMillis() + String.valueOf(result);

    }
}
