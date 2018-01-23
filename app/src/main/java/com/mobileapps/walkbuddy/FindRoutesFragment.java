package com.mobileapps.walkbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.mobileapps.walkbuddy.models.Route;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;

/**
 * Fragment for launching the main functionality of the app. Allows user to pick a place they wish
 * to walk to and begin recording a route to that destination.
 */
public class FindRoutesFragment extends Fragment {
    private static final String ARG_QUICKEST_ROUTES = "quickestRoutes";

    Button findPlaceBut;
    LatLng placeLoc;
    CharSequence placeName;

    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private final static int PLACE_PICKER_REQUEST = 1;

    private ArrayList<Route> quickestRoutes;
    private ListView mListView;
    private QuickestRoutesAdapter adapter;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public FindRoutesFragment() {
        // Required empty public constructor
    }

    public static FindRoutesFragment newInstance(ArrayList<Route> quickestRoutes) {
        FindRoutesFragment fragment = new FindRoutesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUICKEST_ROUTES, quickestRoutes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            quickestRoutes = (ArrayList<Route>) getArguments().getSerializable(ARG_QUICKEST_ROUTES);
        }
        adapter = new QuickestRoutesAdapter(getActivity(), quickestRoutes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_routes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findPlaceBut = view.findViewById(R.id.pickPlace);
        findPlaceBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = builder.build(getActivity());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        TextView noRoutes = view.findViewById(R.id.no_routes_message);

        if(quickestRoutes != null) {
            if (quickestRoutes.size() > 0) {
                noRoutes.setVisibility(View.GONE);
                mListView = view.findViewById(R.id.quickest_routes_list);
                mListView.setAdapter(adapter);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Route selectedRoute = quickestRoutes.get(position);

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
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == getActivity().RESULT_OK){
                Place place = PlacePicker.getPlace(getActivity(), data);
                placeName = place.getName();
                placeLoc = place.getLatLng();

                Fragment fragment = null;
                Class mapFrag = MapFragment.class;
                try {
                    fragment = (Fragment) mapFrag.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("name", placeName);
                    bundle.putDouble("lat",placeLoc.latitude);
                    bundle.putDouble("long",placeLoc.longitude);
                    fragment.setArguments(bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainContent, fragment, "");
                fragmentTransaction.commit();
            }
        }


    }
}
