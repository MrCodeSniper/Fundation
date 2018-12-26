package com.hrz.common.base;

import android.app.Activity;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hrz.common.data.cache.Cache;
import com.hrz.common.ibase.IModel;
import com.hrz.common.ibase.IPaging;
import com.hrz.common.ibase.IPresenter;

public abstract class BaseActivity<P extends IPresenter,M extends IModel> extends Activity implements IPaging {


    private Cache<String, Object> mCache;

    @Nullable
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null
    @Nullable
    protected M mModel;
    @Nullable
    protected PagedList.Config mPageConfig;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intPresenter();
        initPagingConfig();
        int layoutResID = setViewId(savedInstanceState);
        if (layoutResID != 0) {
            setContentView(layoutResID);
        }
        initView();
        initData(savedInstanceState);
    }

     protected abstract void initView();
     protected abstract void intPresenter();
     protected abstract int setViewId(Bundle savedInstanceState);
     protected abstract void initData(Bundle savedInstanceState);


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
        if (mModel != null)
            mModel.onDestroy();//释放资源
        this.mModel = null;
    }
}
