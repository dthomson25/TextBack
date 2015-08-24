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

public class MyRecyclerAdapter extends RecyclerView.Adapter<TextViewHolder> {

    CursorAdapter mCursorAdapter;

    Context mContext;

    public MyRecyclerAdapter(Context context, final Cursor c) {

        mContext = context;

        mCursorAdapter = new CursorAdapter(mContext, c, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                View itemView = LayoutInflater.
                        from(context).
                        inflate(R.layout.card_view, viewGroup, false);
            return itemView;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                    String person = cursor.getString(cursor.getColumnIndex("person"));
                    String lastText = cursor.getString(cursor.getColumnIndex("last_text"));
                    if (lastText.length() > 40) {
                        lastText = lastText.substring(0,40) + "...";
                    }
                    TextView addr = (TextView) view.findViewById(R.id.address);
                    if (person != null) {
                        addr.setText(person);
                    } else {
                        String address = cursor.getString(cursor.getColumnIndex("address"));
                        addr.setText(address);
                    }
                    TextView lastTextTB = (TextView) view.findViewById(R.id.last_text);
                    lastTextTB.setText(lastText);
            }
        };
    }


    public void addTextMessage(Cursor cursor, int position) {
        mCursorAdapter.changeCursor(cursor);
        notifyItemInserted(position);
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), viewGroup);
        return new TextViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TextViewHolder holder, int i) {
        Cursor c = mCursorAdapter.getCursor();
        //This code moves the cursor to make sure that it is getting every element in the cursor
        int position = c.getPosition();
        c.move(i - position);
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

    public void clearTexts() {
        int size = mCursorAdapter.getCount();
        notifyItemRangeRemoved(0, size);
    }

    public void deleteText(Cursor c, int position) {
        mCursorAdapter.changeCursor(c);
        notifyItemRemoved(position);
    }

}