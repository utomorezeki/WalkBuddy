package com.mobileapps.walkbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileapps.walkbuddy.models.Destination;
import com.mobileapps.walkbuddy.models.Route;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.List;

/**
 * Created by kurti on 11/3/2017.
 */

public class QuickestRoutesAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Route> mDataSource;

    public QuickestRoutesAdapter(Context context, List<Route> items) {
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
        View rowView = mInflater.inflate(R.layout.quickest_routes_list_item, parent, false);

        TextView titleTextView = rowView.findViewById(R.id.quickest_destination);
        TextView routeTimeTextView = rowView.findViewById(R.id.quickest_route_time);

        Route route = (Route) getItem(position);
        String titleText = route.getStartLocationName() + " to " + route.getDestinationName();
        titleTextView.setText(titleText);
        String routeTime = getTimeText(route.getTimeInMillis());
        routeTimeTextView.setText(routeTime);

        return rowView;
    }

    private String getTimeText(long timeInMillis) {
        int secs = (int) timeInMillis/1000;
        int mins = secs/60;
        secs %= 60;
        String minText = mins == 1 ? " min " : " mins ";
        String secText = secs == 1 ? " sec " : " secs ";

        return Integer.toString(mins) + minText + secs + secText;
    }
}
