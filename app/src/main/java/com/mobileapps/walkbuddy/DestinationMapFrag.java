package com.mobileapps.walkbuddy;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobileapps.walkbuddy.models.Route;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;
import java.util.List;

public class DestinationMapFrag extends Fragment implements OnMapReadyCallback{
    private static final String ARG_VERTICES_LAT = "verticesLat";
    private static final String ARG_VERTICES_LNG = "verticesLng";

    MapView mMapView;
    GoogleMap mGoogleMap;
    View mView;
    private List<Double> verticesLat;
    private List<Double> verticesLng;

    public DestinationMapFrag() {
        // Required empty public constructor
    }

    public static DestinationMapFrag newInstance(ArrayList<Double> verticesLat, ArrayList<Double> verticesLng) {
        DestinationMapFrag fragment = new DestinationMapFrag();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VERTICES_LAT, verticesLat);
        args.putSerializable(ARG_VERTICES_LNG, verticesLng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.verticesLat = (List<Double>) getArguments().getSerializable(ARG_VERTICES_LAT);
            this.verticesLng = (List<Double>) getArguments().getSerializable(ARG_VERTICES_LNG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_destination_map, container, false);
        return mView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
        ArrayList<LatLng> userData = getUserRoute();
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .addAll(userData));
    }

    public ArrayList<LatLng> getUserRoute() {
        ArrayList<LatLng> result = new ArrayList<>();
        for(int i = 0; i < this.verticesLat.size(); i++){
            result.add(new LatLng(this.verticesLat.get(i),this.verticesLng.get(i)));
        }
        return result;
    }
}
