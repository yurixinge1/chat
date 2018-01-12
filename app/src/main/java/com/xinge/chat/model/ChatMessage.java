package com.xinge.chat.model;

import java.util.Date;

// 接收J2EE服务器返回的消息
public class ChatMessage {
    private int id;
    private int userId;
    private int friendId;
    private String subscribeTopic;
    private int type;                   // 1.文本 2.图片
    private String message;
    private int status;
    private Date sentDate;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }
    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getSubscribeTopic() { return subscribeTopic; }
    public void setSubscribeTopic(String subTopic) { this.subscribeTopic = subTopic; }

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public Date getSentDate() { return sentDate; }
    public void setSentDate(Date sDate) { this.sentDate = sDate; }

    @Override
    public String toString() {
        return "ChatMessage [id=" + id + ", userId=" + userId + ", message=" + message + "]";
    }
}
