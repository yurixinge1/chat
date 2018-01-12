
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include <android/log.h>

#include "com_xinge_chat_module_login_GetLocalKey.h"

char const *RELEASE_SIGN = "308201dd30820146020101300d06092a864886f70d010105050030373116301406035504030c0d416e64726f69642044656275673110300e060355040a0c07416e64726f6964310b3009060355040613025553301e170d3137303731393034333832375a170d3437303731323034333832375a30373116301406035504030c0d416e64726f69642044656275673110300e060355040a0c07416e64726f6964310b300906035504061302555330819f300d06092a864886f70d010101050003818d0030818902818100b93c70eb19ffdff5a72f10e90c19355205092761b557d31f5a83f9ca10d9e59981c4b2a82793e48ddb5b82b773d3c8a915d6ca5a3a0b826b294f89b02d5e8e40925708d2b4d8f367074657402170ae0cf446fa531160aea427caa13c74d457a93d91b701c2aaf79a663a71a6c88256bba474ebebac634fff0a92fc9b4497b8290203010001300d06092a864886f70d0101050500038181009315d4d9829be8d421f4b1ef74d9ddf0de278c6ac68297644c93a3231ca2fb17daffe846c37f034bea25f2513c329f11b80705bc07e6a39f54a79b5dce16eeeab6bbae5315ecb3412780e40e77c7dae35e08204a2041390752ea4dc7a35cde70890ebca069dc0e22ca23fa1dfc91dd9bf261243e60697d046cb21ba25e24d359";
char const *key = "1wAj15gPkkJ1ssjPQu3gg5+Clb/Zhz3NVfCC7+VnL+w=";  // 40个字符

JNIEXPORT jstring JNICALL Java_com_xinge_chat_module_login_GetLocalKey_getKey(JNIEnv *env, jobject obj, jstring signature) {  // signature是app签名

    // 获得Java的String类和getBytes方法
    jclass clsString = env->FindClass("java/lang/String");
    jstring strEncode = env->NewStringUTF("utf-8");
    jmethodID mId = env->GetMethodID(clsString, "getBytes", "(Ljava/lang/String;)[B");

    // 得到签名的字节码，相当于Java的 ByteArray bArray = signature.getBytes("UTF-8");
    jbyteArray bArray = (jbyteArray) env->CallObjectMethod(signature, mId, strEncode);
    // 字节长度
    jsize len = env->GetArrayLength(bArray);
    // 转换成byte[]
    jbyte *bByte = env->GetByteArrayElements(bArray, JNI_FALSE);

    char *cKey = NULL;
    if (len > 0) {
        cKey = (char *) malloc(len + 1);
        memcpy(cKey, bByte, len);
        cKey[len] = 0;
    }
    env->ReleaseByteArrayElements(bArray, bByte, 0);
    if (strcmp(RELEASE_SIGN, cKey) == 0) {
        return env->NewStringUTF(key);
    }

    return (*env).NewStringUTF("从so获取密钥失败！");
}