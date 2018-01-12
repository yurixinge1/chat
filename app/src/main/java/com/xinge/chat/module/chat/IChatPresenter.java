package com.xinge.chat.module.chat;

import android.net.Uri;

import java.util.ArrayList;

import okhttp3.RequestBody;

interface IChatPresenter {

    // RxJava发送聊天信息到J2EE服务器
    void sendMessage(RequestBody requestBody);
    // 发送文字到：本地RecyleView、J2EE服务器(调用sendMessage方法)、MQTT服务器
    void sendText(String strText);
    // 发送图片到：本地RecyleView、J2EE服务器(调用sendMessage方法)、MQTT服务器
    // 图片有多选情况
    void sendImage(ArrayList<String> imagePatchList);
}
