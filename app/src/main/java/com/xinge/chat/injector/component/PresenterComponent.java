package com.xinge.chat.injector.component;

import com.xinge.chat.injector.module.PresenterModule;
import com.xinge.chat.module.chat.ChatActivity;
import com.xinge.chat.module.chat.ChatPresenter;
import com.xinge.chat.module.contracts.ContractsActivity;
import com.xinge.chat.module.contracts.ContractsPresenter;
import com.xinge.chat.module.login.LoginActivity;
import com.xinge.chat.module.login.LoginPresenter;
import com.xinge.chat.module.register.RegisterActivity;
import com.xinge.chat.module.register.RegisterPresenter;
import com.xinge.chat.util.net.MqttUtil;

import dagger.Component;

/*
 * 也可以为每个Activity建一个Component文件
 */
@Component(modules = {PresenterModule.class})
public interface PresenterComponent {

    // ----- LoginActivity -----
    LoginPresenter provideLoginPresenter();
    void injectLoginActivity(LoginActivity activity);


    // ----- ChatActivity -----
    ChatPresenter  provideChatPresenter();
    void injectChatActivity(ChatActivity activity);


    // ----- ContractsActivity -----
    ContractsPresenter provideContractsPresenter();
    void injectContractsActivity(ContractsActivity activity);

    // ----- RegisterActivity -----
    RegisterPresenter provideRegisterPresenter();
    void injectRegisterActivity(RegisterActivity activity);
}
