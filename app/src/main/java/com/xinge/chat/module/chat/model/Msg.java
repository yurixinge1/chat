package com.xinge.chat.module.chat.model;

/**
 * Created by xinge on 2017/10/11.
 * ChatActivity用来显示本地消息到RecyleView
 */

public class Msg {
    private int type;   // 1 收到消息   2 发送消息
    private byte[] msg;
    public static final int MSG_TYPE_RECEIVE = 1;    // 收到消息
    public static final int MSG_TYPE_SEND = 2;      // 发送消息

    public Msg(int t, byte[] m) {
        this.type = t;
        this.msg = m;
    }

    public int getType() { return type; }
    public void setType(int type) { this.type = type;}

    public byte[] getMsg() { return msg; }
    public void setMsg(byte[] msg) { this.msg = msg; }
}
