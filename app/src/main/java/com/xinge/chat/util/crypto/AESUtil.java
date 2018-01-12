package com.xinge.chat.util.crypto;

import android.util.Log;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

// AES对称加密
// 在不同版本的Android SDK可能会错
// 不适用Java
public class AESUtil {

    // 生成随机密钥
    public static byte[] generateKey() {
        byte[] key = null;
        try {
            SecureRandom sr = new SecureRandom();
            byte[] salt = new byte[32];  // salt盐值的长度：一组安全随机数的长度
            sr.nextBytes(salt);           // 将32个伪随机数保存到了salt数组
            // 参数1 "CryptoDemo" 自定义的key字符串 ；参数3：1000 循环次数  参数4 256 密钥长度256bits（也可以是128,192等, 至少128?）
            KeySpec ks = new PBEKeySpec("CryptoDemo".toCharArray(), salt, 1000, 256);  // CryptoDemo也应存于JNI
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");  // 这个算法有很多种，似乎和AES没关
            key = keyFactory.generateSecret(ks).getEncoded();  // 生成密钥。每次生成的都不同，因为根据的是随机数。
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * 根据密钥加密字符串
     * @param str 需要加密的字符串，例如密码、聊天文本等String
     * @param key 密钥
     * @return 将加密后的二进制字节转换成十六进制字符串
     */
    public static String Encrypt(String str, byte[] key) {
        if (str==null || str.equals(""))    {
            Log.e("AES：", "空字符串无法加密。");
            return "";
        }
        // 1.新建cipher
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            return "";
        }

        // 2.加密字符串
        byte[] result;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            // 第3个参数，IV = initialization vector，cipher.getBlockSize=16bytes=128bits
            // IvParameterSpec实现了AlgorithmParameterSpec，其不包含任何方法或常量。它仅用于将所有参数规范分组,并为其提供类型安全。
            // 猜测的用途是cipher对象会将加密算法参数保存到了AlgorithmParameter对象，再拷一份到IV里做备份或其它用途。
            // 猜测是因为加密算法参数在加密过程中可能会被修改，所以备一份到IV里。
            // 如果其它语言系统也用cipher加密，例如C#，这里需要统一，否则无法解密。
            // cipher.getBlockSize()=16字节，如果将16换成其它数字会出错。似乎在安卓里只能是16，也就是cipher的块大小。
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            result = cipher.doFinal(str.getBytes());
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            Log.e("AES：", "加密字符串出错。");
            e.printStackTrace();
            return "";
        }

        // 3.加密完后转十六进制或者Base64。
        // 因为被加密后的字节在码表（例如UTF-8 码表）可能找不到对应字符造成乱码，
        StringBuilder sb = new StringBuilder(2 * result.length);
        String HEX = "0123456789ABCDEF";
        for (byte b : result) {
            sb.append(HEX.charAt((b>>4) & 0x0f)).append(HEX.charAt(b & 0x0f));
        }

        return sb.toString();
    }

    /**
     * 根据密钥解密字符串
     * @param str 需要解密的十六进制字符串
     * @param key 密钥
     * @return 解密后的字符串原文
     */
    public static String Decrypt(String str, byte[] key) {

        if (str==null || str.equals(""))    {
            Log.e("AES：", "空字符串无法解密。");
            return "";
        }

        // 1.十六进制换回二进制
        int len = str.length()/2;
        byte[] b = new byte[len];
        for (int i=0; i<len; i++){
            b[i] = Integer.valueOf(str.substring(2*i, 2*i+2), 16).byteValue();
        }

        // 2.新建cipher
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            return "";
        }

        // 3.解密字符串
        byte[] result;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            result = cipher.doFinal(b);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            Log.e("AES：", "解密字符串出错。");
            e.printStackTrace();
            return "";
        }

        return new String(result);
    }
}
