package com.cyd.xs.util;

import cn.hutool.core.util.IdUtil;

public class IDGenerator {
    public static String generateId() {
        return IdUtil.simpleUUID();
    }

    public static String generateContentId() {
        return "c" + System.currentTimeMillis();
    }

    public static String generateTopicId() {
        return "t" + System.currentTimeMillis();
    }

    public static String generateCircleId() {
        return "g" + System.currentTimeMillis();
    }
}
