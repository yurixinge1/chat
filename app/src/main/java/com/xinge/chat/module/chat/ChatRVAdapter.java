package com.xinge.chat.module.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xinge.chat.R;
import com.xinge.chat.module.chat.model.Msg;
import com.xinge.chat.util.data.StringUtil;
import java.util.List;

class ChatRVAdapter extends RecyclerView.Adapter<ChatRVAdapter.ViewHolder> {
    private List<Msg> msgList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout llSent;
        private LinearLayout llReceived;
        private TextView tvSentText;
        private TextView tvReceivedText;
        private ImageView ivSentImage;
        private ImageView ivReceivedImage;
        private ViewHolder(View view){
            super(view);
            llReceived = (LinearLayout)view.findViewById(R.id.llReceived);
            llSent = (LinearLayout)view.findViewById(R.id.llSent);
            tvReceivedText = (TextView)view.findViewById(R.id.tvReceivedText);
            tvSentText = (TextView)view.findViewById(R.id.tvSentText);
            ivReceivedImage = (ImageView)view.findViewById(R.id.ivReceivedImage);
            ivSentImage = (ImageView)view.findViewById(R.id.ivSentImage);
        }
    }

    ChatRVAdapter(List<Msg> msgList){
        this.msgList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder Holder,int position) {
        Msg msg = msgList.get(position);
        byte[] msgBytes = msg.getMsg();
        int len = msgBytes.length;

        if (msg.getType()==1) { // 收到信息
            Holder.llSent.setVisibility(View.GONE);
            Holder.llReceived.setVisibility(View.VISIBLE);
            if (msgBytes[0]==97) { // 文字
                Holder.ivReceivedImage.setVisibility(View.GONE);
                byte[] b2 = new byte[len-1];
                System.arraycopy(msgBytes, 1, b2, 0, len-1);
                String text = StringUtil.unZip(b2); // b2[]解压回string。如果没压缩过，则String text = new String(b2);
                Holder.tvReceivedText.setText(text);
            } else if (msgBytes[0]==98) { // 图片
                Holder.tvReceivedText.setVisibility(View.GONE);
                Bitmap bp = BitmapFactory.decodeByteArray(msgBytes, 1, len-1);
                Holder.ivReceivedImage.setImageBitmap(bp);
            }
        } else if (msg.getType()==2) { // 发送信息
            Holder.llSent.setVisibility(View.VISIBLE);
            Holder.llReceived.setVisibility(View.GONE);
            if (msgBytes[0] == 97) { // 文字
                Holder.ivSentImage.setVisibility(View.GONE);
                byte[] b2 = new byte[len-1];
                System.arraycopy(msgBytes, 1, b2, 0, len-1);
                String text = StringUtil.unZip(b2); // b2[]解压回string
                Holder.tvSentText.setText(text);
            } else if (msgBytes[0] == 98) { // 图片
                Holder.tvSentText.setVisibility(View.GONE);
                Bitmap bp = BitmapFactory.decodeByteArray(msgBytes, 1, len-1);
                Holder.ivSentImage.setImageBitmap(bp);
            }
        }
    }

    @Override
    public int getItemCount(){
        return msgList.size();
    }
}
