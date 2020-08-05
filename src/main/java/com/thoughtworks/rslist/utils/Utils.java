package com.thoughtworks.rslist.utils;

public class Utils {

    public static void isNull(Object object, String msg) {
        if (object == null) {
            throw new RuntimeException(msg);
        }
    }

    public static boolean strIsBlank(String str) {
        if (str == null) return false;
        return !str.isEmpty();
    }
}
