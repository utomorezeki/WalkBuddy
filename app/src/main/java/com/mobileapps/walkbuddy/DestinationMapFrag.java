package com.mobileapps.walkbuddy;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobileapps.walkbuddy.models.Route;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;
import java.util.List;

public class DestinationMapFrag extends Fragment implements OnMapReadyCallback{
    private static final String ARG_VERTICES_LAT = "verticesLat";
    private static final String ARG_VERTICES_LNG = "verticesLng";
    private final static int MY_PERMISSION_FINE_LOCATION = 101;

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

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
        mGoogleMap.addMarker(new MarkerOptions().position(userData.get(userData.size() - 1)).title("Destination"));
        CameraPosition dest = CameraPosition.builder().target(userData.get(0)).zoom(16).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(dest));
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
