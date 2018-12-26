package com.hrz.common.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hrz.common.base.HrzApplication;
import com.hrz.common.data.cache.Cache;
import com.hrz.common.data.cache.CacheType;
import com.hrz.common.data.db.HrzDbManager;
import com.hrz.common.data.db.IDb;
import com.hrz.common.data.disk.DiskManager;
import com.hrz.common.data.disk.IDisk;
import com.hrz.common.data.sp.Isp;
import com.hrz.common.data.sp.SharePreferenceHelper;
import com.hrz.common.data.user.IUserData;
import com.hrz.common.data.user.SpUserDataManager;
import com.hrz.common.ibase.IModel;
import com.hrz.common.ibase.IRepositoryManager;
import com.hrz.common.util.Preconditions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.internal.RxCache;
import retrofit2.Retrofit;

/**
 * ================================================
 * 用来管理所有业务逻辑的仓库,网络请求层,以及数据缓存层,以后可以添加数据库请求层
 * 所有仓库不直接持有却通过 HrzRepositoryManager 拿到需要的请求层做数据处理的好处是
 * 仓库可以直接和对应的请求层解耦,比如网路请求层,需要从 Retrofit 替换为其他网络请求库,这时仓库就不会受到影响
 * ================================================
 */

public class HrzRepositoryManager implements IRepositoryManager {

    private static HrzRepositoryManager mRepositoryManager;

    //处理net
    Retrofit mRetrofit;
    //处理网络Cache-----替代okhttp自带的缓存：可以控制缓存生效时间 灵活的控制是否废除缓存
    RxCache mRxCache;
    //根据类型 判断使用哪种内存缓存策略 LRU - 还是 Intelligent
    Cache.Factory mCachefactory;
    //处理文件 磁盘缓存
    IDisk mDiskProcessor;
    //处理SP
    Isp mSpProcessor;
    //处理数据库
    IDb mDbProcessor;
    //处理用户数据
    IUserData mUserProcessor;


    //Model层  对象的缓存
    private Cache<String, IModel> mRepositoryCache;
    //retrofit service类的缓存
    private Cache<String, Object> mRetrofitServiceCache;
    //rxcache service类的缓存
    private Cache<String, Object> mCacheServiceCache;


    private HrzRepositoryManager() {
        mRetrofit=HrzApplication.mRetrofit;
        mCachefactory=HrzApplication.mHrzCacheFactory;
        mRxCache=HrzApplication.mRxCache;
        mUserProcessor=SpUserDataManager.getsInstance(HrzApplication.mApplication);
        mDbProcessor=HrzDbManager.getInstance(HrzApplication.mApplication);
        mSpProcessor=SharePreferenceHelper.getInstance(HrzApplication.mApplication);
        mDiskProcessor=DiskManager.getInstance(HrzApplication.mApplication);
    }

    //提供给Model层
    public static HrzRepositoryManager getInstance(){
        if(mRepositoryManager==null){
            mRepositoryManager=new HrzRepositoryManager();
        }
        return mRepositoryManager;
    }

    /**
     * 根据传入的 Class 创建对应的仓库
     *
     * @param repository
     * @param <T>
     * @return
     */
    @NonNull
    @Override
    public synchronized <T extends IModel> T createRepository(@NonNull Class<T> repository) {
        Preconditions.checkNotNull(repository, "repository == null");
        if (mRepositoryCache == null)
            mRepositoryCache = mCachefactory.build(CacheType.REPOSITORY_CACHE);
        Preconditions.checkNotNull(mRepositoryCache, "Cannot return null from a Cache.Factory#build(int) method");
        T repositoryInstance = (T) mRepositoryCache.get(repository.getCanonicalName());
        if (repositoryInstance == null) {
            //如果缓存中没有这个Model层实例就通过反射调用其构造方法并加入缓存  （Model层对象的缓存）
            Constructor<? extends IModel> constructor = findConstructorForClass(repository);
            try {
                repositoryInstance = (T) constructor.newInstance(this);
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to invoke " + constructor, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to invoke " + constructor, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Create repository error", e);
            }
            mRepositoryCache.put(repository.getCanonicalName(), repositoryInstance);
        }
        return repositoryInstance;
    }

    /**
     * 根据传入的 Class 创建对应的 Retrofit service
     *
     * @param serviceClass ApiService class
     * @param <T>          ApiService class
     * @return ApiService
     */
    @NonNull
    @Override
    public synchronized <T> T createRetrofitService(@NonNull Class<T> serviceClass) {
        return createWrapperService(serviceClass);
    }

    /**
     * 根据 https://zhuanlan.zhihu.com/p/40097338 对 Retrofit 进行的优化
     *
     * @param serviceClass ApiService class
     * @param <T>          ApiService class
     * @return ApiService
     */
    private <T> T createWrapperService(Class<T> serviceClass) {
        Preconditions.checkNotNull(serviceClass, "serviceClass == null");
        // 通过二次代理，对 Retrofit 代理方法的调用包进新的 Observable 里在 io 线程执行。
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, @Nullable Object[] args)
                            throws Throwable {
                        if (method.getReturnType() == Observable.class) {
                            // 如果方法返回值是 Observable 的话，则包一层再返回
                            return Observable.defer(() -> {
                                final T service = getRetrofitService(serviceClass);
                                // 执行真正的 Retrofit 动态代理的方法
                                return ((Observable) getRetrofitMethod(service, method)
                                        .invoke(service, args))
                                        .subscribeOn(Schedulers.io());
                            }).subscribeOn(Schedulers.single());
                        }
                        // 返回值不是 Observable 的话不处理
                        final T service = getRetrofitService(serviceClass);
                        return getRetrofitMethod(service, method).invoke(service, args);
                    }
                });
    }

    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     *
     * @param serviceClass ApiService class
     * @param <T>          ApiService class
     * @return ApiService
     */
    private <T> T getRetrofitService(Class<T> serviceClass) {
        if (mRetrofitServiceCache == null) {
            mRetrofitServiceCache = mCachefactory.build(CacheType.RETROFIT_SERVICE_CACHE);
        }
        Preconditions.checkNotNull(mRetrofitServiceCache,
                "Cannot return null from a Cache.Factory#build(int) method");
        T retrofitService = (T) mRetrofitServiceCache.get(serviceClass.getCanonicalName());
        if (retrofitService == null) {
            retrofitService = mRetrofit.create(serviceClass);
            mRetrofitServiceCache.put(serviceClass.getCanonicalName(), retrofitService);
        }
        return retrofitService;
    }

    private <T> Method getRetrofitMethod(T service, Method method) throws NoSuchMethodException {
        return service.getClass().getMethod(method.getName(), method.getParameterTypes());
    }

    /**
     * 根据传入的 Class 创建对应的 RxCache service
     *
     * @param cacheClass Cache class
     * @param <T>        Cache class
     * @return Cache
     */
    @NonNull
    @Override
    public synchronized <T> T createCacheService(@NonNull Class<T> cacheClass) {
        Preconditions.checkNotNull(cacheClass, "cacheClass == null");
        if (mCacheServiceCache == null) {
            mCacheServiceCache = mCachefactory.build(CacheType.CACHE_SERVICE_CACHE);
        }
        Preconditions.checkNotNull(mCacheServiceCache,
                "Cannot return null from a Cache.Factory#build(int) method");
        T cacheService = (T) mCacheServiceCache.get(cacheClass.getCanonicalName());
        if (cacheService == null) {
            cacheService = mRxCache.using(cacheClass);
            mCacheServiceCache.put(cacheClass.getCanonicalName(), cacheService);
        }
        return cacheService;
    }

    /**
     * 清理所有RxCache缓存
     */
    @Override
    public void clearAllCache() {
        mRxCache.evictAll().subscribe();
    }

    @NonNull
    @Override
    public Context getContext() {
        return HrzApplication.mApplication;
    }


    private static Constructor<? extends IModel> findConstructorForClass(Class<?> cls) {

        Constructor<? extends IModel> bindingCtor;

        String clsName = cls.getName();

        try {
            //noinspection unchecked
            bindingCtor = (Constructor<? extends IModel>) cls.getConstructor(IRepositoryManager.class);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find constructor for " + clsName, e);
        }

        return bindingCtor;
    }
}
