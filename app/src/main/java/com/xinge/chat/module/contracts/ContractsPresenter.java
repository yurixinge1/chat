package com.xinge.chat.module.contracts;

import com.xinge.chat.model.User;
import com.xinge.chat.module.base.BasePresenter;
import com.xinge.chat.util.net.RetrofitUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ContractsPresenter extends BasePresenter implements IContractsPresenter {

    private RetrofitUtil mRetrofit = RetrofitUtil.getInstance();
    private IContractsView mView;
    private int mOffset = 0;   // 从0开始，
    int mLimit = 20;            // 每次获取mLimit条数据。

    public ContractsPresenter(IContractsView cv) {
        mView = cv;
    }

    void getData(String userId) {
        mRetrofit.contracts(userId, String.valueOf(mOffset), String.valueOf(mLimit))
        .subscribe(new Observer<User[]>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(User[] users) {
                int len = users.length;
                if (len>0 )   {
                    mView.showFriends(users);
                    mOffset += len;
                }
            }

            @Override
            public void onError(Throwable e) {
                /*e.printStackTrace();*/
            }

            @Override
            public void onComplete() {
            }
        });
    }
}
