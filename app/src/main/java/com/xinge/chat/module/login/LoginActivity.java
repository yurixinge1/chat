package com.xinge.chat.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.xinge.chat.R;
import com.xinge.chat.injector.component.DaggerPresenterComponent;
import com.xinge.chat.injector.module.PresenterModule;
import com.xinge.chat.module.base.BaseActivity;
import com.xinge.chat.module.contracts.ContractsActivity;
import com.xinge.chat.module.register.RegisterActivity;
import com.xinge.chat.util.data.BitmapUtil;
import com.xinge.chat.util.hotfix.HotFix;

public class LoginActivity extends BaseActivity<LoginPresenter> implements ILoginView {

    private EditText etName;
    private EditText etPassword;
    private CheckBox cbAutoLogin;
    private TextView tvRegister;
    public HotFix hf = new HotFix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView ivLoginLogo = (ImageView) findViewById(R.id.iv_login_logo);
        etName = (EditText)findViewById(R.id.et_login_name);   // 未限制输入
        etPassword = (EditText)findViewById(R.id.et_login_password);
        cbAutoLogin = (CheckBox)findViewById(R.id.cb_login_auto_login);
        tvRegister = (TextView)findViewById(R.id.tv_login_register);
        Button btnLogin = (Button) findViewById(R.id.btn_login);

        BitmapUtil.BitmapCache bCache = new BitmapUtil.BitmapCache();
        bCache.setBitmap(ivLoginLogo, "http://10.0.2.2:8080/chat/images/login_logo.jpg");

        mPresenter = new LoginPresenter(this);
        mPresenter.getUserInfo();  // 之前如有选择“自动登录”就读取用户信息出来显示

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login(etName.getText().toString(), etPassword.getText().toString());
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = new Intent(LoginActivity.this, ContractsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void initInjector() {
        DaggerPresenterComponent
                .builder()
                .presenterModule(new PresenterModule(this))
                .build()
                .injectLoginActivity(this);
    }

    @Override
    public void showUserInfo(String name, String password) {
        etName.setText(name);
        etPassword.setText(password);
        cbAutoLogin.setChecked(true);
    }

    @Override
    public void saveUserInfo() {
        mPresenter.saveUserInfo(cbAutoLogin.isChecked(), etName.getText().toString(), etPassword.getText().toString());
    }

    @Override
    public void showLoginError() {
        Toast.makeText(this, "登录失败！", Toast.LENGTH_SHORT).show();
    }
}
