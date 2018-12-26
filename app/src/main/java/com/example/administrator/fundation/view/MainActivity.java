package com.example.administrator.fundation.view;

import android.arch.lifecycle.LiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.fundation.R;
import com.example.administrator.fundation.adapter.MyAdapter;
import com.example.administrator.fundation.contract.ShareContract;
import com.example.administrator.fundation.model.entity.ShareEntity;
import com.example.administrator.fundation.model.imodel.ShareModel;
import com.example.administrator.fundation.presenter.SharePresenter;
import com.hrz.common.base.BaseActivity;
import com.hrz.common.config.Api;
import com.hrz.common.data.HrzRepositoryManager;
import com.hrz.common.eventline.Message;
import com.hrz.common.util.HrzUtil;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity<SharePresenter,ShareModel> implements ShareContract.View {


    private int page=1;


    @Override
    protected void initView() {
        final MyAdapter mAdapter = new MyAdapter(null,this);
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void intPresenter() {
        //在基类回收
        mModel=new ShareModel(HrzRepositoryManager.getInstance());
        mPresenter=new SharePresenter(mModel,this);
    }

    @Override
    protected int setViewId(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mPresenter.requestShare(getShareParamMap(page));
    }

    private void loadShareData(int page){
        mPresenter.requestShare(getShareParamMap(page));
    }

    private Map<String,Object> getShareParamMap(int page){
        Map<String,Object> map=new HashMap<>();
        map.put("page",page);
        map.put("pageSize",Api.COMMON_PAGE_SIZE);
        map.put("clientVersion","3.8.6");
        map.put("clientId","Android");
        map.put("nonceStr","xxx");
        map.put("timeStamp",System.currentTimeMillis());
        return map;
    }

    @Override
    public void getShareContentSuccess(ShareEntity entity) {
        HrzUtil.LogDHrz(entity.toString());

    }

    @Override
    public void getShareContentFail(String msg) {
        HrzUtil.LogDHrz(msg);
    }

    @Override
    public void getShareContentEmpty(String msg) {
        HrzUtil.LogDHrz(msg);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    @Override
    public void handleMessage(@NonNull Message message) {

    }

    /**
     * 有分页就配置
     */
    @Override
    public void initPagingConfig() {
        mPageConfig = new PagedList.Config.Builder()
                .setPageSize(Api.COMMON_PAGE_SIZE)                         //配置分页加载的数量
                .setEnablePlaceholders(false)                               //配置是否启动PlaceHolders
                .setInitialLoadSizeHint(Api.COMMON_PAGE_SIZE)              //初始化加载的数量
                .build();
    }

    /**
     * 三种DataSource
     * 1.PageKeyedDataSource：如果页面在加载时插入一个/下一个键，例如：从网络获取社交媒体的帖子，可能需要将nextPage加载到后续的加载中；
     * 2.ItemKeyedDataSource：在需要让使用的数据的item从N条增加到N+1条时使用
     * 3.PositionalDataSource：如果需要从数据存储的任意位置来获取数据页面。
     * 此类支持你从任何位置开始请求一组item的数据集。例如，该请求可能会返回从位置1200条开始的20个数据项；
     */
    class MyDataSource extends ItemKeyedDataSource<String,ShareEntity.DataBeanX.DataBean> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback) {

        }

        @Override
        public void loadAfter(@NonNull LoadParams params, @NonNull LoadCallback callback) {

        }

        @Override
        public void loadBefore(@NonNull LoadParams params, @NonNull LoadCallback callback) {

        }

        @NonNull
        @Override
        public String getKey(@NonNull ShareEntity.DataBeanX.DataBean item) {
            return null;
        }
    }

}
