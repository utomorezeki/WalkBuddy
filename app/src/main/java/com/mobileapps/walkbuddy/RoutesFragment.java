package com.mobileapps.walkbuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.mobileapps.walkbuddy.models.Destination;
import com.mobileapps.walkbuddy.models.Route;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoutesFragment.OnRoutesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoutesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DESTINATION_NAME = "destinationName";
    private static final String ARG_DESTINATION_POSITION = "position";

    // TODO: Rename and change types of parameters
    private String destinationName;
    private int destinationPosition;
    private List<Route> routes = new ArrayList<>();

    private ListView mListView;
    private RouteAdapter adapter;

    private OnRoutesFragmentInteractionListener mListener;

    public RoutesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param destinationName Parameter 1.
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


        mListView.setLongClickable(true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Route selectedRoute = routes.get(position);

                ArrayList<Double> verticesLat = (ArrayList<Double>) selectedRoute.getVerticesLat();
                ArrayList<Double> verticesLng = (ArrayList<Double>) selectedRoute.getVerticesLng();

                Fragment fragment = DestinationMapFrag.newInstance(verticesLat, verticesLng);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainContent, fragment, "");
                fragmentTransaction.addToBackStack(null).commit();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(routes.size() > 1) {
                    final int selectedPosition = position;
                    final Route selectedRoute = routes.get(selectedPosition);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                    alertDialogBuilder.setTitle("Delete Destination");

                    alertDialogBuilder
                            .setMessage("Are you sure you want to delete the route from " + selectedRoute.getStartLocationName() + "?")
                            .setCancelable(false)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mListener.deleteRoute(destinationName, selectedPosition);
                                    adapter.notifyDataSetChanged();
                                    if (routes.size() == 1) {
                                        mListView.setLongClickable(false);
                                    }
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                return true;
            }
        });

        return mFrameLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRoutesFragmentInteractionListener) {
            mListener = (OnRoutesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnRoutesFragmentInteractionListener {
        // TODO: Update argument type and name
        void deleteRoute(String destinationName, int routeIndex);
    }
}
