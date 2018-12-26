package com.example.administrator.fundation.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.fundation.model.entity.ShareEntity;
import com.example.administrator.fundation.viewholder.MyViewHolder;

public class MyAdapter extends PagedListAdapter<ShareEntity.DataBeanX.DataBean, MyViewHolder> {

    private Context mContext;

    public MyAdapter(@NonNull DiffUtil.ItemCallback<ShareEntity.DataBeanX.DataBean> diffCallback,Context mContext) {
        super(diffCallback);
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_2,null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        ShareEntity.DataBeanX.DataBean data = getItem(position);
        myViewHolder.text1.setText(String.valueOf(position));
        myViewHolder.text2.setText(String.valueOf(data.getContent()));
    }
}
