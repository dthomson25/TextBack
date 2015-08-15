package com.dthomson.textback;

/**
 * Created by dthomson on 8/6/2015.
 */

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dthomson.textback.interfaces.ItemTouchHelperViewHolder;

public class TextViewHolder extends RecyclerView.ViewHolder
    implements ItemTouchHelperViewHolder {
    private static final String TAG = "viewholder";

    private TextMessage text;
    protected TextView titleText;
    protected TextView contentText;
    protected CardView card;

    public TextViewHolder(View itemView) {
        super(itemView);
        // Define click listener for the ViewHolder's View.
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                mListener.onTextSelected(null);
//                Toast.makeText(v.getContext(), "hi", Toast.LENGTH_SHORT).show();
//            }
//        });
        titleText = (TextView) itemView.findViewById(R.id.address);
        contentText = (TextView) itemView.findViewById(R.id.last_text);
        card = (CardView) itemView;
    }

    @Override
    public void onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onItemClear() {
        itemView.setBackgroundColor(0);
    }

}
