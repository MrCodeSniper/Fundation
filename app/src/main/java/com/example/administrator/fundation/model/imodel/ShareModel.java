package com.example.administrator.fundation.model.imodel;

import com.example.administrator.fundation.contract.ShareContract;
import com.example.administrator.fundation.model.entity.ShareEntity;
import com.example.administrator.fundation.model.service.ShareService;
import com.hrz.common.base.BaseModel;
import com.hrz.common.ibase.IRepositoryManager;
import java.util.Map;
import io.reactivex.Observable;

public class ShareModel extends BaseModel implements ShareContract.Model {

    public ShareModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<ShareEntity> getSearchHotWords(Map<String, Object> map) {
        return mRepositoryManager.createRetrofitService(ShareService.class).getShareDataByMap(map);
    }
}
