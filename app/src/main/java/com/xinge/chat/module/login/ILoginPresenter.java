package com.xinge.chat.module.login;

interface ILoginPresenter {
    // RxJava调用服务器接口，将用户名和密码发送给服务器验证。
    void login(String name, String password);
    // 之前登录选择了“自动登录”，读取用户信息显示到登录窗口
    void getUserInfo();
    // 登录时选择了“自动登录”，登录成功后保存用户信息到本地
    void saveUserInfo(Boolean isChecked, String name, String password);
}
