package com.mobileapps.walkbuddy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Fragment for saving a route that has just been recorded.
 */
public class SaveRouteFragment extends Fragment {
    private static final String ARG_USER_LAT = "user_lat";
    private static final String ARG_USER_LNG = "user_lng";
    private static final String ARG_DEST_LAT = "dest_lat";
    private static final String ARG_DEST_LNG = "dest_lng";
    private static final String ARG_POI_LAT = "poi_lat";
    private static final String ARG_POI_LNG = "poi_lng";
    private static final String ARG_TIME = "timeInMillis";

    private List<Double> userLat;
    private List<Double> userLng;
    private double destLat;
    private double destLng;
    private List<Double> poiLat;
    private List<Double> poiLng;
    private long timeInMillis;

    private EditText editDestinationNameText, editStartLocationText;

    private OnSaveRouteFragmentInteractionListener mListener;

    public SaveRouteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SaveRouteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaveRouteFragment newInstance(ArrayList<Double> userLat, ArrayList<Double> userLng, double destLat, double destLng, ArrayList<Double> poiLat, ArrayList<Double> poiLng, long timeInMillis) {
        SaveRouteFragment fragment = new SaveRouteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_LAT, userLat);
        args.putSerializable(ARG_USER_LNG, userLng);
        args.putDouble(ARG_DEST_LAT, destLat);
        args.putDouble(ARG_DEST_LNG, destLng);
        args.putSerializable(ARG_POI_LAT, poiLat);
        args.putSerializable(ARG_POI_LNG, poiLng);
        args.putLong(ARG_TIME, timeInMillis);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userLat = (List<Double>) getArguments().getSerializable(ARG_USER_LAT);
            userLng = (List<Double>) getArguments().getSerializable(ARG_USER_LNG);
            destLat = getArguments().getDouble(ARG_DEST_LAT);
            destLng = getArguments().getDouble(ARG_DEST_LNG);
            poiLat = (List<Double>) getArguments().getSerializable(ARG_POI_LAT);
            poiLng = (List<Double>) getArguments().getSerializable(ARG_POI_LNG);
            timeInMillis = getArguments().getLong(ARG_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_save_route, container, false);

        TextView finalTimeText = frameLayout.findViewById(R.id.final_time);

        // Set time
        finalTimeText.setText(getFinalTime());

        Button btnSaveRoute = frameLayout.findViewById(R.id.btn_save_route);
        Button btnCancelRoute = frameLayout.findViewById(R.id.btn_cancel_route);

        editStartLocationText = frameLayout.findViewById(R.id.start_location);
        editDestinationNameText = frameLayout.findViewById(R.id.destination_name);

        btnCancelRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancelSaveRoute();
            }
        });

        btnSaveRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startLocationInput = editStartLocationText.getText().toString().trim();
                String destinationInput = editDestinationNameText.getText().toString().trim();

                if(TextUtils.isEmpty(startLocationInput)) {
                    Toast.makeText(getActivity(), "Please enter a starting location", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(destinationInput)) {
                    Toast.makeText(getActivity(), "Please enter a destination name", Toast.LENGTH_SHORT).show();
                    return;
                }

                mListener.saveRoute(destinationInput, startLocationInput, userLat, userLng, poiLat, poiLng, timeInMillis);
            }
        });

        return frameLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSaveRouteFragmentInteractionListener) {
            mListener = (OnSaveRouteFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSaveRouteFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private String getFinalTime() {
        int secs = (int) timeInMillis/1000;
        int mins = secs/60;
        secs %= 60;
        return String.format(Locale.getDefault(), "%02d", mins) + ":" + String.format(Locale.getDefault(), "%02d",secs);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnSaveRouteFragmentInteractionListener {
        /**
         * Cancels route saving and goes back to main activity.
         */
        void cancelSaveRoute();

        /**
         * Saves route for user.
         *
         * @param destinationName
         * @param startLocationName
         * @param userLat
         * @param userLng
         * @param poiLat
         * @param poiLng
         * @param timeInMillis
         */
        void saveRoute(String destinationName, String startLocationName, List<Double> userLat, List<Double> userLng, List<Double> poiLat, List<Double> poiLng, long timeInMillis);
    }
}
