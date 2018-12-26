package com.hrz.common.ibase;



public interface IPaging {

    public void initPagingConfig();

    /**
     *  下拉刷新和加载更多 的view操作 T为返回实体类型
     */
    public interface IpagenateView<T> extends IView {

        void loadMoreSuccess(T t);

        void loadMoreEmpty();

        void refreshEmpty();

        void loadMoreError();

        void refreshError();

    }



    public interface IpagingModel<T> extends IModel {

        T loadPagingData();

    }


    public interface IPagingPresenter<T> extends IPresenter {



    }

}
