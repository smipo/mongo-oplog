package com.oplog.common.utils;

public class Requires {

    public static <T> T requireNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
}
