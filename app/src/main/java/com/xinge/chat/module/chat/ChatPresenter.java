package com.xinge.chat.module.chat;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import com.xinge.chat.model.ChatMessage;
import com.xinge.chat.module.base.BasePresenter;
import com.xinge.chat.module.chat.model.Msg;
import com.xinge.chat.util.data.BitmapUtil;
import com.xinge.chat.util.data.ByteUtil;
import com.xinge.chat.util.data.StringUtil;
import com.xinge.chat.util.net.MqttUtil;
import com.xinge.chat.util.net.RetrofitUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ChatPresenter extends BasePresenter implements IChatPresenter {
    private RetrofitUtil mRetrofit = RetrofitUtil.getInstance();
    private IChatView mView;
    private MqttUtil mMqtt;
    private ChatHandler mHandler;

    public ChatPresenter(IChatView cv) {
        mView = cv;
        mHandler = new ChatHandler((ChatActivity)mView);
        mMqtt = new MqttUtil(mHandler);
    }

    // 此handler接收来自MQTT的消息
    private static class ChatHandler extends Handler {
        WeakReference<ChatActivity> mActivity = null;
        ChatHandler(ChatActivity ca) {
            mActivity = new WeakReference<>(ca);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatActivity ca = mActivity.get();
            switch (msg.what) {
                case 1:
                    Log.d("Mqtt", "收到来自MQTT服务器的消息。");
                    byte[] msgBytes = msg.getData().getByteArray("msg");
                    Msg msg1 = new Msg(Msg.MSG_TYPE_RECEIVE, msgBytes);
                    ca.insertRVItem(msg1);
                    break;
                case 998:
                    Log.d("Mqtt", "连接MQTT服务器成功。");
                    break;
                case 999:
                    Log.d("Mqtt", "连接MQTT服务器失败。");
                    break;
            }
        }
    }

    @Override
    public void sendMessage(RequestBody requestBody) {
        mRetrofit.send(requestBody).subscribe(new Observer<ChatMessage>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(ChatMessage responseInfo) {
                // 测试用
                Log.d("J2EE", "收到来自J2EE服务器的消息。");
//                        String str = responseInfo.getMessage();
//                        byte[] res = Base64.decode(str.getBytes(), Base64.DEFAULT);
//                        Msg msg = new Msg(Msg.MSG_TYPE_SEND, res);
//                        mView.insertRVItem(msg);
            }
            @Override
            public void onError(Throwable e) {
                Log.e("ChatPresenter：", "onError()");
                e.printStackTrace();
            }
            @Override
            public void onComplete() {
                Log.e("ChatPresenter：", "onComplete()");
            }
        });
    }

    // 发送文字
    @Override
    public void sendText(String strText) {
        byte[] b = StringUtil.zip(strText); // 字符重复率低的情况下压缩反而会使压缩后的字符串变大
        byte[] bText = ByteUtil.setFlag(b, (byte)('a'));
        sendMsgToRV(bText);       // 本地RecyleView，不需要压缩和转换成Base64
        sendMsgToMQTT(bText);     // MQTT服务器
        sendMsgToJ2EE(bText);     // J2EE服务器（存储）
    }

    // 发送图片
    @Override
    public void sendImage(ArrayList<String> imagePatchList) {
        for (int i=0; i<imagePatchList.size(); i++) {
            String imagePath = imagePatchList.get(i);
            Bitmap bitmap = BitmapUtil.getNativeImage(imagePath);
            // bitmap = BitmapUtil.compressBitmap(bitmap);
            byte[] b = BitmapUtil.BitmapToBytes(bitmap);
            byte[] bImage = ByteUtil.setFlag(b, (byte) ('b'));
            sendMsgToRV(bImage);
            sendMsgToMQTT(bImage);
            sendMsgToJ2EE(bImage);
        }
    }

    // 发送消息到J2EE服务器
    private void sendMsgToJ2EE(byte[] bMsg) {
        // 要将byte[]转换成Base64字符串再发送，猜想原因是和加密一样byte[]里有乱码。不再对msg再压缩，避免无法还原。
        String msg = new String(Base64.encode(bMsg, Base64.DEFAULT));
        JSONObject jo = new JSONObject();
        try {
            jo.put("userId", 1);
            jo.put("friendId", 2);
            jo.put("subscribeTopic", "test/topic");
            jo.put("type", 1);
            jo.put("message", msg);
            jo.put("status", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jo.toString());
        sendMessage(requestBody); // RxJava+Retrofit发送消息
    }

    // 发送消息到本地RecyleView显示
    private void sendMsgToRV(byte[] bMsg) {
        Msg msg = new Msg(Msg.MSG_TYPE_SEND, bMsg);
        mView.insertRVItem(msg);
    }

    // 发送消息到Mqtt服务器（Apollo）
    private void sendMsgToMQTT(byte[] bMsg) {
        mMqtt.sendMessage(bMsg);
    }

    void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);  // mHandler是独立的，它是否回收不影响mMqtt、mPresenter和ChatActivity对象，虽然被它们引用。
            mHandler = null;                            // 这样操作是因为不知主线程的Looper什么时候释放对它的引用。
        }
        if (mMqtt != null) {
            mMqtt.shutdown();  // 关闭后还会发起最后一次线程执行。
            mMqtt = null;      // 强引用对象，即使设为null了，GC也要过点时间再回收它，所以它里面的线程还会再运行一会。而且线程未结束，mMqtt对象也无法被回收。
        }
        // mView = null;   // mView=null不会对ChatActivity对象有影响,mView只是个引用
    }
}
