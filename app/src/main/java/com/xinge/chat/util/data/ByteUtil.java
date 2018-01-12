package com.xinge.chat.util.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by xinge on 2017/10/13.
 */

public class ByteUtil {

    // 聊天传输的内容会先转换成byte[]，并设置第一个byte
    // 为标志：byte[0]='a'，文字；byte[0]='b'，图片
    public static byte[] setFlag(byte[] source, byte flag) {
        int len = source.length;
        byte[] msgBytes = new byte[len+1];
        msgBytes[0] = flag;
        System.arraycopy(source, 0, msgBytes, 1, len);
//        for (int i=0; i<len; i++) {
//            msgBytes[i+1] = source[i];
//        }
        return msgBytes;
    }

    // 压缩byte[]
    // 方法有很多种
    public static byte[] zip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(data);
            gzip.finish();
            gzip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    // 解压byte[]
    public static byte[] unZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }
}
