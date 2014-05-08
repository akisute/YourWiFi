package com.akisute.yourwifi.app;

import android.app.Activity;
import android.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.akisute.yourwifi.app.model.NetworkListAdapter;

public class NetworkListFragment extends Fragment {

    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            String appName = getResources().getString(R.string.app_name);
            getActivity().setTitle(getResources().getQuantityString(R.plurals.activity_main_title, mAdapter.getCount(), appName, mAdapter.getCount()));
        }
    };
    private NetworkListAdapter mAdapter;

    public NetworkListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new NetworkListAdapter(activity);
        mAdapter.registerToEventBus();
        mAdapter.registerDataSetObserver(mDataSetObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter.unregisterDataSetObserver(mDataSetObserver);
        mAdapter.unregisterFromEventBus();
        mAdapter = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network_list, container, false);

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(mAdapter);

        return view;
    }
}
