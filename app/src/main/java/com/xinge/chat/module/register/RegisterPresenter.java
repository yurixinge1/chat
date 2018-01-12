package com.xinge.chat.module.register;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import com.xinge.chat.model.User;
import com.xinge.chat.module.base.BasePresenter;
import com.xinge.chat.util.data.BitmapUtil;
import com.xinge.chat.util.net.RetrofitUtil;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RegisterPresenter extends BasePresenter implements IRegisterPresenter {
    private RetrofitUtil mRetrofit = RetrofitUtil.getInstance();
    private IRegisterView mView;
    public RegisterPresenter(IRegisterView rv) {
        mView = rv;
    }

    @Override
    public void register(RequestBody requestBody) {
        mRetrofit.register(requestBody)
        .subscribe(new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("注册用户：", "成功。");
            }

            @Override
            public void onNext(User baseInfo) {
                mView.regSuccess();
            }

            @Override
            public void onError(Throwable e) {
                Log.d("注册用户：", "失败。");
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Override
    public void registerUser(String name, String password, Bitmap bitmap) {
        byte[] bImage = BitmapUtil.BitmapToBytes(bitmap);
        String strImage = new String(Base64.encode(bImage, Base64.DEFAULT));
        JSONObject jo = new JSONObject();
        try {
            jo.put("name", name);
            jo.put("password", password);
            jo.put("age", 30);
            jo.put("picBase64", strImage);
            jo.put("signature", "no signature.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jo.toString());
        register(requestBody);
    }
}
