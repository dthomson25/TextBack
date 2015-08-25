package com.dthomson.textback;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TextViewHolder extends RecyclerView.ViewHolder {

    protected TextView titleText;
    protected TextView contentText;
    protected TextView rowIDText;
    protected CardView card;

    public TextViewHolder(View itemView) {
        super(itemView);
        titleText = (TextView) itemView.findViewById(R.id.address);
        contentText = (TextView) itemView.findViewById(R.id.last_text);
        rowIDText = (TextView) itemView.findViewById(R.id.row_id);
        card = (CardView) itemView;
    }

}
