package com.xinge.chat.module.login;

// 从so库读取密钥（自动登录密码解密用）
class GetLocalKey {
    static {
        System.loadLibrary("ChatLibrary");
    }
    public native static String getKey(String signature);
}
