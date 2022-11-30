package com.xdroid.annotation.tool;

import java.util.regex.Pattern;

/**
 * 字符串处理工具
 */
public class XDStrings {

    /**
     * 避免使用 “+”
     * SafeVarargs 因为擦除，数组的运行时类型就只能是Object[]
     * 不影响安全，忽略
     */
    @SafeVarargs
    public static <T> String unitMultiArgs(T... args) {
        StringBuilder sb = new StringBuilder();
        for (T s : args) {
            sb.append(s);
        }
        return sb.toString();
    }

}
