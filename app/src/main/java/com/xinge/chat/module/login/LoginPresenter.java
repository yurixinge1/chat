package com.xinge.chat.module.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import com.xinge.chat.model.User;
import com.xinge.chat.module.base.BasePresenter;
import com.xinge.chat.module.chat.ChatActivity;
import com.xinge.chat.module.contracts.ContractsActivity;
import com.xinge.chat.util.crypto.AESUtil;
import com.xinge.chat.util.environment.AppInfo;
import com.xinge.chat.util.net.RetrofitUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.content.Context.MODE_PRIVATE;

public class LoginPresenter extends BasePresenter implements ILoginPresenter {
    private RetrofitUtil mRetrofit = RetrofitUtil.getInstance();
    private ILoginView mView;
    private SharedPreferences sp;

    public LoginPresenter(ILoginView lv) {
        mView = lv;
        sp = ((Context)mView).getSharedPreferences("USER_INFO", MODE_PRIVATE);
    }

    // 登录成功调用onNext和onComplete方法，否则调用onError方法。
    @Override
    public void login(String name,String password) {
        mRetrofit.login(name, password)
        .subscribe(new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(User user) {
                Log.v("服务器返回: ", user.toString());
                mView.saveUserInfo();
                Intent intent = new Intent((Context)mView, ContractsActivity.class);
                ((Context)mView).startActivity(intent);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mView.showLoginError(); // 未检查是网络原因，还是用户名/密码错误。
            }

            @Override
            public void onComplete() {
            }
        });
    }

    // 之前登录选择了“自动登录”，读取用户信息显示到登录窗口
    @Override
    public void getUserInfo() {
        if (sp.getBoolean("remenber_password", false)) {
            String name = sp.getString("name", "");
            String password = sp.getString("password", "");
            // String strKey = sp.getString("key", "");   // key放在了so
            String strKey = GetLocalKey.getKey(AppInfo.getSignature((Context)mView));
            // Log.d("从so读出的密钥=", strKey);
            byte[] bKey = Base64.decode(strKey.getBytes(), Base64.DEFAULT);
            String dePassword = AESUtil.Decrypt(password, bKey);
            mView.showUserInfo(name, dePassword);
        }
    }

    // 登录时选择了“自动登录”，登录成功后保存用户信息到本地
    @Override
    public void saveUserInfo(Boolean isChecked, String name, String password) {
        SharedPreferences.Editor editor = sp.edit();
        if (isChecked) {
            editor.putBoolean("remenber_password", true);
            editor.putString("name", name);
            // byte[] key = AESUtil.generateKey();
            // String strKey = new String(Base64.encode(key, Base64.DEFAULT));
            // editor.putString("key", strKey);  // key放在了so
            String strKey = GetLocalKey.getKey(AppInfo.getSignature((Context)mView));
            byte[] bKey = Base64.decode(strKey.getBytes(), Base64.DEFAULT);
            String enPwd = AESUtil.Encrypt(password, bKey);
            editor.putString("password", enPwd);
            editor.apply();
        } else { // 不选择“自动登录”，而且登录成功，就清除之前保存的信息。
            editor.putBoolean("remenber_password", false);
            editor.putString("name", "");
            editor.putString("key", "");
            editor.putString("password", "");
            editor.apply();
        }
    }
}
