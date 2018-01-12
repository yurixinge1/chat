package com.xinge.chat.module.login;

public interface ILoginView {
    // 如果之前的登录保存了用户信息，Presenter调用此方法显示用户名和密码
    void showUserInfo(String name, String password);
    // 登录成功后，保存用户信息（调用Presenter的方法）。
    void saveUserInfo();
    // 用户名或密码错误，或其它网络原因等
    void showLoginError();
}
