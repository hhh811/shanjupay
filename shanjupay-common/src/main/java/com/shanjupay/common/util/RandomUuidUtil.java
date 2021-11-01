package com.shanjupay.common.util;

import java.util.UUID;

/**
 * 随机UUID工具
 */
public class RandomUuidUtil {

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr = str.replace("-", "");
        return uuidStr;
    }

    public static void main(String[] args) {
        System.out.println(getUUID());
    }
}
