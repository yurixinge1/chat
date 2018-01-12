package com.xinge.chat.util.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.xinge.chat.R;
import com.xinge.chat.application.ChatApplication;
import com.xinge.chat.util.crypto.MD5Encoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtil {

    // 将安卓格式的Bitmap转换成字节数组（发给服务器）
    public static byte[] BitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);       // 质量压缩：改变位数
        return baos.toByteArray();
    }

    // 根据实际路径获取图片
    public static Bitmap getNativeImage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        Log.d("压缩前:",  (bitmap.getByteCount()/1024) + "K, 宽度: " + bitmap.getWidth() + "高度: " + bitmap.getHeight());
        return bitmap;
    }

    // 压缩图片
    public static Bitmap compressBitmap(Bitmap bm) {
        // 如果尺寸和大小小于一定的值不压缩。

        // 缩放法压缩
        Matrix matrix = new Matrix();
        matrix.setScale(0.8f, 0.8f);
        Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        Log.d("压缩后:",  (bitmap.getByteCount()/1024) + "K, 宽度: " + bitmap.getWidth() + "高度: " + bitmap.getHeight());

        // 还有很多种压缩法。图片占用的内存 = 长像素*宽像素*位数，改变这3个因素来压缩。

        return bitmap;
    }

    // 缓存
    public static class BitmapCache {
        private MemoryCache mMemoryCache;
        private LocalCache mLocalCache;
        private NetCache mNetCache;
        public BitmapCache() {
            mMemoryCache = new MemoryCache();
            mLocalCache = new LocalCache(mMemoryCache);
            mNetCache = new NetCache(mLocalCache);
        }
        public void setBitmap(ImageView iv, String url) {
            /*Bitmap bitmap = mMemoryCache.getBitmapFromMemory(url);
            if (bitmap != null){
                iv.setImageBitmap(bitmap);
                System.out.println("从内存获取了图片。");
                return;
            }
            bitmap = mLocalCache.getBitmapFromLocal(url);
            if (bitmap != null) {
                iv.setImageBitmap(bitmap);
                System.out.println("从本地获取了图片。");
                return;
            }*/
            // 网络缓存, 用AsyncTask和HttpURLConnection。AsyncTask似乎不能返回Bitmap，所以直接在里面更新ImageView。
            mNetCache.getBitmapFromNet(iv, url);
        }
    }

    // 网络缓存
    private static class NetCache {

        private LocalCache mLocalCache;     // 从网络获得图片后写入本地文件缓存

        NetCache(LocalCache lc) {
            mLocalCache = lc;
        }

        void getBitmapFromNet(ImageView iv, String url) {
            new BitmapNetTask().execute(iv, url);
        }

        private class BitmapNetTask extends AsyncTask<Object, Void, Bitmap> {
            private ImageView iv;
            private String url;

            // 子线程下载图片
            // 如果有多个View同时通过此方法获取网络图片，会开多条线程；
            // 完成任务后的线程默认结束，如果有新任务会复用。
            @Override
            protected Bitmap doInBackground(Object... params) {
                iv = (ImageView)params[0];
                url = (String)params[1];
                Bitmap bitmap = null;
                HttpURLConnection conn = null;
                try {
                    System.out.println("线程名称=" + Thread.currentThread().getName());
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);  // 如果读一个大文件超过5秒，AsyncTask会不会自动结束？
                    conn.setRequestMethod("GET");
                    int responseCode = conn.getResponseCode();
                    if (responseCode==200) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;   // 宽高压缩为原来的1/2
                        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                        bitmap = BitmapFactory.decodeStream(conn.getInputStream(), null, options);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null)   conn.disconnect();
                    System.out.println("线程名称=" + Thread.currentThread().getName() + "finally。");
                }
                return bitmap;
            }

            // 主线程更新进度
            @Override
            protected void onProgressUpdate(Void[] values) {
                super.onProgressUpdate(values);
            }

            // 主线程处理任务结束后的事情
            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    iv.setImageBitmap(result);
                    mLocalCache.setBitmapToLocal(url, result);
                    //System.out.println("从网络获取了图片。");
                }
                System.out.println("线程名称=" + Thread.currentThread().getName() + "结束。");
            }
        }
    }

    // 本地文件缓存
    private static class LocalCache {

        private MemoryCache mMemoryCache;   // 写入内存缓存
        // private static final String BITMAP_LOCAL_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() ;  // 内置SD卡，不同版本安卓有不同
        private static final String BITMAP_LOCAL_CACHE_PATH = ChatApplication.getApp().getCacheDir() + "/ImagesCache"; // /data/data/com.xinge.chat/cache/ImagesCache

        LocalCache(MemoryCache mc) {
            mMemoryCache = mc;
        }

        Bitmap getBitmapFromLocal(String url){
            String fileName;
            Bitmap bitmap = null;
            try {
                fileName = MD5Encoder.encode(url);
                File file = new File(BITMAP_LOCAL_CACHE_PATH, fileName);
                bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        // 从网络获取图片后保存到本地
        void setBitmapToLocal(String url, Bitmap bitmap){
            try {
                String fileName = MD5Encoder.encode(url);  // url有特殊字符系统可能无法识别，需要先做MD5加密
                File file = new File(BITMAP_LOCAL_CACHE_PATH, fileName);
                File parentFile = file.getParentFile();
                if (!parentFile.exists()){
                    if (!parentFile.mkdirs())  {
                        System.out.println("创建本地图片目录出错。");
                        return;
                    }
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                mMemoryCache.setBitmapToMemory(url, bitmap);
            } catch (Exception e) {
                System.out.println("保存到本地图片目录出错。");
                e.printStackTrace();
            }
        }
    }

    // 内存缓存
    private static class MemoryCache {
        // 使用LRU最少最近使用算法，和SoftReference相比好处在于
        private LruCache<String,Bitmap> mMemoryCache;

        MemoryCache() {
            long maxMemory = Runtime.getRuntime().maxMemory() / 8;            // 得到手机最大允许内存的1/8。
            mMemoryCache = new LruCache<String,Bitmap>((int) maxMemory){    // 超过指定内存,则开始回收。
                @Override
                protected int sizeOf(String key, Bitmap value) {  // 返回每个bitmap的大小
                    return value.getByteCount();
                }
            };
        }
        // 从内存读取图片
        Bitmap getBitmapFromMemory(String url) {
            return mMemoryCache.get(url);
        }
        // 往内存写入图片
        void setBitmapToMemory(String url, Bitmap bitmap) {
            mMemoryCache.put(url, bitmap);
        }
    }
}