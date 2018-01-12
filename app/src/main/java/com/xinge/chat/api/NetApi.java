package com.xinge.chat.api;

import com.xinge.chat.model.ChatMessage;
import com.xinge.chat.model.User;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import io.reactivex.Observable;
import retrofit2.http.Query;

public interface NetApi {

    // 缓存用
    // 假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，再次请求该URL，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回
    String CACHE_CONTROL_NETWORK = "Cache-Control: public, max-age=3600";

    // 用户登录
    @Headers(CACHE_CONTROL_NETWORK)
    @GET("login")
    Observable<User> login(@Query("name") String name, @Query("password") String password);

    // 用户注册
    @Headers(CACHE_CONTROL_NETWORK)
    @POST("register")
    Observable<User> register(@Body RequestBody requestBody);

    // 分批获取联系人
    @Headers(CACHE_CONTROL_NETWORK)
    @GET("contracts/limit")
    Observable<User[]> contracts(@Query("userId") String userId, @Query("offset") String offset, @Query("limit") String limit);

    // 发送消息, J2EE服务器接收消息
    @Headers(CACHE_CONTROL_NETWORK)
    @POST("receive")
    Observable<ChatMessage> send(@Body RequestBody requestBody);
}
