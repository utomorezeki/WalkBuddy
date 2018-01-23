package com.mobileapps.walkbuddy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.mobileapps.walkbuddy.models.Destination;
import com.mobileapps.walkbuddy.models.Route;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a user's routes to a selected destination.
 */
public class RoutesFragment extends Fragment {
    private static final String ARG_DESTINATION_NAME = "destinationName";
    private static final String ARG_DESTINATION_POSITION = "position";

    // TODO: Rename and change types of parameters
    private String destinationName;
    private int destinationPosition;
    private List<Route> routes = new ArrayList<>();

    private ListView mListView;
    private RouteAdapter adapter;

    public RoutesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param destinationName
     * @return A new instance of fragment RoutesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoutesFragment newInstance(String destinationName, int position) {
        RoutesFragment fragment = new RoutesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DESTINATION_NAME, destinationName);
        args.putInt(ARG_DESTINATION_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            destinationName = getArguments().getString(ARG_DESTINATION_NAME);
            destinationPosition = getArguments().getInt(ARG_DESTINATION_POSITION);
            Destination destination = ((MainActivity)getActivity()).destinations.get(destinationPosition);
            routes = destination.getRoutes();
        }
        adapter = new RouteAdapter(getActivity(), routes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout mFrameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_routes, container, false);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle(destinationName);

        mListView = mFrameLayout.findViewById(R.id.route_list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Route selectedRoute = routes.get(position);

                ArrayList<Double> verticesLat = (ArrayList<Double>) selectedRoute.getVerticesLat();
                ArrayList<Double> verticesLng = (ArrayList<Double>) selectedRoute.getVerticesLng();
                ArrayList<Double> poiLat = (ArrayList<Double>) selectedRoute.getPoiLat();
                ArrayList<Double> poiLng = (ArrayList<Double>) selectedRoute.getPoiLng();

                long time = selectedRoute.getTimeInMillis();
                Fragment fragment;
                if (time > 420000) {
                    fragment = DestinationMapFrag.newInstance(verticesLat, verticesLng, poiLat, poiLng);
                } else {
                    fragment = DestinationLiteMapFrag.newInstance(verticesLat, verticesLng, poiLat, poiLng);
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainContent, fragment, "");
                fragmentTransaction.addToBackStack(null).commit();
            }
        });

        return mFrameLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
