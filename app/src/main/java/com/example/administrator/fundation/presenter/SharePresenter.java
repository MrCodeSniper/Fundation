package com.example.administrator.fundation.presenter;

import com.example.administrator.fundation.contract.ShareContract;
import com.example.administrator.fundation.model.entity.ShareEntity;
import com.hrz.common.base.BasePagingPresenter;
import com.hrz.common.base.BasePresenter;
import com.hrz.common.config.Api;
import com.hrz.common.ibase.IView;

import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SharePresenter extends BasePresenter<ShareContract.Model,ShareContract.View> {

    public SharePresenter(ShareContract.Model model, ShareContract.View rootView) {
        super(model, rootView);
    }



    //传进来的对象结束时记得销毁

    public void requestShare(Map<String,Object> map){
        if(mModel == null || mRootView == null) return;
        mRootView.showLoading();
        mModel.getSearchHotWords(map)
                .subscribeOn(Schedulers.io())
                //??? 能单独抽出来对象吗
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        addDispose(disposable);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShareEntity>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        addDispose(disposable);//?????
                    }

                    @Override
                    public void onNext(ShareEntity entity) {
                        if(entity.getData() == null ||
                                entity.getData().getData() == null ||
                                    entity.getData().getData().size() == 0){
                            mRootView.getShareContentEmpty("数据为空");
                        }else if(entity.getData().getData().size()<Api.COMMON_PAGE_SIZE){
                           mRootView.noMoreShareContent();
                        }else {
                            mRootView.getShareContentSuccess(entity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRootView.getShareContentFail(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }





}
