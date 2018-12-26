package com.hrz.common.base;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.hrz.common.config.Api;
import com.hrz.common.data.cache.Cache;
import com.hrz.common.data.cache.CacheType;
import com.hrz.common.data.cache.IntelligentCache;
import com.hrz.common.data.cache.LruCache;
import com.hrz.common.data.image.BaseImageLoaderStrategy;
import com.hrz.common.data.image.glide.GlideImageLoaderStrategy;
import com.hrz.common.init.Flow;
import com.hrz.common.init.ProcessUtils;
import com.hrz.common.init.log.ILog;
import com.hrz.common.init.Init;
import com.hrz.common.init.Task;
import com.hrz.common.util.HrzUtil;

import java.io.File;

import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HrzApplication extends Application implements ILog {

    public static HrzApplication mApplication;
    public static Retrofit mRetrofit;
    public static OkHttpClient mOkHttpClient;
    public static Gson gson;
    public static Cache.Factory mHrzCacheFactory;
    public static RxCache mRxCache;
    public static File  mRxcacheDirectory;
    public static BaseImageLoaderStrategy mHrzImageLoaderStrategy;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication=this;
        Init.init(this);
        initSerial();
        initSync();
    }


    /**
     * 并行初始化
     */
    private void initSync(){
        long startTime=System.currentTimeMillis();   //获取开始时间
        //创建app初始化的事件流
        Flow flow =new Flow("hrz_flow");
        //理清初始化关系
        //retrofit初始化需要okhttp和gson初始化完成
        //Rxcache初始化需要gson初始化完成
        //先让okhttp gson cache工厂 imageloader 先异步加载 阻塞下一波task执行
        //上一波task执行完毕之后 执行retrofit和Rxcache的初始化


        //创建非阻塞的task
        // 默认Task，延迟0，且阻塞下一波task的执行，参数字符串可以用来追踪任务执行状态
        Task task_prepare = new Task("task-okhttp-gson-cache-imageloader") {
            @Override
            protected void start() {
                initGson();
                initOkHttp();
                initCacheFactory();
                initImageLoader();
            }

            // 仅在返回true的时候才会在对应进程执行
            @Override
            public boolean runOnProcess(String processName) {
                return processName.equals(ProcessUtils.myProcessName());
            }
        };

        // 创建一个task，非阻塞，且延时300毫秒执行
        Task task_last = new Task("task-retrofit-rxcache", false, 0) {
            @Override
            protected void start() {
                initRetrofit();
                initRxCache();
                HrzUtil.LogDHrz("并行执行时间:"+(System.currentTimeMillis()-startTime)+"ms");
            }
        };

        // 往flow添加刚才创建的task, 第一个参数是wave序号，会从小到大执行每个wave的task
        flow.addTask(1, task_prepare).addTask(2, task_last);
        // 启动flow，开始初始化
        Init.start(flow);
    }


    /**
     * 串行初始化
     */
    private void initSerial(){
        long startTime=System.currentTimeMillis();   //获取开始时间
        initGson();
        initOkHttp();
        initRetrofit();
        initCacheFactory();
        initRxCache();
        initImageLoader();
        HrzUtil.LogDHrz("串行执行时间:"+(System.currentTimeMillis()-startTime)+"ms");
    }

    private void initImageLoader() {
        if(mHrzImageLoaderStrategy==null){
            mHrzImageLoaderStrategy=new GlideImageLoaderStrategy();
        }
    }

    private void initRxCache() {
        mRxcacheDirectory=HrzUtil.getCacheFile(this);
        if(mRxCache==null){
            mRxCache=new RxCache.Builder().persistence(mRxcacheDirectory, new GsonSpeaker(gson));
        }
    }

    private void initCacheFactory() {
        if(mHrzCacheFactory==null) {
            mHrzCacheFactory = new Cache.Factory() {
                @NonNull
                @Override
                public Cache build(CacheType type) {
                    //若想自定义 LruCache 的 size, 或者不想使用 LruCache, 想使用自己自定义的策略
                    //使用 GlobalConfigModule.Builder#cacheFactory() 即可扩展
                    switch (type.getCacheTypeId()) {
                        //Activity、Fragment 以及 Extras 使用 IntelligentCache (具有 LruCache 和 可永久存储数据的 Map)
                        case CacheType.EXTRAS_TYPE_ID:
                        case CacheType.ACTIVITY_CACHE_TYPE_ID:
                        case CacheType.FRAGMENT_CACHE_TYPE_ID:
                            return new IntelligentCache(type.calculateCacheSize(mApplication));
                        //其余使用 LruCache (当达到最大容量时可根据 LRU 算法抛弃不合规数据)
                        default:
                            return new LruCache(type.calculateCacheSize(mApplication));
                    }
                }
            };
        }
    }

    private void initGson(){
        if(gson==null){
            gson=new Gson();
        }
    }

    private void initOkHttp() {
        if(mOkHttpClient==null){
            mOkHttpClient=new OkHttpClient();
        }
    }

    private void initRetrofit() {
        if(mRetrofit==null){
            Retrofit.Builder builder = new Retrofit.Builder();//创建Retrfit构建器
            builder.baseUrl(Api.APP_DOMAIN)//域名
                    .client(mOkHttpClient)//设置 OkHttp
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用 RxJava
                    .addConverterFactory(GsonConverterFactory.create(gson));//使用 Gson
            mRetrofit=builder.build();
        }
    }


    @Override
    public void info(String tag, String msg) {
        HrzUtil.LogDHrz(msg);
    }

    @Override
    public void warn(String tag, String msg) {
        HrzUtil.LogDHrz(msg);
    }
}
