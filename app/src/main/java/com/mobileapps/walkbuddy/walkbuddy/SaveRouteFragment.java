package com.mobileapps.walkbuddy.walkbuddy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.mobileapps.walkbuddy.MainActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SaveRouteFragment.OnSaveRouteFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SaveRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveRouteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DESTINATION_NAME = "destination_name";
    private static final String ARG_USER_LAT = "user_lat";
    private static final String ARG_USER_LNG = "user_lng";
    private static final String ARG_DEST_LAT = "dest_lat";
    private static final String ARG_DEST_LNG = "dest_lng";
    private static final String ARG_TIME = "timeInMillis";

    // TODO: Rename and change types of parameters
    private String destinationName;
    private List<Double> userLat;
    private List<Double> userLng;
    private double destLat;
    private double destLng;
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
    public static SaveRouteFragment newInstance(CharSequence destinationName, ArrayList<Double> userLat, ArrayList<Double> userLng, double destLat, double destLng, long timeInMillis) {
        SaveRouteFragment fragment = new SaveRouteFragment();
        Bundle args = new Bundle();
        args.putCharSequence(ARG_DESTINATION_NAME, destinationName);
        args.putSerializable(ARG_USER_LAT, userLat);
        args.putSerializable(ARG_USER_LNG, userLng);
        args.putDouble(ARG_DEST_LAT, destLat);
        args.putDouble(ARG_DEST_LNG, destLng);
        args.putLong(ARG_TIME, timeInMillis);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            destinationName = (String) getArguments().getCharSequence(ARG_DESTINATION_NAME);
            userLat = (List<Double>) getArguments().getSerializable(ARG_USER_LAT);
            userLng = (List<Double>) getArguments().getSerializable(ARG_USER_LNG);
            destLat = getArguments().getDouble(ARG_DEST_LAT);
            destLng = getArguments().getDouble(ARG_DEST_LNG);
            timeInMillis = getArguments().getLong(ARG_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_save_route, container, false);

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
                    double startLat = userLat.get(0);
                    double startLng = userLng.get(0);

                    startLocationInput = Double.toString(startLat) + ", " + Double.toString(startLng);
                }

                if(TextUtils.isEmpty(destinationInput)) {
                    if(destinationName.equals("error")) {
                        destinationInput = Double.toString(destLat) + ", " + Double.toString(destLng);
                    } else {
                        destinationInput = destinationName;
                    }
                }

                mListener.saveRoute(destinationInput, startLocationInput, userLat, userLng, timeInMillis);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSaveRouteFragmentInteractionListener {
        // TODO: Update argument type and name
        void cancelSaveRoute();
        void saveRoute(String destinationName, String startLocationName, List<Double> userLat, List<Double> userLng, long timeInMillis);
    }
}
