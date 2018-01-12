package com.xinge.chat.util.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class StringUtil {

    // 压缩字符串，String压缩成byte[]，ChatActivity需要对byte[]做其它处理。
    // 字符数量少（通常200个）且重复率低的情况下压缩反而会使压缩后的字符串变大，GZIP和7-ZIP压缩率差不多。
    public static byte[] zip(String str) {
        if (str==null || str.length()==0) {
            return null;
        }
        byte[] compressed = null;
        ByteArrayOutputStream out;
        ZipOutputStream zout;
        try {
            out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str.getBytes("utf-8"));
            zout.closeEntry();
            compressed = out.toByteArray();
            zout.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressed;
    }

    // 解压缩
    public static String unZip(byte[] compressed) {
        if (compressed==null) {
            return null;
        }
        ByteArrayOutputStream out;
        ByteArrayInputStream in;
        ZipInputStream zin;
        String decompressed;
        try {
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            zin = new ZipInputStream(in);
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset;
            while ( (offset=zin.read(buffer)) != -1 ) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
            zin.close();
            in.close();
            out.close();
        } catch (IOException e) {
            decompressed = null;
            e.printStackTrace();
        }
        return decompressed;
    }
}
