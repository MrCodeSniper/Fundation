package com.example.administrator.fundation.contract;

import com.example.administrator.fundation.model.entity.ShareEntity;
import com.hrz.common.ibase.IModel;
import com.hrz.common.ibase.IView;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;

public interface ShareContract {

    interface View extends IView{

        void getShareContentSuccess(ShareEntity entity);
        void getShareContentFail(String msg);
        void getShareContentEmpty(String msg);
        void noMoreShareContent();

    }


    interface Model extends IModel{
        Observable<ShareEntity> getSearchHotWords(Map<String,Object> map);
    }


}
