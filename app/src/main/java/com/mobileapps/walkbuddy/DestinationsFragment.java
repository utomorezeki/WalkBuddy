package com.mobileapps.walkbuddy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileapps.walkbuddy.models.Destination;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for viewing a user's destinations and accessing their routes.
 */
public class DestinationsFragment extends Fragment {
    private static final String ARG_DESTINATIONS = "destinations";

    private OnDestinationsFragmentInteractionListener mListener;

    private ListView mListView;
    private List<Destination> destinations;
    private DestinationAdapter adapter;

    public DestinationsFragment() {
        // Required empty public constructor
    }

    public static DestinationsFragment newInstance(List<Destination> destinations) {
        DestinationsFragment fragment = new DestinationsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DESTINATIONS, (ArrayList) destinations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            destinations = (ArrayList<Destination>) getArguments().getSerializable(ARG_DESTINATIONS);
        }
        adapter = new DestinationAdapter(getActivity(), destinations);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout mFrameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_destinations, container, false);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Destinations");

        TextView noRoutes = mFrameLayout.findViewById(R.id.no_routes_message);

        mListView = mFrameLayout.findViewById(R.id.destination_list);
        mListView.setAdapter(adapter);

        if(destinations.size() > 0) {
            noRoutes.setVisibility(View.GONE);
        } else {
            noRoutes.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }

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
     */
    public interface OnDestinationsFragmentInteractionListener {
    }
}
