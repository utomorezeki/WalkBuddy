package com.mobileapps.walkbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileapps.walkbuddy.models.Destination;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.List;

/**
 * Adapter for managing the Destination ListView.
 */

public class DestinationAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Destination> mDataSource;

    public DestinationAdapter(Context context, List<Destination> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.destination_list_item, parent, false);

        TextView titleTextView = rowView.findViewById(R.id.destination_title);
        TextView routeCountTextView = rowView.findViewById(R.id.destination_route_count);

        Destination destination = (Destination) getItem(position);
        titleTextView.setText(destination.getDestinationName());
        int routeCount = destination.getRoutes().size();
        String routeAppend = routeCount == 1 ? " route" : " routes";
        String routeCountText = routeCount + routeAppend;
        routeCountTextView.setText(routeCountText);

        return rowView;
    }
}
