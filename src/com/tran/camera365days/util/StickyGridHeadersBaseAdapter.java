package com.tran.camera365days.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public interface StickyGridHeadersBaseAdapter extends ListAdapter {
    /**
     * Get the number of items with a given header.
     *
     * @param section
     *            The header in the adapter's data set.
     * @return The number of items for the specified header.
     */
    public int getCountForHeader(int header);

    /**
     * Get the number of headers in the adapter's data set.
     *
     * @return Number of headers.
     */
    public int getNumHeaders();

    View getHeaderView(int position, View convertView, ViewGroup parent);
}
