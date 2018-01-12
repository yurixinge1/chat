package com.xinge.chat.util.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* Apollo服务器的两个问题未解决：
 * 1. 发给对方的消息服务器也会推送给自己。
 *    处理方法：发送的字节里加入客户端ID（固定长度的、唯一的）. 从收到的消息里抽取ID，如果和本地客户端ID相同，那么handler不必再发消息出去。
 * 2. 离线消息接收很不稳定，经常丢失或不定时间接收到。（怎样干脆做到只给在线的订阅者推送消息？）
 *    处理方法：找开源服务器软件或自己开发或考虑XMTT协议。
 *
 *    待清晰理解setCleanSession()、setKeepAliveInterval()、setRetained、qos，怎样自己设计要有个概念。
 *    另外要做个mqtt推送例子和XMTT聊天例子
 */
public class MqttUtil {
    private MqttClient client;
    private Handler handler;                // 从UI主线程传过来
    private String myTopic = "test/topic";
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;
    private MqttTopic topic;
    private MqttMessage message;

    public MqttUtil(Handler handler) {
        this.handler = handler;
        init();
        startReconnect();
    }

    private void init() {
        String host = "tcp://10.0.2.2:61613";
        // String host = "tcp://192.168.1.101:61613";
        String userName = "admin";
        String passWord = "password";
        try {
            // 第2个参数为客户端ID，一般以客户端唯一标识符表示
            client = new MqttClient(host, "test1", new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setUserName(userName);
            options.setPassword(passWord.toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);

            // 设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("Mqtt", "客户端连接丢失。");
                }

                // 消息发送完成后
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }

                // 收到消息后（未区分是否是自己发给自己的消息）
                @Override
                public void messageArrived(String topicName, MqttMessage message) throws Exception {
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    byte[] msgBytes = message.getPayload();
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("msg", msgBytes);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            });

            topic = client.getTopic(myTopic);
            message = new MqttMessage();
            message.setQos(2);
            message.setRetained(true);  // 服务器是否保存消息
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 只有一条线程的线程池。每隔10秒检查客户端如果已断开则开条新线程重连。可以换Service
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                client.connect(options);
                                Message msg = handler.obtainMessage();
                                msg.what = 998;   // 连接成功
                                handler.sendMessage(msg);
                                client.subscribe(myTopic, 2);  // 第2个参数qos服务质量：0, 1 或 2
                            } catch (Exception e) {
                                e.printStackTrace();
                                Message msg = handler.obtainMessage();
                                msg.what = 999;
                                handler.sendMessage(msg);
                            }
                        }
                    }).start();
                }
            }
        }, 0, 10000, TimeUnit.MILLISECONDS);  // 延时0毫秒，每10000毫秒执行一次
    }

    // 发送消息
    public void sendMessage(byte[] msg) {
        message.setPayload(msg);
        try {
            MqttDeliveryToken token = topic.publish(message);
            token.waitForCompletion();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // 结束与Mqtt服务器的连接
    public void shutdown() {
        try {
            scheduler.shutdown();
            client.unsubscribe(myTopic);
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
