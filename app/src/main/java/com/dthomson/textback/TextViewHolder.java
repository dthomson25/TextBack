package com.dthomson.textback;

/**
 * Created by dthomson on 8/6/2015.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class TextViewHolder extends RecyclerView.ViewHolder {

    protected TextView titleText;
    protected TextView contentText;
    protected CardView card;

    public TextViewHolder(View itemView) {
        super(itemView);
        titleText = (TextView) itemView.findViewById(R.id.person);
        contentText = (TextView) itemView.findViewById(R.id.conversation);
        card = (CardView) itemView;
    }

}

//public static class TextViewHolder extends RecyclerView.ViewHolder{
//
//    View v1;
//    protected TextView titleText;
//protected TextView contentText;
//protected CardView card;
//    public ViewHolder(View itemView) {
//        super(itemView);
//        v1 = itemView.findViewById(R.id.v1);
//        titleText = (TextView) itemView.findViewById(R.id.person);
//        contentText = (TextView) itemView.findViewById(R.id.conversation);
//        card = (CardView) itemView;
//    }
//}