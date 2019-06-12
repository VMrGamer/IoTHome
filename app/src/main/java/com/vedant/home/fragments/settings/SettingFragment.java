package com.vedant.home.fragments.settings;

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
import com.vedant.home.fragments.settings.Contents.Item;
import com.vedant.home.helpers.AppData;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public SettingFragment() {
    }

    public static SettingFragment newInstance(int columnCount) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            Contents.ITEMS = new ArrayList<>();
            Contents.ITEM_MAP = new HashMap<>();
            Contents.addItem(new Item(AppData.CLIENT_ID_KEY,"Client ID", "Current Value: "+ AppData.CLIENT_ID +".Tap to edit"));
            Contents.addItem(new Item(AppData.MQTT_BROKER_URL_KEY,"Broker URL", "Current Value: "+ AppData.MQTT_BROKER_URL +".Tap to edit"));
            Contents.addItem(new Item(AppData.MQTT_USERNAME_KEY,"Username", "Current Value: "+ AppData.MQTT_USERNAME +".Tap to edit"));
            Contents.addItem(new Item(AppData.MQTT_PASSWORD_KEY,"Password", "Current Value: "+ AppData.MQTT_PASSWORD +".Tap to edit"));
            recyclerView.setAdapter(new SettingRecyclerViewAdapter(Contents.ITEMS, mListener));
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(Item item);
    }
}
