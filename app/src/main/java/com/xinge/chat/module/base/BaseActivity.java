package com.xinge.chat.module.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import javax.inject.Inject;

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements IBaseView {

    // Java泛型不知怎样实例化，也就是不知怎样 T mPresenter = new T(); 所以用Dagger2注入。
    // 泛型T在运行时会擦除，但Dagger2是在编译时生成了DaggerPresenterComponent文件，
    // 可能是编译期间就注入了，网上介绍Dagger2是完全静态的、编译时依赖注入（DI）注入框架。
    // 所以编译时遇到注解@Inject，后台估计生成了很多文件（T已转成了具体类）以便在运行时调用。
    @Inject
    protected T mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInjector();  // 子类实现了方法。
    }

    // 注入mPresenter到子类
    protected abstract void initInjector();
}
