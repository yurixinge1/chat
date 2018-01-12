package com.xinge.chat.module.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.xinge.chat.R;
import com.xinge.chat.injector.component.DaggerPresenterComponent;
import com.xinge.chat.injector.module.PresenterModule;
import com.xinge.chat.module.base.BaseActivity;
import com.xinge.chat.module.chat.model.Msg;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity<ChatPresenter> implements IChatView {

    private EditText etText;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Msg> mMsgList;

    // 选择图片：startActivityForResult返回码
    private final int REQUEST_FROM_PICTURES = 1;     // 选择相片
    // private final int REQUEST_TAKE_PICTURE = 2;       // 拍照
    // private final int REQUEST_PREVIEW_PICTURE = 3;    // 预览

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Button btnSendText = (Button)findViewById(R.id.btnSendText);
        btnSendText.setOnClickListener(sendTextMsgListener);
        Button btnSendImage = (Button)findViewById(R.id.btnSendImage);
        btnSendImage.setOnClickListener(sendImageMsgListener);
        etText = (EditText)findViewById(R.id.etText);

        mMsgList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.rvChat);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ChatRVAdapter(mMsgList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initInjector() {
        DaggerPresenterComponent
                .builder()
                .presenterModule(new PresenterModule(this,this))
                .build()
                .injectChatActivity(this);
    }

    // 发送文字
    Button.OnClickListener sendTextMsgListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPresenter.sendText(etText.getText().toString());
            etText.setText("");
        }
    };

    // 选择图片
    Button.OnClickListener sendImageMsgListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            PhotoPickerIntent intent = new PhotoPickerIntent(ChatActivity.this);
            intent.setSelectModel(SelectModel.MULTI);
            intent.setShowCarema(true);
            intent.setMaxTotal(9);
            ArrayList<String> imagePatchList = new ArrayList<>();
            intent.setSelectedPaths(imagePatchList); // 已选中的照片地址， 用于回显选中状态
            // intent.setImageConfig(config);   // ImageConfig config 可以用于过滤图片的类型、大小、尺寸等等，具体看该类里的方法。
            startActivityForResult(intent, REQUEST_FROM_PICTURES);
        }
    };

    // 发送图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==Activity.RESULT_OK ){
            switch (requestCode) {
                case REQUEST_FROM_PICTURES:
                    ArrayList<String> imagePatchList = new ArrayList<>(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                    mPresenter.sendImage(imagePatchList);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        super.onDestroy();
    }

    // 插入记录到RecyleView并显示
    @Override
    public void insertRVItem(Msg msg) {
        mMsgList.add(msg);
        mAdapter.notifyItemInserted(mMsgList.size()-1);
        mRecyclerView.scrollToPosition(mMsgList.size()-1);
    }
}
