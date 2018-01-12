package com.xinge.chat.module.contracts;

import com.xinge.chat.model.User;

public interface IContractsView {
    // 显示从服务器获取到联系人
    void showFriends(User[] users);
}
