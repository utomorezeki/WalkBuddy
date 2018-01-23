package com.mobileapps.walkbuddy;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;
import java.util.List;

public class DestinationLiteMapFrag extends Fragment implements OnMapReadyCallback {
    private static final String ARG_VERTICES_LAT = "verticesLat";
    private static final String ARG_VERTICES_LNG = "verticesLng";
    private static final String ARG_POI_LAT = "poi_lat";
    private static final String ARG_POI_LNG = "poi_lng";
    private final static int MY_PERMISSION_FINE_LOCATION = 101;

    MapView mMapView;
    GoogleMap mGoogleMap;
    View mView;
    private List<Double> verticesLat;
    private List<Double> verticesLng;
    private List<Double> poiLat;
    private List<Double> poiLng;

    public DestinationLiteMapFrag() {
        // Required empty public constructor
    }

    public static DestinationLiteMapFrag newInstance(ArrayList<Double> verticesLat, ArrayList<Double> verticesLng, ArrayList<Double> poiLat, ArrayList<Double> poiLng) {
        DestinationLiteMapFrag fragment = new DestinationLiteMapFrag();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VERTICES_LAT, verticesLat);
        args.putSerializable(ARG_VERTICES_LNG, verticesLng);
        args.putSerializable(ARG_POI_LAT, poiLat);
        args.putSerializable(ARG_POI_LNG, poiLng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.verticesLat = (List<Double>) getArguments().getSerializable(ARG_VERTICES_LAT);
            this.verticesLng = (List<Double>) getArguments().getSerializable(ARG_VERTICES_LNG);
            this.poiLat = (List<Double>) getArguments().getSerializable(ARG_POI_LAT);
            this.poiLng = (List<Double>) getArguments().getSerializable(ARG_POI_LNG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_destination_lite_map, container, false);
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

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }

        if(poiLat != null) {
            for (int i = 0; i < poiLat.size(); i++) {
                LatLng poi = new LatLng(poiLat.get(i), poiLng.get(i));
                int poiNum = i + 1;
                mGoogleMap.addMarker(new MarkerOptions().position(poi).title("POI " + poiNum));
            }
        }

        mGoogleMap.addMarker(new MarkerOptions().position(userData.get(userData.size() - 1)).title("Destination"));
        LatLngBounds.Builder asd = new LatLngBounds.Builder();
        asd.include(userData.get(userData.size() - 1));
        asd.include(userData.get(0));
        LatLngBounds scale = asd.build();

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(scale,50));
    }

    public ArrayList<LatLng> getUserRoute() {
        ArrayList<LatLng> result = new ArrayList<>();
        for(int i = 0; i < this.verticesLat.size(); i++){
            result.add(new LatLng(this.verticesLat.get(i),this.verticesLng.get(i)));
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
}
