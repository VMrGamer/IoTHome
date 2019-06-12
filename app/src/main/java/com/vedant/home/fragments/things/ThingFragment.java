package com.vedant.home.fragments.things;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vedant.home.R;
import com.vedant.home.fragments.things.Contents.Thing;
import com.vedant.home.helpers.AppData;

import java.util.ArrayList;
import java.util.HashMap;

public class ThingFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_ID = "id";
    private int mColumnCount = 2;
    private String mID = "ROOMS";
    private OnListFragmentInteractionListener mListener;

    public ThingFragment() {
    }

    public static ThingFragment newInstance(int columnCount) {
        ThingFragment fragment = new ThingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static ThingFragment newInstance(int columnCount, String ID) {
        ThingFragment fragment = new ThingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_ID, ID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            if(getArguments().containsKey(ARG_ID)){
                mID = getArguments().getString(ARG_ID);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thing_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //Contents contents = new Contents();
            Contents.THINGS = new ArrayList<>();
            Contents.THING_MAP = new HashMap<>();
            if(mID.equals(AppData.ROOM_ID)) {
                for (String s : AppData.APPLIANCES) {
                    Contents.addThing(new Thing(
                            Contents.nameToTopicMapper(s),
                            s.replaceAll("[^A-Za-z0-9]", ""),
                            "Tap to toggle",
                            AppData.STATES.get(Contents.nameToTopicMapper(s.replaceAll("[^A-Za-z0-9]", ""))),
                            AppData.STATE_CHANGE.get(Contents.nameToTopicMapper(s.replaceAll("[^A-Za-z0-9]", ""))),
                            Contents.nameToTopicMapper(s.replaceAll("[^A-Za-z0-9]", ""))
                    ));
                }
            } else if(mID.equals(AppData.ROOMS_KEY)){
                for (String s : AppData.ROOMS){
                    Contents.addThing(new Thing(s.trim(),s,"Tap to change room", true, false, "null"));
                }
            }
            recyclerView.setAdapter(new ThingRecyclerViewAdapter(Contents.THINGS, mListener));


        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Thing item);
    }
}
