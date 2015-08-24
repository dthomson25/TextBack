package com.dthomson.textback;

/**
 * Created by dthomson on 8/6/2015.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TextViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "viewholder";

    protected TextView titleText;
    protected TextView contentText;
    protected CardView card;

    public TextViewHolder(View itemView) {
        super(itemView);
        titleText = (TextView) itemView.findViewById(R.id.address);
        contentText = (TextView) itemView.findViewById(R.id.last_text);
        card = (CardView) itemView;
    }

}
