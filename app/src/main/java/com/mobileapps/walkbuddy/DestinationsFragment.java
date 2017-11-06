package com.mobileapps.walkbuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.gms.vision.Frame;
import com.mobileapps.walkbuddy.models.Destination;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;
import java.util.List;

public class DestinationsFragment extends Fragment {

    private OnDestinationsFragmentInteractionListener mListener;
    private ListView mListView;
    private List<Destination> destinations = new ArrayList<>();
    private DestinationAdapter adapter;

    public DestinationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        destinations = ((MainActivity)getActivity()).destinations;
        adapter = new DestinationAdapter(getActivity(), destinations);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout mFrameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_destinations, container, false);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Destinations");

        mListView = mFrameLayout.findViewById(R.id.destination_list);
        mListView.setAdapter(adapter);

        mListView.setLongClickable(true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Destination selectedDestination = destinations.get(position);
                Fragment fragment = null;
                try {
                    fragment = RoutesFragment.newInstance(selectedDestination.getDestinationName(), position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainContent, fragment).addToBackStack(null).commit();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int position, long id) {
                final int selectedPosition = position;
                final Destination selectedDestination = destinations.get(selectedPosition);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                alertDialogBuilder.setTitle("Delete Destination");

                alertDialogBuilder
                        .setMessage("Are you sure you want to delete " + selectedDestination.getDestinationName() + " and all associated routes?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mListener.deleteDestination(selectedDestination.getDestinationName());
                                destinations.remove(selectedPosition);
                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        return mFrameLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDestinationsFragmentInteractionListener) {
            mListener = (OnDestinationsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFindRoutesFragmentInteractionListener");
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
    public interface OnDestinationsFragmentInteractionListener {
        void deleteDestination(String destinationName);
    }
}
