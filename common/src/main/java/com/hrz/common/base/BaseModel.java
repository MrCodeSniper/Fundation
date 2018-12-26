package com.hrz.common.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import com.hrz.common.ibase.IModel;
import com.hrz.common.ibase.IRepositoryManager;

public class BaseModel implements IModel,LifecycleObserver {

    protected IRepositoryManager mRepositoryManager;//用于管理网络请求层, 以及数据缓存层

    public BaseModel(IRepositoryManager repositoryManager) {
        this.mRepositoryManager = repositoryManager;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner) {
        owner.getLifecycle().removeObserver(this);
    }


    @Override
    public void onDestroy() {
        mRepositoryManager = null;
    }
}
