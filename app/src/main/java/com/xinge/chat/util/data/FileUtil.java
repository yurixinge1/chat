package com.xinge.chat.util.data;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//
public class FileUtil {

    /* 将String保存为SD卡中TXT文件
    public static boolean StringToFile(String content, String path, String fileName) {
        try {
            FileWriter fw = new FileWriter(path + fileName);  // 文件如果不存在会自动创建，文件夹不会。
            fw.flush();
            fw.write(content);
            fw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    } */

    // 从服务器下载一个文件保存到本地
    public static void downloadFile(final String serverUrl, final String localUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(serverUrl).openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        byte[] bs = new byte[1024];
                        int len;
                        OutputStream os = new FileOutputStream(localUrl);
                        while ((len = is.read(bs)) != -1) {
                            os.write(bs, 0, len);
                        }
                        os.close();
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null)   conn.disconnect();
                }
            }
        }).start();
    }

    // 上传一个文件到J2EE服务器上
    public static void uploadFile(String localUrl, String serverUrl) {

    }

    // 将一个文件拷到本地项目res/目录下，再复制进AVD里

}
