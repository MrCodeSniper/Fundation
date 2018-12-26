package com.example.administrator.fundation.model.service;

import com.example.administrator.fundation.model.entity.ShareEntity;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ShareService {


    @GET("/xdz/admin/share/list")
    Observable<ShareEntity> getShareData(@Query("page") int page,
                                         @Query("pageSize")int  pageSize,
                                         @Query("clientVersion") String version,
                                         @Query("clientId") String clientId,
                                         @Query("nonceStr") String nonceStr,
                                         @Query("timeStamp") String timeStamp);


    @GET("/xdz/admin/share/list")
    Observable<ShareEntity> getShareDataByMap(@QueryMap Map<String ,Object> map);



}
