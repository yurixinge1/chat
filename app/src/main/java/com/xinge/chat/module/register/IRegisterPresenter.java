package com.xinge.chat.module.register;

import android.graphics.Bitmap;
import okhttp3.RequestBody;

interface IRegisterPresenter {
    void registerUser(String name, String password, Bitmap bitmap);
    void register(RequestBody requestBody);
}
