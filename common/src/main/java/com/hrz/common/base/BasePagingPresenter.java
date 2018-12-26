package com.hrz.common.base;

import android.app.Activity;
import android.app.Service;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.view.View;

import com.hrz.common.ibase.IModel;
import com.hrz.common.ibase.IPresenter;
import com.hrz.common.ibase.IView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * ================================================
 * 基类 Presenter
 * ================================================
 */
public abstract class BasePagingPresenter<M extends IModel, V extends IView,P>implements IPagingPresenter<P>, LifecycleObserver {
    protected final String TAG = this.getClass().getSimpleName();
    protected CompositeDisposable mCompositeDisposable;
    protected M mModel;
    protected V mRootView;
    protected boolean mHasMoreData=true;

    public BasePagingPresenter() {
        onStart();
    }

    public BasePagingPresenter(M model, V rootView) {
        this.mModel = model;
        this.mRootView = rootView;
        onStart();
    }

    @Override
    public void onStart() {
//        if (useEventBus())//如果要使用 EventBus 请将此方法返回 true
    }

    /**
     * 在框架中 {@link Activity#onDestroy()} 时会默认调用 {@link IPresenter#onDestroy()}
     */
    @Override
    public void onDestroy() {
//        if (useEventBus())//注销
        unDispose();//解除订阅
        if (mModel != null)
            mModel.onDestroy();
        this.mModel = null;
        this.mCompositeDisposable = null;
    }

    /**
     * 只有当 {@code view} 实现了 {@link LifecycleOwner} 时, 此方法才会被调用
     * 所以当您想在 {@link Service} 以及一些自定义 {@link View} 或自定义类中使用 {@code Presenter} 时
     * 您也将不能继续使用 {@link OnLifecycleEvent} 绑定生命周期
     *
     * @param owner link {@link SupportActivity} and {@link Fragment}
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner) {
        /**
         * 注意, 如果在这里调用了 {@link #onDestroy()} 方法, 会出现某些地方引用 {@code mModel} 为 null 的情况
         * 比如如果你声明了多个 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY) 时在其他 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
         * 中引用了 {@code mModel} 也可能会出现此情况
         */
        owner.getLifecycle().removeObserver(this);
    }

    /**
     * 是否使用 EventBus
     * Art 核心库现在并不会依赖某个 EventBus, 要想使用 EventBus, 还请在项目中自行依赖对应的 EventBus
     * 现在支持两种 EventBus, greenrobot 的 EventBus 和畅销书 《Android源码设计模式解析与实战》的作者 何红辉 所作的 AndroidEventBus
     * 确保依赖后, 将此方法返回 true, Art 会自动检测您依赖的 EventBus, 并自动注册
     * 这种做法可以让使用者有自行选择三方库的权利, 并且还可以减轻 Art 的体积
     *
     * @return 返回 {@code true} (默认为 {@code true}), Art 会自动注册 EventBus
     */
    public boolean useEventBus() {
        return true;
    }

    /**
     * 将 {@link Disposable} 添加到 {@link CompositeDisposable} 中统一管理
     * 可在 {@link Activity#onDestroy()} 中使用 {@link #unDispose()} 停止正在执行的 RxJava 任务,避免内存泄漏(框架已自行处理)
     *
     * @param disposable
     */
    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有 Disposable 放入集中处理
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    public void unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();//保证 Activity 结束时取消所有正在执行的订阅
        }
    }


    public abstract void refresh(P param);

    public abstract void loadingNext();

    @Override
    public boolean hasMoreData() {
        return mHasMoreData;
    }
}
