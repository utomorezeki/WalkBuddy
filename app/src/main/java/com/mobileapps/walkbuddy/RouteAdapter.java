package com.mobileapps.walkbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileapps.walkbuddy.models.Destination;
import com.mobileapps.walkbuddy.models.Route;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.List;

/**
 * Created by kurti on 11/3/2017.
 */

public class RouteAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Route> mDataSource;

    public RouteAdapter(Context context, List<Route> items) {
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
        View rowView = mInflater.inflate(R.layout.route_list_item, parent, false);

        TextView titleTextView = rowView.findViewById(R.id.route_title);
        TextView routeTimeTextView = rowView.findViewById(R.id.route_time);

        Route route = (Route) getItem(position);
        titleTextView.setText("From " + route.getRouteName());
        String routeTime = Long.toString(route.getTimeInMillis()) + " min";
        routeTimeTextView.setText(routeTime);

        return rowView;
    }
}
