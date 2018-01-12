package com.xinge.chat.util.hotfix;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;


public class HotFix {

    public void mergeDex(Context context) {
        try {
            File dexOutputDir = context.getDir("dex_output", 0);
            String dexPath = "/data/data/com.xinge.chat/app_apk/fixbug.jar";

            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            DexClassLoader dexClassLoader = new DexClassLoader(dexPath, dexOutputDir.getAbsolutePath(), null, getClass().getClassLoader());

            // pathList是DexPathList对象，在BaseDexClassLoader里声明的private final对象。
            Object appDexPathList = getDexPathListField(pathClassLoader);
            Object fixDexPathList = getDexPathListField(dexClassLoader);

            Object appDexElements = getDexElements(appDexPathList);
            Object fixDexElements = getDexElements(fixDexPathList);

            Object finalElements = combineArray(fixDexElements, appDexElements); // 合并，并将修复的dex插入到数组最前面
            setFiledValue(appDexPathList, appDexPathList.getClass(), "dexElements", finalElements);  // 重新赋值

            Log.e("HotFix:", "完成");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getDexPathListField(Object classLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(classLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private Object getDexElements(Object obj) throws NoSuchFieldException, IllegalAccessException {
        return getField(obj, obj.getClass(), "dexElements");
    }

    private Object getField(Object obj, Class<?> clz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = clz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }


    // Elements[]里的每个element保存的是dex文件。  DexFile dex = element.dexFile

    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
         Class<?> localClass = arrayLhs.getClass().getComponentType();  // ComponentType是Element[]
         int i = Array.getLength(arrayLhs);
         int j = i + Array.getLength(arrayRhs);
         Object result = Array.newInstance(localClass, j);
         // 因为Elements[]是个数组，所以用Array.newInstance方法来创建？
         // 如果不是数组，那么Object result = localClass.newInstance() ?
         for (int k = 0; k < j; k++) {
             if (k < i) {
                 Array.set(result, k, Array.get(arrayLhs, k)); // 对数组的操作用Array.get或set
             } else {
                 Array.set(result, k, Array.get(arrayRhs, k - i));
             }
         }

         return result;
     }

     /*
    public Object combineArray(Object object, Object object2) {
        Class<?> aClass = Array.get(object, 0).getClass();
        Object obj = Array.newInstance(aClass, 2);
        Array.set(obj, 0, Array.get(object2, 0));
        Array.set(obj, 1, Array.get(object, 0));
        return obj;
    } */

    private void setFiledValue(Object obj, Class<?> claz, String filed, Object value) throws NoSuchFieldException, IllegalAccessException {
        try {
            Field field = claz.getDeclaredField(filed);
            field.setAccessible(true);
            field.set(obj, value);
            //field.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
