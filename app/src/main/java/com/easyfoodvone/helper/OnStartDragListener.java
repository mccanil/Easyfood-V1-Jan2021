package com.easyfoodvone.helper;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by javierg on 12/10/2016.
 */

public interface OnStartDragListener {
    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder,int position);
}
