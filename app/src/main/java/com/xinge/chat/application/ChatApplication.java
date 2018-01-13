package com.xinge.chat.application;

import android.support.multidex.MultiDexApplication;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.squareup.leakcanary.LeakCanary;
import com.xinge.chat.util.data.FileUtil;

import java.io.File;

public class ChatApplication extends MultiDexApplication {
    private static ChatApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;   // 应有启动时已自动创建了一个ChatApplication

	// 内存泄露检测
        if (!LeakCanary.isInAnalyzerProcess(this))    LeakCanary.install(this);
        initLogger();
        initFiles();
    }

    public static ChatApplication getApp() {
        return app;
    }

    // 初始化Logger2.1.1，旧版的不用初始化直接就可以用
    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  //（可选）是否显示线程信息。 默认值为true
                .methodCount(2)         // （可选）要显示的方法行数。 默认2
                .methodOffset(7)        // （可选）隐藏内部方法调用到偏移量。 默认5
                //.logStrategy()     //（可选）更改要打印的日志策略。 默认LogCat
                .tag("Logger")         //（可选）每个日志的全局标记。 默认PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    private void initFiles() {

    }
}
