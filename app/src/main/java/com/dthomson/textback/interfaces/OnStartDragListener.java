package com.dthomson.textback.interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by dthomson on 8/15/2015.
 */
public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);


    void deleteText(int position);
}
