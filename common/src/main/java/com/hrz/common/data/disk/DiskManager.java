package com.hrz.common.data.disk;

import android.content.Context;

import com.hrz.common.data.disk.core.DiskLruCache;



public class DiskManager implements IDisk {

    private DiskLruCache mDiskLruCache;

    private static DiskManager mDiskManager;


    public static DiskManager getInstance(Context mContext){
        if(mDiskManager==null){
            mDiskManager=new DiskManager();
        }
        return mDiskManager;
    }

    private DiskManager(){
        //初始化DiskLruCache
    }





}
