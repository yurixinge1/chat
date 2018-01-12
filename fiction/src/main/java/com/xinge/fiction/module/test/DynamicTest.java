package com.xinge.fiction.module.test;

import android.util.Log;

public class DynamicTest implements IDynamic {
    public String helloworld() {
        Log.e("hello=", "hello world  xx");
        return "hello world  xx";

    }
}
