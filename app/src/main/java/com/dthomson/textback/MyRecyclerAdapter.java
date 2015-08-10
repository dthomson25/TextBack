package com.dthomson.textback;

/**
 * Created by dthomson on 8/6/2015.
 */
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<TextViewHolder> {

    CursorAdapter mCursorAdapter;

    Context mContext;

    public MyRecyclerAdapter(Context context, Cursor c) {

        mContext = context;

        mCursorAdapter = new CursorAdapter(mContext, c, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                View itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.card_view, viewGroup, false);
            return itemView;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
//                TextMessage text = texts.get(i);
                if (cursor != null) {
                    TextMessage text = new TextMessage("REd","red");
                    TextView  addr = (TextView) view.findViewById(R.id.person);
                    addr.setText(text.getAddress());
                    TextView lastText = (TextView) view.findViewById(R.id.person);
                    lastText.setText(text.getAddress());
                }

            }
        };
    }



//    private List<TextMessage> texts;
//
//    public MyRecyclerAdapter(List<TextMessage> texts) {
////        this.texts = new ArrayList<>();
////        this.texts.addAll(texts);
//    }

//    public List<TextMessage> getTexts() {
//        return texts;
//    }

    public void addTextMessage(Cursor cursor, int position) {
        mCursorAdapter.changeCursor(cursor);
//        texts.add(position, textMessage);
        notifyItemInserted(position);
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), viewGroup);
        return new TextViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(TextViewHolder holder, int i) {

        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    public void defaultCards(Cursor cursor) {
        mCursorAdapter.changeCursor(cursor);
        notifyItemRangeInserted(0, mCursorAdapter.getCount());
    }

    public void changeCursor(Cursor cursor) {
        mCursorAdapter.changeCursor(cursor);
    }

    public void clearTexts(Cursor cursor) {
        int size = mCursorAdapter.getCount();
        mCursorAdapter.changeCursor(cursor);
        notifyItemRangeRemoved(0,size);
    }
}