package com.xinge.chat.util.net;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.xinge.chat.api.NetApi;
import com.xinge.chat.application.ChatApplication;
import com.xinge.chat.model.ChatMessage;
import com.xinge.chat.model.User;
import java.io.File;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private NetApi netApi;
    private static final RetrofitUtil ourInstance = new RetrofitUtil();

    public static RetrofitUtil getInstance() {
        return ourInstance;
    }

    // 单例
    private RetrofitUtil() {
        init();
    }

    private void init() {
        Cache cache = new Cache(new File(ChatApplication.getApp().getCacheDir(), "HttpCache"), 1024*1024*100); // "HttpCache"文件夹如果不存在，Cache会创建
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache)
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        String SERVER_URL = "http://10.0.2.2:8080/chat/";  // "http://192.168.1.101:8080/chat/";
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // 和RxJava关联
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_URL)
                .build();

        netApi = retrofit.create(NetApi.class);
    }

    // 用户登录
    public Observable<User> login(String user, String pass) {
        return netApi.login(user, pass)
                .subscribeOn(Schedulers.io())   // Observer的运行线程
                .observeOn(AndroidSchedulers.mainThread()); // Observable的运行线程
    }

    // 用户注册
    public Observable<User> register(RequestBody requestBody) {
        return netApi.register(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 联系人列表
    public Observable<User[]> contracts(String userId, String offset, String limit) {
        return netApi.contracts(userId, offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 发送消息
    public Observable<ChatMessage> send(RequestBody requestBody) {
        return netApi.send(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
