package com.xinge.chat.injector.module;

import com.xinge.chat.module.chat.ChatActivity;
import com.xinge.chat.module.chat.ChatPresenter;
import com.xinge.chat.module.chat.IChatView;
import com.xinge.chat.module.contracts.ContractsPresenter;
import com.xinge.chat.module.contracts.IContractsView;
import com.xinge.chat.module.login.ILoginView;
import com.xinge.chat.module.login.LoginPresenter;
import com.xinge.chat.module.register.IRegisterView;
import com.xinge.chat.module.register.RegisterPresenter;
import dagger.Module;
import dagger.Provides;

/*
 * 不知该不该每个Activity建一个Module文件
 */
@Module
public class PresenterModule {

    // ----- LoginActivity -----
    private ILoginView mLoginView;
    public PresenterModule(ILoginView mView){
        this.mLoginView = mView;
    }
    @Provides
    LoginPresenter provideLoginPresenter(){
        return new LoginPresenter(mLoginView);
    }


    // ----- ChatActivity -----
    private IChatView mChatView;
    public PresenterModule(IChatView mView, ChatActivity ca) { this.mChatView = mView; }
    @Provides
    ChatPresenter provideChatPresenter(){
        return new ChatPresenter(mChatView);
    }


    // ----- ContractsActivity -----
    private IContractsView mContractsView;
    public PresenterModule(IContractsView mView){
        this.mContractsView = mView;
    }
    @Provides
    ContractsPresenter provideContractsPresenter(){
        return new ContractsPresenter(mContractsView);
    }

    // ----- RegisterActivity -----
    private IRegisterView mRegisterView;
    public PresenterModule(IRegisterView mView){
        this.mRegisterView = mView;
    }
    @Provides
    RegisterPresenter provideRegisterPresenter(){
        return new RegisterPresenter(mRegisterView);
    }
}
