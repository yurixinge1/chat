package com.xinge.chat.util.dynamic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinge.chat.R;
import com.xinge.chat.util.data.FileUtil;
import com.xinge.chat.util.reflect.RefInvoke;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

// 动态加载练习，和hotfix没关联。
public class DynamicLoadActivity extends AppCompatActivity {
    private TextView tvTest;
    private ImageView ivTest;
    private Button btnTest1;
    private Button btnTest2;
    private Resources mResources;
    protected AssetManager mAssetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_load);

        File file = this.getDir("apk", 0);
        String url2 = file.getAbsolutePath() + "/app-release.apk";
        FileUtil.downloadFile("http://10.0.2.2:8080/chat/resources/app-release.apk", url2);

        tvTest = (TextView)findViewById(R.id.tvTest);
        ivTest = (ImageView)findViewById(R.id.ivTest);
        btnTest1 = (Button)findViewById(R.id.btnTest1);
        btnTest2 = (Button)findViewById(R.id.btnTest2);
        btnTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRes("/data/data/com.xinge.chat/app_apk/app-release.apk");
                setStyle1();
            }
        });
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });
    }

    @SuppressLint("NewApi")
    private void click(){
        File dexOutputDir = getDir("dex_output", 0);
        String dexPath = "/data/data/com.xinge.chat/app_apk/app-release.apk";

        // 一、获得插件apk的AssetManager、Resource、Theme，并将当前apk的这3项改成它们。
        loadRes(dexPath);

        // 二、根据插件apk定义一个DexClassLoader，
        // 获得插件apk的MainActivity类对象
        // 获得插件apk的layout类对象，获得其成员apk1_layout.xml
        // LayoutInflater.from(this) 是定义一个MainActivity的layout填充器，对这个填充器填充apk1_layout形成一个layout。
        // 调用MainActivity的setLayoutView()方法设置上一步的layout。

        // 加载插件apk的Activity后又打开本地app的Activity不会受影响，因为：
        // 第四个参数是父加载器。设置父加载器为当前类的加载器，就能保证类的双亲委派模型不被破坏，
        // 在加载类时都是先由父加载器来加载，加载不成功时再由自己加载。
        DexClassLoader loader = new DexClassLoader(dexPath, dexOutputDir.getAbsolutePath(), null, getClass().getClassLoader());
        // getClassLoader()得到PathClassLoader，系统在ApkLoader里创建的mClassLoader也是PathClassLoader。
        Class<?> clazz = null;
        try {
            clazz = loader.loadClass("com.temp.rlapk1.MainActivity");

            // 获取和设置插件的layout（因为宿主app无法识别插件apk里的R.id）
            Class layout = loader.loadClass("com.temp.rlapk1.R$layout");
            Field field = layout.getField("apk1_layout");  // apk1_layout  activity_main
            Integer obj = (Integer)field.get(null); // 因为apk1_layout是static对象，所以参数可以为null，否则是layout
            // 这个this是插件apk里的。什么时候开始？什么时候结束？
            // 估计是从loadRes(dexPath);开始，mAssetManager=null后结束
            //
            View view = LayoutInflater.from(this).inflate(mResources.getLayout(obj), null);
            Method method = clazz.getDeclaredMethod("setLayoutView", View.class);
            method.setAccessible(true);
            method.invoke(clazz, view);  // 因为是static方法，第一个参数可为null
            // 可以操作是因为setLayoutView方法、成员都是static，所以才可以在加载activity前操作。
            // 真是神奇，在类对象新建（初始化）前可以先对其static对象操作。非反射的情况下，app进入
            // 运行时状态，系统有地方可以保存static对象我可以理解，但是反射这种情况下，估计的可能是：
            // 上面loader.loadClass("com.temp.rlapk1.MainActivity")，也就是系统加载一个类时，就将
            // 类里的static对象写入内存？启动activity对象时，activity对象如果需要内存里的static对象，
            // 那么再读出来。所以反不反射都一样，系统都是load一个class时就已将class里的static对象写入
            // 了内存，也可能DexClassLoader loader = new DexClassLoader(dexPath, dexOutputDir.getAbsolutePath(), null, getClass().getClassLoader());
            // 时就已将目标dex里的所有static对象都写入了内存。

            // 获取和设置插件的TextView的资源id
            Class ids = loader.loadClass("com.temp.rlapk1.R$id");
            Field field1 = ids.getDeclaredField("tvOne");
            Integer obj1 = (Integer)field1.getInt(null);
            System.out.println("tvOne=" + obj1);
            Method method1 = clazz.getMethod("setTextView", Integer.class);
            method1.setAccessible(true);
            method1.invoke(clazz, obj1);

            // 获取和设置插件的R.string里的文字的资源id，供上面的TextView使用。
            Class strings = loader.loadClass("com.temp.rlapk1.R$string");
            Method method2 = clazz.getMethod("setMyName", Integer.class);
            Field field2 = strings.getDeclaredField("myname");
            Integer obj2 = (Integer)field2.getInt(null);
            System.out.println("myname=" + obj2);
            method2.setAccessible(true);
            method2.invoke(clazz, obj2);

            // 获取和设置ImageView
            Method method3 = clazz.getMethod("setImageView", Integer.class);
            Field field3 = ids.getDeclaredField("ivOne");
            Integer obj3 = (Integer)field3.getInt(null);
            method3.setAccessible(true);
            method3.invoke(clazz, obj3);

            // 获取和设置drawable，供上面的ImageView使用。
            Class drawables = loader.loadClass("com.temp.rlapk1.R$drawable");
            Method method4 = clazz.getMethod("setMyPic", Integer.class);
            Field field4 = drawables.getDeclaredField("apk1pic");
            Integer obj4 = (Integer)field4.getInt(null);
            method4.setAccessible(true);
            method4.invoke(clazz, obj4);

        } catch (Throwable e) {
            e.printStackTrace();
        }

        // 三、自定义的DexClassLoader不能加载Activity，但包含的插件apk有目标activity类；
        // 当前app的默认的可加载Activity的PathClassLoader没包含插件apk的activity，
        // 所以将自定义的DexClassLoader换成插件apk的可加载Activity的PathClassLoader? 不是。
        // 所以将宿主app的ActivityThread类中的mPackages变量中保存的以当前包名为键的LoadedApk值的mClassLoader替换成自定义的DexClassLoader。
        // （将自声明的DexClassLoader塞到LoadedApk里就可以启动activity，放在外面不塞就不行，估计可能的原因是启动activity需要做大量的加工处理，
        // 例如上下文、初始化生命周期、资源处理、布局等等，这些处理由LoadedApk、ActivityThread完成。完成完后系统要找个ClassLoader来启动activity，
        // 而又没有现成的方法将自声明的DexClassLoader传到里面去，而且系统又只会去找LoadedApk里的mClassLoader。）
        // 因为DexClassLoader和PathClassLoader内部类似，所以才可以替换掉。
        loadApkClassLoader(loader);

        // 四、有合适的ClassLoader后就可以启动插件apk的Actvity。
        // 系统会新建插件的Activity对象，然后像操作本地Activity一样。
        // 新建前无法调用插件activity的方法，因为还没新建对象；
        // startActivity后也无法，因为当前宿主的activity已onStop()。
        Intent intent = new Intent(DynamicLoadActivity.this, clazz);
        // intent可以携带基本数据类型、序列化对象、bundler等。
        intent.putExtra("hichajian", "nihaochajian");
        startActivity(intent);
    }

    private void loadRes(String dexPath){
        try {
            mAssetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(mAssetManager, dexPath);  // 获得的是插件apk的资源管理器，不会和宿主的冲突。接下来就可以获取插件的资源了。
        } catch (Exception e) {
        }
        mResources = new Resources(mAssetManager, super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
        // 也可以根据资源获取主题
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    // 替换
    @SuppressLint("NewApi")
    private void loadApkClassLoader(DexClassLoader dLoader){
        try{
            // 1.在ActivityThread（主线程或UI线程）里：
            // static final ThreadLocal<ActivityThread> sThreadLocal = new ThreadLocal<ActivityThread>();
            // public static ActivityThread currentActivityThread() {
            //     return sThreadLocal.get();
            // }
            // 不知道什么时候做过sThreadLocal.set(当前ActivityThread) ，反正下面的方法是得到当前运行的ActivityThread
            // 一个app如果只有一个进程，那么应该只有一条ActivityThread，除非多进程。
            Object currentActivityThread = RefInvoke.invokeStaticMethod(   // 得到当前进程里的ActivityThread本身对象
                    "android.app.ActivityThread", "currentActivityThread",
                    new Class[] {}, new Object[] {});  // android.app.ActivityThread@bb02f22

            // 从指定的ActivityThread对象里获取其mPackage成员变量，因为mPackage不是static。
            // 如果mPackage是static，并且有个static方法可以返回mPackage，就不需要从指定的ActivityThread对象里获取其mPackage。
            // 也不该是static，应该在ActivityThread运行时根据包名给mPackage赋值，而每个ActivityThread的包名都不一样。
            ArrayMap mPackages = (ArrayMap) RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mPackages");

            // 2. final ArrayMap<String, WeakReference<LoadedApk>>  mPackages
            String packageName = this.getPackageName();  // com.xinge.chat
            WeakReference wr = (WeakReference) mPackages.get(packageName);  // 得到WeakReference<LoadedApk>

            // 3.wr.get()得到LoadedApk，然后将里面的mClassLoader替换成dLoader
            RefInvoke.setFieldOjbect("android.app.LoadedApk", "mClassLoader", wr.get(), dLoader); //
        }catch(Exception e) {
            Log.i("demo", "load apk classloader error:"+Log.getStackTraceString(e));
        }
    }

    @SuppressLint("NewApi")
    private void setStyle1() {
        DexClassLoader loader = null;
        Class clazz = null;
        try{
            File dexOutputDir = getDir("dex_output", 0);
            String dexPath = "/data/data/com.xinge.chat/app_apk/apk1.apk";
            loader = new DexClassLoader(dexPath, dexOutputDir.getAbsolutePath(), null, getClassLoader());
            clazz = loader.loadClass("com.temp.rlapk1.UIUtil");
            Method method = clazz.getMethod("getTextString", Context.class); // 第2个参数是类型.class，例如View.class, String.class
            String str = (String)method.invoke(null, this);  // 这个this是插件apk的UIUtil
            tvTest.setText(str);
            method = clazz.getMethod("getImageDrawable", Context.class);
            Drawable drawable = (Drawable)method.invoke(null, this);
            ivTest.setBackground(drawable);
//         method = clazz.getMethod("getLayout", Context.class);
//         View view = (View)method.invoke(null, this);
//         layout.addView(view);
        }catch(Exception e){
            Log.i("Loader", "error:"+ Log.getStackTraceString(e));
        }
    }
}
