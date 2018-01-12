package com.xinge.chat.module.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.xinge.chat.R;
import com.xinge.chat.injector.component.DaggerPresenterComponent;
import com.xinge.chat.injector.module.PresenterModule;
import com.xinge.chat.module.base.BaseActivity;
import com.xinge.chat.module.contracts.ContractsActivity;
import com.xinge.chat.util.data.BitmapUtil;

import java.util.ArrayList;

public class RegisterActivity extends BaseActivity<RegisterPresenter> implements IRegisterView {

    private EditText etName;
    private EditText etPassword;
    private ImageView ivLogo;
    private final int REQUEST_FROM_PICTURES = 1;    // startActivityForResult, 选择头像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText)findViewById(R.id.et_reg_name);
        etPassword = (EditText)findViewById(R.id.et_reg_password);
        ivLogo = (ImageView) findViewById(R.id.iv_reg_logo);
        Button btnSubmit = (Button) findViewById(R.id.btn_register);
        btnSubmit.setOnClickListener(submitListener);
        ivLogo.setOnClickListener(choosePicListener);
    }

    @Override
    protected void initInjector() {
        DaggerPresenterComponent
                .builder()
                .presenterModule(new PresenterModule(this))
                .build()
                .injectRegisterActivity(this);
    }

    // 选择头像
    ImageView.OnClickListener choosePicListener = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            PhotoPickerIntent intent = new PhotoPickerIntent(RegisterActivity.this);
            intent.setSelectModel(SelectModel.SINGLE);
            intent.setShowCarema(true);  // 可以自拍，没加上处理方法
            ArrayList<String> imagePatchList = new ArrayList<>();
            intent.setSelectedPaths(imagePatchList); // 已选中的照片地址， 用于回显选中状态
            // intent.setImageConfig(config);   // ImageConfig config 可以用于过滤图片的类型、大小、尺寸等等，具体看该类里的方法。
            startActivityForResult(intent, REQUEST_FROM_PICTURES);
        }
    };

    // 选择头像
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK ){
            switch (requestCode) {
                case REQUEST_FROM_PICTURES:
                    ArrayList<String> imagePatchList = new ArrayList<>(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                    String imagePath = imagePatchList.get(0);
                    Log.e("头像地址=", imagePath);
                    Bitmap bitmap = BitmapUtil.getNativeImage(imagePath);
                    ivLogo.setImageBitmap(bitmap);
                    break;
                default:
                    break;
            }
        }
    }

    // 提交注册
    Button.OnClickListener submitListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = etName.getText().toString();
            String password = etPassword.getText().toString();
            Bitmap bitmap = ((BitmapDrawable)ivLogo.getDrawable()).getBitmap();
            if (bitmap==null) {
                ivLogo.setImageDrawable(getResources().getDrawable(R.drawable.choose_logo));
                bitmap = ((BitmapDrawable)ivLogo.getDrawable()).getBitmap();
            }
            mPresenter.registerUser(name, password, bitmap);
        }
    };

    @Override
    public void regSuccess() {
        Toast.makeText(this, "注册成功！", Toast.LENGTH_LONG).show();
        startActivity(new Intent(RegisterActivity.this, ContractsActivity.class));
        finish();
    }
}
