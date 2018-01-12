package com.xinge.chat.module.contracts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.xinge.chat.R;
import com.xinge.chat.model.User;
import com.xinge.chat.util.data.BitmapUtil;
import java.util.ArrayList;

class ContractsRVAdapter extends RecyclerView.Adapter {  // RecyclerView.Adapter<ContractsRVAdapter.ViewHolder>
    private ArrayList<User> mUsers;
    private BitmapUtil.BitmapCache mBitMapCacheUtil = new BitmapUtil.BitmapCache();
    private final int TYPE_ITEM = 1;       // 正常数据行
    boolean mNoMore = false;        // true时说明已全部加载完，底部不显示附加item
    boolean mResumeLoadImage = true;
    private ArrayList<ViewHolderItem> vhiList= new ArrayList<>();  // ? extends ViewHolder
    private String mHostUrl = "10.0.2.2"; // 测试需要

    ContractsRVAdapter(ArrayList<User> data) {
        mUsers = data;
    }

    @Override
    public int getItemCount() {
        return mUsers.size() == 0 ? 0 : mUsers.size() + 1;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolderItem) {
            //
            ((ViewHolderItem) holder).ivPicture.setImageDrawable(null);
        } else {
            System.out.println("回收ViewHolderBottom.");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  // viewType默认等于0
        System.out.println("创建ViewHolder, viewType=" + String.valueOf(viewType));
        View view;
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_contracts_item, parent, false);
            return new ViewHolderItem(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_contracts_bottom, parent, false);
            return new ViewHolderBottom(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position+1==getItemCount()) ? 2 : TYPE_ITEM;
    }

    //
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            System.out.println("onBindViewHolder, position=" + position);
            final String url = mUsers.get(position).getPicUrlPath().replaceFirst("localhost", mHostUrl);
            ViewHolderItem vhi = (ViewHolderItem) holder;
            vhi.tvName.setText(mUsers.get(position).getName());
            vhi.tvSignature.setText(mUsers.get(position).getSignature());
            if (mResumeLoadImage)   mBitMapCacheUtil.setBitmap(vhi.ivPicture, url);  // 等滑动停止后再下载
            vhiList.add(vhi);
        } else {
            if (mNoMore)   ((ViewHolderBottom) holder).tvLoadMore.setVisibility(View.GONE);
        }
    }

    void reloadBitmap(int first, int last) {

        while (first<=last) {
            if (first>=mUsers.size())   return;  // 因为有last=mUsers.size()的情况
            ViewHolderItem vhi = vhiList.get(first);
            if (vhi.ivPicture.getDrawable()==null) {
                System.out.println("reloadBitmap, first=" + first + ", last=" + last);
                String url = mUsers.get(first).getPicUrlPath();
                url = url.replaceFirst("localhost", mHostUrl);
                mBitMapCacheUtil.setBitmap(vhi.ivPicture, url);
            }
            first++;
        }
    }

    private class ViewHolderItem extends RecyclerView.ViewHolder {
        ImageView ivPicture;
        TextView tvName;
        TextView tvSignature;
        ViewHolderItem(View itemView) {
            super(itemView);
            ivPicture = (ImageView)itemView.findViewById(R.id.rv_iv_contracts_picture);
            tvName = (TextView)itemView.findViewById(R.id.rv_iv_contracts_name);
            tvSignature = (TextView)itemView.findViewById(R.id.rv_iv_contracts_signature);
        }
    }
    private class ViewHolderBottom extends RecyclerView.ViewHolder {
        TextView tvLoadMore;
        ViewHolderBottom(View itemView) {
            super(itemView);
            tvLoadMore = (TextView)itemView.findViewById(R.id.rv_iv_contracts_loadmore);
        }
    }
}
