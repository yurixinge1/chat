package com.xinge.chat.util.crypto;

import java.security.MessageDigest;

public class MD5Encoder {
    // 图片缓存工具用：url有特殊字符系统可能无法识别，需要先做MD5加密
    // MD5都是128bit，不可逆，所以没decode方法
    public static String encode(String string) throws Exception {
        byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
