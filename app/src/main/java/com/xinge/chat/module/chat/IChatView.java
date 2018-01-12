package com.xinge.chat.module.chat;

import com.xinge.chat.module.chat.model.Msg;

public interface IChatView {
    void insertRVItem(Msg msg);  // 插入item到RecyleView并显示。
}
