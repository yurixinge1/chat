package com.xinge.chat.module.contracts;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xinge.chat.R;
import com.xinge.chat.injector.component.DaggerPresenterComponent;
import com.xinge.chat.injector.module.PresenterModule;
import com.xinge.chat.model.User;
import com.xinge.chat.module.base.BaseActivity;
import java.util.ArrayList;
import java.util.Collections;

public class ContractsActivity extends BaseActivity<ContractsPresenter> implements IContractsView {

    private Handler mHandler = new Handler();
    private RecyclerView mRecyclerView;
    private ContractsRVAdapter mAdapter;
    private ArrayList<User> mUserList;
    private boolean  isLoading = false;
    private LinearLayoutManager mLayoutManager;
    private String mUserId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contracts);

        mUserList = new ArrayList<>();
        mAdapter = new ContractsRVAdapter(mUserList);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvContracts);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(rvScrollListener);

        mPresenter.getData(mUserId);
    }

    @Override
    protected void initInjector() {
        DaggerPresenterComponent
                .builder()
                .presenterModule(new PresenterModule(this))
                .build()
                .injectContractsActivity(this);
    }

    // mPresenter获得联系人后回调此方法
    @Override
    public void showFriends(User[] users) {
        Collections.addAll(mUserList, users);
        mAdapter.notifyItemInserted(mUserList.size()-1);
        if (users.length < mPresenter.mLimit)   mAdapter.mNoMore = true;
    }

    // RecylerView的滑动事件
    RecyclerView.OnScrollListener rvScrollListener = new RecyclerView.OnScrollListener() {
        // SCROLL_STATE_IDLE = 0 没有滑动；SCROLL_STATE_DRAGGING = 1 正在滑动； SCROLL_STATE_SETTLING = 2 滑动再松手，由于惯性还会在滑动时的状态。
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // 滑动时暂停图片加载，没滑动时只加载当前屏幕内的图片。
            if (newState==0) { // 停止滑动了
                mAdapter.mResumeLoadImage = true;
                // 检查当前屏幕内的所有item，如果因为滑动而没加载到图片的，重新加载
                int first = mLayoutManager.findFirstVisibleItemPosition();
                int last = mLayoutManager.findLastVisibleItemPosition();
                mAdapter.reloadBitmap(first, last);
            } else {
                mAdapter.mResumeLoadImage = false;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            /*
            不Delay一点时间，可能会发生重复从网络获取数据的情况。因为mPresenter.getData(mUserId);运行在独立的子线程，
            而且不依赖于isLoading = true; 所以系统调度时，可能会先运行子线程的mPresenter.getData(mUserId);一次或多次
            （不断上拉的情况下），再到主线程的isLoading = true;
            解决方法是主线程delay runnable一点时间，这样就不会先运行run()里的代码，而isLoading=true;就可以先执行。
            或者在isLoading = true;后面给一个volatile对象赋值，这样可以保证volatile对象前的代码先执行完再到后面的代码。（未测试）
           */
            if (isLoading)     return;
            int last = mLayoutManager.findLastVisibleItemPosition();
            if (last+1 == mAdapter.getItemCount()) {
                isLoading = true;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.getData(mUserId);
                        isLoading = false;
                    }
                }, 1000);
            }
        }
    };
}
