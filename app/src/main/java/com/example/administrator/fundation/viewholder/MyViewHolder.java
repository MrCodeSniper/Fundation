package com.example.administrator.fundation.viewholder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder  {

    public TextView text1;
    public TextView text2;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        text1 = itemView.findViewById(android.R.id.text1);
        text1.setTextColor(Color.RED);

        text2 = itemView.findViewById(android.R.id.text2);
        text2.setTextColor(Color.BLUE);
    }
}
