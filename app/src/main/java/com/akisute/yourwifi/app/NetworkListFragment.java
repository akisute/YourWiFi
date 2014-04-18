package com.akisute.yourwifi.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.akisute.yourwifi.app.model.NetworkListAdapter;
import com.akisute.yourwifi.app.util.GlobalEventBus;

public class NetworkListFragment extends Fragment {

    private NetworkListAdapter mAdapter;

    public NetworkListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new NetworkListAdapter(activity);
        GlobalEventBus.getInstance().register(mAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        GlobalEventBus.getInstance().unregister(mAdapter);
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
